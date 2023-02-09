package com.wwd.modules.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.feign.dto.ware.SkuHasStockVo;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.ware.dao.WareSkuDao;
import com.wwd.modules.ware.dto.WareSkuDTO;
import com.wwd.modules.ware.entity.WareSkuEntity;
import com.wwd.modules.ware.exception.NotStockException;
import com.wwd.modules.ware.service.WareSkuService;
import com.wwd.modules.ware.vo.LockStockResult;
import com.wwd.modules.ware.vo.OrderItem;
import com.wwd.modules.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareIds;
    }
    /**
     * 订单锁定库存
     * @param wareSkuLockVo
     * @return
     */
    @Transactional//TODO 事务管理，发生异常数据操作全部回滚
    @Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) throws NotStockException {
        String orderSn = wareSkuLockVo.getOrderSn();
        List<OrderItem> locks = wareSkuLockVo.getLocks();

        //遍历查询每个商品的有库存仓库
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
                throw new NotStockException(skuId);
            } else {
                for (Long wareId : wareIds) {
                    Long count = wareSkuDao.orderLockStock(wareId, skuId, num);
                    if (count == 1){
                        //锁住
                        shocklocked = true;
                        break;
                    } else {
                        shocklocked = false;
                    }
                }
                if (!shocklocked){
                    //没有库存，无法锁住
                    throw new NotStockException(skuId);
                }
            }
        }
        //库存锁定成功
        return true;
    }

    @Override
    public List<Long> getWareIdsHasStickBySkuId(Long skuId) {

        return null;
    }
}