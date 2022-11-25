package com.wwd.modules.product.service;

import com.wwd.common.page.PageData;
import com.wwd.common.service.CrudService;
import com.wwd.modules.product.dto.SpuInfoDTO;
import com.wwd.modules.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface SpuInfoService extends CrudService<SpuInfoEntity, SpuInfoDTO> {

    PageData<SpuInfoDTO> search(Map<String, Object> params);

    void updatePublishStatusById(Long id);
}