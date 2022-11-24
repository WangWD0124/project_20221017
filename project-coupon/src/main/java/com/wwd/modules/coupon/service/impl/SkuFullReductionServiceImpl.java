package com.wwd.modules.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.feign.dto.coupon.SkuFullReductionDTO;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.coupon.dao.SkuFullReductionDao;
import com.wwd.modules.coupon.entity.SkuFullReductionEntity;
import com.wwd.modules.coupon.service.SkuFullReductionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class SkuFullReductionServiceImpl extends CrudServiceImpl<SkuFullReductionDao, SkuFullReductionEntity, SkuFullReductionDTO> implements SkuFullReductionService {

    @Override
    public QueryWrapper<SkuFullReductionEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<SkuFullReductionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}