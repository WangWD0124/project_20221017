package com.wwd.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wwd.common.constant.OrderConstant;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.common.utils.Result;
import com.wwd.modules.order.dao.OrderDao;
import com.wwd.modules.order.dto.OrderDTO;
import com.wwd.modules.order.entity.OrderEntity;
import com.wwd.modules.order.feign.CartServerFeign;
import com.wwd.modules.order.feign.MemberServerFeign;
import com.wwd.modules.order.feign.WareServerFeign;
import com.wwd.modules.order.interceptor.LoginUserInterceptor;
import com.wwd.modules.order.service.OrderService;
import com.wwd.modules.order.vo.*;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
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
    private CartServerFeign cartServerFeign;

    @Autowired
    private WareServerFeign wareServerFeign;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
            //创建订单数据
            OrderDTO orderDTO = createOrder();
            orderSubmitResponseVo.setOrder(orderDTO);
        }
        return orderSubmitResponseVo;
    }

    /**
     * 创建订单
     * @return
     */
    private OrderDTO createOrder() {

        OrderSubmitVo orderSubmitVo = submitVoThreadLocal.get();
        OrderDTO orderDTO = new OrderDTO();
        //1、生成订单号
        String orderSn = IdWorker.getTimeId();
        orderDTO.setOrderSn(orderSn);
        //2、

//        @ApiModelProperty(value = "使用的优惠券")
//        private Long couponId;
//
//        @ApiModelProperty(value = "create_time")
//        private Date createTime;
//
//        @ApiModelProperty(value = "用户名")
//        private String memberUsername;
//
//        @ApiModelProperty(value = "订单总额")
//        private BigDecimal totalAmount;
//
//        @ApiModelProperty(value = "应付总额")
//        private BigDecimal payAmount;
//
//        @ApiModelProperty(value = "运费金额")
//        private BigDecimal freightAmount;
//
//        @ApiModelProperty(value = "促销优化金额（促销价、满减、阶梯价）")
//        private BigDecimal promotionAmount;
//
//        @ApiModelProperty(value = "积分抵扣金额")
//        private BigDecimal integrationAmount;
//
//        @ApiModelProperty(value = "优惠券抵扣金额")
//        private BigDecimal couponAmount;
//
//        @ApiModelProperty(value = "后台调整订单使用的折扣金额")
//        private BigDecimal discountAmount;
//
//        @ApiModelProperty(value = "支付方式【1->支付宝；2->微信；3->银联； 4->货到付款；】")
//        private Integer payType;
//
//        @ApiModelProperty(value = "订单来源[0->PC订单；1->app订单]")
//        private Integer sourceType;
//
//        @ApiModelProperty(value = "订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】")
//        private Integer status;
//
//        @ApiModelProperty(value = "物流公司(配送方式)")
//        private String deliveryCompany;
//
//        @ApiModelProperty(value = "物流单号")
//        private String deliverySn;
//
//        @ApiModelProperty(value = "自动确认时间（天）")
//        private Integer autoConfirmDay;
//
//        @ApiModelProperty(value = "可以获得的积分")
//        private Integer integration;
//
//        @ApiModelProperty(value = "可以获得的成长值")
//        private Integer growth;
//
//        @ApiModelProperty(value = "发票类型[0->不开发票；1->电子发票；2->纸质发票]")
//        private Integer billType;
//
//        @ApiModelProperty(value = "发票抬头")
//        private String billHeader;
//
//        @ApiModelProperty(value = "发票内容")
//        private String billContent;
//
//        @ApiModelProperty(value = "收票人电话")
//        private String billReceiverPhone;
//
//        @ApiModelProperty(value = "收票人邮箱")
//        private String billReceiverEmail;
//
//        @ApiModelProperty(value = "收货人姓名")
//        private String receiverName;
//
//        @ApiModelProperty(value = "收货人电话")
//        private String receiverPhone;
//
//        @ApiModelProperty(value = "收货人邮编")
//        private String receiverPostCode;

        //省份/直辖市
        String receiverProvince;

        //城市
        String receiverCity;

        //区
        String receiverRegion;

        //详细地址
        String receiverDetailAddress;

        //订单备注
        String note;

        //确认收货状态[0->未确认；1->已确认]
        Integer confirmStatus;

        //删除状态【0->未删除；1->已删除】
        Integer deleteStatus;

        //下单时使用的积分
        Integer useIntegration;



        return orderDTO;
    }

}























