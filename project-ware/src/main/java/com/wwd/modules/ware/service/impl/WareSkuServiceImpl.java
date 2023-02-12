package com.wwd.modules.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.feign.dto.ware.SkuHasStockVo;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.ware.dao.WareSkuDao;
import com.wwd.modules.ware.dto.WareOrderTaskDTO;
import com.wwd.modules.ware.dto.WareOrderTaskDetailDTO;
import com.wwd.modules.ware.dto.WareSkuDTO;
import com.wwd.modules.ware.entity.WareOrderTaskDetailEntity;
import com.wwd.modules.ware.entity.WareSkuEntity;
import com.wwd.modules.ware.exception.NotStockException;
import com.wwd.modules.ware.feign.OrderServerFeign;
import com.wwd.modules.ware.service.WareOrderTaskDetailService;
import com.wwd.modules.ware.service.WareOrderTaskService;
import com.wwd.modules.ware.service.WareSkuService;
import com.wwd.modules.ware.vo.LockStockResult;
import com.wwd.modules.ware.vo.OrderItem;
import com.wwd.modules.ware.vo.StockLockedToMQVo;
import com.wwd.modules.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品库存
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class WareSkuServiceImpl extends CrudServiceImpl<WareSkuDao, WareSkuEntity, WareSkuDTO> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Autowired
    OrderServerFeign orderServerFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public QueryWrapper<WareSkuEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public WareSkuDTO getStockById(Long id) {

        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<WareSkuEntity>();
        wrapper.eq(WareSkuEntity::getId, id);
        return null;
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStockVoByIds(List<Long> skuIds) {

        List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            skuHasStockVo.setSkuId(skuId);
            Long stock = baseDao.getSkuHasStockVoBySkuId(skuId);
            skuHasStockVo.setStock(stock==null?0:stock);
            return skuHasStockVo;
        }).collect(Collectors.toList());

        return skuHasStockVos;
    }

    /**
     * 查询商品库存有货仓库列表
     * @param skuId
     * @return
     */
    @Override
    public List<Long> getWareIdsHasStickBySkuId(Long skuId) {
        return baseDao.getWareIdsHasStickBySkuId(skuId);
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareIds;
    }
    /**
     * 锁定库存
     * @param wareSkuLockVo
     * @return
     */
    @Transactional//TODO 事务管理，发生异常数据操作全部回滚
    @Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) throws NotStockException {
        String orderSn = wareSkuLockVo.getOrderSn();
        List<OrderItem> locks = wareSkuLockVo.getLocks();

        //记录订单库存锁定保存工作单
        WareOrderTaskDTO wareOrderTaskDTO = new WareOrderTaskDTO();
        wareOrderTaskDTO.setOrderSn(orderSn);
        wareOrderTaskService.save(wareOrderTaskDTO);

        //遍历查询每个商品的有库存仓库列表
        List<SkuWareHasStock> wareHasStockList = locks.stream().map(orderItem -> {
            SkuWareHasStock wareHasStock = new SkuWareHasStock();
            Long skuId = orderItem.getSkuId();
            wareHasStock.setSkuId(skuId);
            wareHasStock.setNum(orderItem.getCount());
            List<Long> wareIds = getWareIdsHasStickBySkuId(skuId);
            wareHasStock.setWareIds(wareIds);
            return wareHasStock;
        }).collect(Collectors.toList());

        //锁定库存
        for (SkuWareHasStock skuWareHasStock : wareHasStockList) {
            Boolean shocklocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            Integer num = skuWareHasStock.getNum();
            List<Long> wareIds = skuWareHasStock.getWareIds();
            if (wareIds != null || wareIds.size() == 0){
                //没有仓库有库存
                throw new NotStockException(skuId);//抛出无库存异常
            } else {
                for (Long wareId : wareIds) {
                    Long count = wareSkuDao.orderLockStock(wareId, skuId, num);//锁定库存
                    if (count == 1){
                        //锁住
                        shocklocked = true;
                        //记录到库存锁定保存工作单单项
                        WareOrderTaskDetailDTO wareOrderTaskDetailDTO = new WareOrderTaskDetailDTO(null, skuId, null, num, wareOrderTaskDTO.getId(), wareId, null);
                        wareOrderTaskDetailService.save(wareOrderTaskDetailDTO);

                        break;
                    } else {
                        shocklocked = false;
                    }
                }
                if (!shocklocked){
                    throw new NotStockException(skuId);//抛出无库存异常
                }
            }
        }
        //发送锁定成功消息到MQ
        StockLockedToMQVo stockLockedToMQVo = new StockLockedToMQVo();
        stockLockedToMQVo.setTaskId(wareOrderTaskDTO.getId());
        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedToMQVo);
        //库存锁定成功
        return true;
    }


    /**
     * 解锁库存
     * @param stockLockedToMQVo
     */
    @Transactional
    @Override
    public void StockRelease(StockLockedToMQVo stockLockedToMQVo) {

        //【解锁库存】：
        //库存服务异常（工作单不存在），本服务自动回滚解锁，无需处理
        //库存服务正常（工作单存在），需获取解锁消息，根据订单状态，分情况手动解锁
        Long taskId = stockLockedToMQVo.getTaskId();
        WareOrderTaskDTO wareOrderTaskDTO = wareOrderTaskService.get(taskId);
        if (wareOrderTaskDTO != null){
            //根据订单号，远程获取订单状态
            String orderSn = wareOrderTaskDTO.getOrderSn();
            Integer status = orderServerFeign.getStatusByOrderSn(orderSn).getData();
            //【订单状态】：
            //订单服务异常回滚查不到订单、订单待付款-0、订单已关闭-4：解锁库存，实际库存不变
            //订单状态正常（1->待发货；2->已发货；3->已完成）：解锁库存，减去实际库存
            List<WareOrderTaskDetailEntity> taskDetailEntities = wareOrderTaskDetailService.getByTaskId(taskId);
            if (status == null && status == 0 && status == 4){
                for (WareOrderTaskDetailEntity taskDetailEntity : taskDetailEntities) {
                    Long wareId = taskDetailEntity.getWareId();
                    Long skuId = taskDetailEntity.getSkuId();
                    Integer num = taskDetailEntity.getSkuNum();
                    baseDao.orderReleaseStockWithNo(wareId, skuId, num, taskId);
                }
                if (status == 0){
                    //远程将订单状态从0-待付款修改为4-已关闭
                    int code = orderServerFeign.updateStatusByOrderSn(orderSn, 4).getCode();
                    if(code == 1){
                        throw new RuntimeException();//订单关闭失败
                    }
                }
            } else {
                for (WareOrderTaskDetailEntity taskDetailEntity : taskDetailEntities) {
                    Long wareId = taskDetailEntity.getWareId();
                    Long skuId = taskDetailEntity.getSkuId();
                    Integer num = taskDetailEntity.getSkuNum();
                    baseDao.orderReleaseStockWithOk(wareId, skuId, num, taskId);
                }
            }
        }
    }

}