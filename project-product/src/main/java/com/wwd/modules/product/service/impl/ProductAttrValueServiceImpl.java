package com.wwd.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.common.utils.ConvertUtils;
import com.wwd.modules.product.dao.ProductAttrValueDao;
import com.wwd.modules.product.dto.ProductAttrValueDTO;
import com.wwd.modules.product.entity.ProductAttrValueEntity;
import com.wwd.modules.product.service.ProductAttrValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class ProductAttrValueServiceImpl extends CrudServiceImpl<ProductAttrValueDao, ProductAttrValueEntity, ProductAttrValueDTO> implements ProductAttrValueService {

    @Override
    public QueryWrapper<ProductAttrValueEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<ProductAttrValueEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<ProductAttrValueDTO> getBySpuId(Long spuId) {

        LambdaQueryWrapper<ProductAttrValueEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductAttrValueEntity::getSpuId, spuId);
        return ConvertUtils.sourceToTarget(baseDao.selectList(wrapper), ProductAttrValueDTO.class);
    }

    @Override
    public void updateValue(List<ProductAttrValueDTO> productAttrValueDTOList, Long spuId) {

        for (ProductAttrValueDTO productAttrValueDTO:productAttrValueDTOList){
            Long attrId = productAttrValueDTO.getAttrId();
            String attrValue = productAttrValueDTO.getAttrValue();
            Integer quickShow = productAttrValueDTO.getQuickShow();
            baseDao.updateValue(attrValue, quickShow,spuId, attrId);
        }
    }
}