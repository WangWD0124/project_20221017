package com.wwd.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.product.dao.SkuSaleAttrValueDao;
import com.wwd.modules.product.dto.SkuSaleAttrValueDTO;
import com.wwd.modules.product.entity.SkuSaleAttrValueEntity;
import com.wwd.modules.product.service.SkuSaleAttrValueService;
import com.wwd.modules.product.vo.SkuItemVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class SkuSaleAttrValueServiceImpl extends CrudServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity, SkuSaleAttrValueDTO> implements SkuSaleAttrValueService {

    @Override
    public QueryWrapper<SkuSaleAttrValueEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<SkuSaleAttrValueEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<SkuItemVo.SkuItemSaleAttrVo> getSkuSaleAttrVoBySpuId(Long spu_id) {
        return baseDao.getSkuSaleAttrVoBySpuId(spu_id);
    }
}