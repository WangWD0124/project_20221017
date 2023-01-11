package com.wwd.modules.product.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.product.dto.SkuSaleAttrValueDTO;
import com.wwd.modules.product.entity.SkuSaleAttrValueEntity;
import com.wwd.modules.product.vo.SkuItemVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface SkuSaleAttrValueService extends CrudService<SkuSaleAttrValueEntity, SkuSaleAttrValueDTO> {

    List<SkuItemVo.SkuItemSaleAttrVo> getSkuSaleAttrVoBySpuId(Long spu_id);

}