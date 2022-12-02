package com.wwd.modules.product.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.product.dto.ProductAttrValueDTO;
import com.wwd.modules.product.entity.ProductAttrValueEntity;

import java.util.List;

/**
 * spu属性值
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface ProductAttrValueService extends CrudService<ProductAttrValueEntity, ProductAttrValueDTO> {

    List<ProductAttrValueDTO> getBySpuId(Long id);

    List<ProductAttrValueDTO> getBaseAttrEnableSearchBySpuId(Long spuId);

    void updateValue(List<ProductAttrValueDTO> sourceToTarget, Long spuId);
}