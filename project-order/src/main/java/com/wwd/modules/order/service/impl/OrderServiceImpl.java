package com.wwd.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wwd.common.constant.OrderConstant;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.common.utils.ConvertUtils;
import com.wwd.common.utils.Result;
import com.wwd.modules.order.dao.OrderDao;
import com.wwd.modules.order.dto.OrderDTO;
import com.wwd.modules.order.dto.OrderItemDTO;
import com.wwd.modules.order.entity.OrderEntity;
import com.wwd.modules.order.entity.OrderItemEntity;
import com.wwd.modules.order.enume.OrderStatusEnum;
import com.wwd.modules.order.feign.CartServerFeign;
import com.wwd.modules.order.feign.MemberServerFeign;
import com.wwd.modules.order.feign.ProductServerFeign;
import com.wwd.modules.order.feign.WareServerFeign;
import com.wwd.modules.order.interceptor.LoginUserInterceptor;
import com.wwd.modules.order.service.OrderItemService;
import com.wwd.modules.order.service.OrderService;
import com.wwd.modules.order.vo.*;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class OrderServiceImpl extends CrudServiceImpl<OrderDao, OrderEntity, OrderDTO> implements OrderService {

    public static ThreadLocal<OrderSubmitVo> submitVoThreadLocal = new ThreadLocal<>();
    @Autowired
    private MemberServerFeign memberServerFeign;

    @Autowired
    private ProductServerFeign productServerFeign;

    @Autowired
    private CartServerFeign cartServerFeign;

    @Autowired
    private WareServerFeign wareServerFeign;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OrderItemService orderItemService;

    @Override
    public QueryWrapper<OrderEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    /**
     * 订单确认页
     * @return
     */
    @Override
    public OrderConfirmVo orderConfirm() throws ExecutionException, InterruptedException {

        OrderConfirmVo confirmOrder = new OrderConfirmVo();
        //用户
        MemberDTO memberDTO = LoginUserInterceptor.threadLocal.get();
        Long memberId = memberDTO.getId();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//TODO RequestContextHolder存在于ThreadLocal，
        /**
         * 查询用户收货地址列表
         */
        CompletableFuture<Void> addressListCompletableFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);//TODO 异步请求线程绑定原请求参数
            List<MemberReceiveAddressDTO> addressDTOList = memberServerFeign.getAddressListByMemberId(memberId).getData();
            confirmOrder.setMemberAddressVos(addressDTOList);
        }, executor);

        /**
         * 订单购物项
         */
        CompletableFuture<Void> itemsCompletableFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItem> items = cartServerFeign.getOrderItemsByMemberId(memberId).getData();
            confirmOrder.setItems(items);
        }, executor).thenRunAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItem> items = confirmOrder.getItems();
            List<Long> skuIds = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            Result<List<SkuHasStockVo>> skuHasStockVo = wareServerFeign.getSkuHasStockVoByIds(skuIds);
            List<SkuHasStockVo> skuHasStockVoData = skuHasStockVo.getData();
            if (skuHasStockVoData != null){
                Map<Long, Long> collect = skuHasStockVoData.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getStock));
                confirmOrder.setStocks(collect);
            }
        }, executor);

        /**
         * TODO 订单确认令牌（防止重复确认） , 幂等性
         */
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberId, token, 30, TimeUnit.MINUTES);
        confirmOrder.setOrderToken(token);

        CompletableFuture.allOf(addressListCompletableFuture, itemsCompletableFuture).get();
        return confirmOrder;
    }

    /**
     * 提交订单
     * @param orderSubmitVo
     * @return
     */
    //TODO 分布式事务@GlobalTransactional
    @Transactional
    @Override
    public OrderSubmitResponseVo orderSubmit(OrderSubmitVo orderSubmitVo) {

        submitVoThreadLocal.set(orderSubmitVo);
        OrderSubmitResponseVo orderSubmitResponseVo = new OrderSubmitResponseVo();
        MemberDTO memberDTO = LoginUserInterceptor.threadLocal.get();
        String orderToken = orderSubmitVo.getOrderToken();
        //String redisOrderToken = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberDTO.getId());
        //TODO　执行lua脚本原子对比并删除订单令牌，保证原子性，实现幂等性
        String script = "if redis.call('get', KEYS[1]) = ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberDTO.getId(), orderToken));
        //以下代码不具备原子性
        //        if (orderToken.equals(redisOrderToken)){
        //            //删除订单令牌
        //            redisTemplate.delete(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberDTO.getId());
        //            //创建订单数据
        //        }
        if (result == 0L){
            orderSubmitResponseVo.setCode(1);//失败
        } else {
            orderSubmitResponseVo.setCode(0);//成功
            //1、创建订单数据
            OrderCreateVo orderCreateVo = createOrder();
            //2、验价
            if (orderSubmitVo.getPayPrice().compareTo(orderCreateVo.getPayPrice()) == 0) {
                //价格依然相等->3、保存订单信息
                saveOrder(orderCreateVo);
            } else {
                //价格发生变化
                orderSubmitResponseVo.setCode(2);//失败
                return orderSubmitResponseVo;
            }
            //4、锁定库存
            WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
            wareSkuLockVo.setOrderSn(orderCreateVo.getOrder().getOrderSn());
            List<OrderItem> orderItems = orderCreateVo.getOrderItemDTOS().stream().map(orderItemDTO -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setSkuId(orderItemDTO.getSkuId());
                orderItem.setTitle(orderItemDTO.getSkuName());
                orderItem.setCount(orderItemDTO.getSkuQuantity());
                return orderItem;
            }).collect(Collectors.toList());
            wareSkuLockVo.setLocks(orderItems);
            int code = wareServerFeign.orderLockStock(wareSkuLockVo).getCode();//TODO 分布式事务远程调用要通过返回值获取执行结果
            if (code != 1){
                //库存锁定失败
                orderSubmitResponseVo.setCode(3);//失败
                throw new RuntimeException();//远程库存锁定失败，抛出运行时异常，回滚订单记录
            }
            orderSubmitResponseVo.setOrder(orderCreateVo);
        }
        return orderSubmitResponseVo;
    }

    /**
     * 创建订单返回页面数据
     * @return
     */
    private OrderCreateVo createOrder() {

        OrderSubmitVo orderSubmitVo = submitVoThreadLocal.get();

        OrderCreateVo orderCreateVo = new OrderCreateVo();
        //生成订单号
        String orderSn = IdWorker.getTimeId();
        //构建订单基本信息
        OrderDTO orderDTO = bulidOrder(orderSn);
        orderCreateVo.setOrder(orderDTO);
        //构建所有订单项
        MemberDTO memberDTO = LoginUserInterceptor.threadLocal.get();
        List<OrderItemDTO> orderItemDTOS = cartServerFeign.getOrderItemsByMemberId(memberDTO.getId()).getData().stream().map(orderItem -> {
            OrderItemDTO orderItemDTO = buildOrderItemDTO(orderItem);
            orderItemDTO.setOrderSn(orderSn);
            return orderItemDTO;
        }).collect(Collectors.toList());
        orderCreateVo.setOrderItemDTOS(orderItemDTOS);
        orderCreateVo.setFare(orderDTO.getFreightAmount());
        //计算应付总额
        computePayPrice(orderDTO, orderItemDTOS);
        orderCreateVo.setPayPrice(orderDTO.getPayAmount());
        return orderCreateVo;
    }

    /**
     * 保存订单、订单项
     * @param orderCreateVo
     */
    private void saveOrder(OrderCreateVo orderCreateVo) {

        //保存订单
        OrderDTO order = orderCreateVo.getOrder();
        baseDao.insert(ConvertUtils.sourceToTarget(order, OrderEntity.class));
        //保存订单项
        List<OrderItemDTO> orderItemDTOS = orderCreateVo.getOrderItemDTOS();
        for (OrderItemDTO orderItemDTO : orderItemDTOS) {
            orderItemDTO.setOrderId(order.getId());
        }
        orderItemService.insertBatch(ConvertUtils.sourceToTarget(orderItemDTOS, OrderItemEntity.class));

    }

    /**
     * 所有订单项求和
     */
    private void computePayPrice(OrderDTO orderDTO, List<OrderItemDTO> orderItemDTOS) {

        BigDecimal totalAmount = new BigDecimal("0.0");

        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        BigDecimal payAmount = new BigDecimal("0.0");

        Integer giftIntegration = new Integer("0");
        Integer giftGrowth = new Integer("0");
        for (OrderItemDTO orderItemDTO : orderItemDTOS) {
            coupon = coupon.add(orderItemDTO.getCouponAmount());
            integration = integration.add(orderItemDTO.getIntegrationAmount());
            promotion = promotion.add(orderItemDTO.getPromotionAmount());
            totalAmount = totalAmount.add(orderItemDTO.getSkuPrice().multiply(new BigDecimal(orderItemDTO.getSkuQuantity().toString())));
            payAmount = payAmount.add(orderItemDTO.getRealAmount());
            giftIntegration = giftIntegration + orderItemDTO.getGiftIntegration();
            giftGrowth = giftGrowth + orderItemDTO.getGiftGrowth();
        }
        // 订单总额
        orderDTO.setTotalAmount(totalAmount);
        // 使用的优惠券
        //orderDTO.setCouponId();
        // 优惠券抵扣金额
        orderDTO.setCouponAmount(coupon);
        // 促销优化金额（促销价、满减、阶梯价）
        orderDTO.setPromotionAmount(promotion);
        // 后台调整订单使用的折扣金额
        orderDTO.setDiscountAmount(integration);
        // 下单时使用的积分
        //orderDTO.setUseIntegration();
        // 积分抵扣金额
        //orderDTO.setIntegrationAmount();
        // 可以获得的积分
        orderDTO.setIntegration(giftIntegration);
        // 可以获得的成长值
        orderDTO.setGrowth(giftGrowth);
        // 应付总额
        orderDTO.setPayAmount(payAmount);

    }

    /**
     * 构建订单项
     * @param orderItem
     * @return
     */
    private OrderItemDTO buildOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        /**
         * spu
         */
        SpuInfoDTO spuInfoDTO = productServerFeign.getSpuInfoBySkuId(orderItem.getSkuId()).getData();
        orderItemDTO.setSpuId(spuInfoDTO.getId());
        orderItemDTO.setSpuName(spuInfoDTO.getSpuName());
        orderItemDTO.setSpuBrand(spuInfoDTO.getBrandId().toString());
        orderItemDTO.setCategoryId(spuInfoDTO.getCatalogId());
        /**
         * sku
         */
        orderItemDTO.setSkuId(orderItem.getSkuId());
        orderItemDTO.setSkuName(orderItem.getTitle());
        orderItemDTO.setSkuPic(orderItem.getImage());
        orderItemDTO.setSkuAttrsVals(org.springframework.util.StringUtils.collectionToDelimitedString(orderItem.getSkuAttr(), ";"));
        orderItemDTO.setSkuQuantity(orderItem.getCount());
        orderItemDTO.setSkuPrice(orderItem.getPrice());
        /**
         * 优惠（暂无）
         */
        orderItemDTO.setCouponAmount(new BigDecimal("0.0"));
        orderItemDTO.setPromotionAmount(new BigDecimal("0.0"));
        orderItemDTO.setIntegrationAmount(new BigDecimal("0.0"));
        /**
         * 积分（暂无）
         */
        orderItemDTO.setGiftGrowth(orderItem.getPrice().intValue());
        orderItemDTO.setGiftIntegration(orderItem.getPrice().intValue());
        /**
         * 项小计金额
         */
        BigDecimal orign = orderItemDTO.getSkuPrice().multiply(new BigDecimal(orderItemDTO.getSkuQuantity()));
        BigDecimal subtract = orderItemDTO.getCouponAmount().add(orderItemDTO.getPromotionAmount()).add(orderItemDTO.getIntegrationAmount());
        orderItemDTO.setRealAmount(orign.subtract(subtract));
        return orderItemDTO;
    }

    /**
     * 构建订单
     * @param orderSn
     * @return
     */
    private OrderDTO bulidOrder(String orderSn) {

        OrderSubmitVo orderSubmitVo = submitVoThreadLocal.get();
        MemberDTO memberDTO = LoginUserInterceptor.threadLocal.get();

        OrderDTO orderDTO = new OrderDTO();
        //订单号
        orderDTO.setOrderSn(orderSn);
        // 用户名
        orderDTO.setMemberUsername(memberDTO.getUsername());
        /**
         * ********收货地址*********************************************
         */
        FareVo fareVo = wareServerFeign.getFare(orderSubmitVo.getAddrId()).getData();
        MemberReceiveAddressDTO address = fareVo.getAddress();
        // 收货人姓名
        orderDTO.setReceiverName(address.getName());
        // 收货人电话
        orderDTO.setReceiverPhone(address.getPhone());
        // 收货人邮编
        orderDTO.setReceiverPostCode(address.getPostCode());
        //省份/直辖市
        orderDTO.setReceiverProvince(address.getProvince());
        //城市
        orderDTO.setReceiverCity(address.getCity());
        //区
        orderDTO.setReceiverRegion(address.getRegion());
        //详细地址
        orderDTO.setReceiverDetailAddress(address.getDetailAddress());
        /**
         * *********物流信息********************************************
         */
        // 物流公司(配送方式)
        orderDTO.setDeliveryCompany("顺丰速运");
        // 物流单号
        orderDTO.setDeliverySn(IdWorker.getTimeId());
        // 运费金额
        orderDTO.setFreightAmount(fareVo.getFarePrice());
        /**
         * *********金额信息********************************************
        // 订单总额
        //orderDTO.setTotalAmount();
        // 使用的优惠券
        orderDTO.setCouponId();
        // 优惠券抵扣金额
        orderDTO.setCouponAmount();
        // 促销优化金额（促销价、满减、阶梯价）
        orderDTO.setPromotionAmount();
        // 后台调整订单使用的折扣金额
        orderDTO.setDiscountAmount();
        // 下单时使用的积分
        orderDTO.setUseIntegration();
        // 积分抵扣金额
        orderDTO.setIntegrationAmount();
        // 可以获得的积分
        orderDTO.setIntegration();
        // 可以获得的成长值
        orderDTO.setGrowth();
        // 应付总额
        //orderDTO.setPayAmount();
        // 支付方式【1->支付宝；2->微信；3->银联； 4->货到付款；】
        orderDTO.setPayType(orderSubmitVo.getPayType()); */
        /**
         * *********状态信息********************************************
         */
        // 订单创建时间
        orderDTO.setCreateTime(new Date());
        // 订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】")
        orderDTO.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        //确认收货状态[0->未确认；1->已确认]
        orderDTO.setConfirmStatus(0);
        // 自动确认时间（天）
        orderDTO.setAutoConfirmDay(7);
        //删除状态【0->未删除；1->已删除】
        orderDTO.setDeleteStatus(0);
        /**
         * *******发票**********************************************
         // 发票类型[0->不开发票；1->电子发票；2->纸质发票]")
         orderDTO.setBillType();
         // 发票抬头")
         orderDTO.setBillHeader();
         // 发票内容
         orderDTO.setBillContent();
         // 收票人电话
         orderDTO.setBillReceiverPhone();
         // 收票人邮箱
         orderDTO.setBillReceiverEmail();
         */
        /**
         * *********其他********************************************
         */
        //订单备注
        orderDTO.setNote(orderSubmitVo.getNote());
        // 订单来源[0->PC订单；1->app订单]")
        orderDTO.setSourceType(0);

        return orderDTO;
    }


    /**
     * 查询订单状态
     * @param orderSn
     * @return
     */
    @Override
    public Integer getStatusByOrderSn(String orderSn) {

        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderEntity::getOrderSn, orderSn);
        OrderEntity orderEntity = baseDao.selectOne(wrapper);
        Integer status = null;//订单服务异常，订单回滚已不存在
        if (orderEntity != null){
            orderEntity.getStatus();
        }
        return status;
    }

    /**
     * 修改订单状态
     * @param orderSn
     * @param status
     * @return
     */
    @Override
    public Long updateStatusByOrderSn(String orderSn, Integer status) {

        Long res = baseDao.updateStatusByOrderSn(orderSn, status);
        return res;
    }
}























