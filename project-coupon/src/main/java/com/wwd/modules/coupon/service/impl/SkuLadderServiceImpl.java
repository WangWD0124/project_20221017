package com.wwd.modules.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.feign.dto.coupon.SkuLadderDTO;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.coupon.dao.SkuLadderDao;
import com.wwd.modules.coupon.entity.SkuLadderEntity;
import com.wwd.modules.coupon.service.SkuLadderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class SkuLadderServiceImpl extends CrudServiceImpl<SkuLadderDao, SkuLadderEntity, SkuLadderDTO> implements SkuLadderService {

    @Override
    public QueryWrapper<SkuLadderEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<SkuLadderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}