package com.wwd.modules.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.page.PageData;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.ware.dao.PurchaseDetailDao;
import com.wwd.modules.ware.dto.PurchaseDetailDTO;
import com.wwd.modules.ware.entity.PurchaseDetailEntity;
import com.wwd.modules.ware.service.PurchaseDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class PurchaseDetailServiceImpl extends CrudServiceImpl<PurchaseDetailDao, PurchaseDetailEntity, PurchaseDetailDTO> implements PurchaseDetailService {

    @Override
    public QueryWrapper<PurchaseDetailEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public PageData<PurchaseDetailDTO> search(Map<String, Object> params) {
        String wareId = (String) params.get("wareId");
        String status = (String) params.get("status");
        String key = (String) params.get("key");

        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();

        if (!wareId.equals("0")){
            wrapper.eq(PurchaseDetailEntity::getWareId, wareId);
        }
        if (status != null && !status.equals("")){
            wrapper.eq(PurchaseDetailEntity::getStatus, status);
        }
        if (key != null && !key.equals("")){
            wrapper.and(i -> i.eq(PurchaseDetailEntity::getSkuId, key)
                    .or().like(PurchaseDetailEntity::getSkuNum, key));
        }
        IPage<PurchaseDetailEntity> page = baseDao.selectPage(getPage(params, null, true), wrapper);
        return getPageData(page, currentDtoClass());
    }

    @Override
    public void merge(Map<String, Object> params) {

        List<Long> ids = (List<Long>) params.get("ids");
        Long purchaseId = (Long) params.get("purchaseId");
        List<PurchaseDetailEntity> list = ids.stream().map(id -> {
                    PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                    purchaseDetailEntity.setId(id);
                    purchaseDetailEntity.setPurchaseId(purchaseId);
                    return purchaseDetailEntity;
                }).collect(Collectors.toList());
        this.updateBatchById(list);//批量修改
    }
}