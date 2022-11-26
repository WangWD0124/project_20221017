package com.wwd.modules.ware.service;

import com.wwd.common.page.PageData;
import com.wwd.common.service.CrudService;
import com.wwd.modules.ware.dto.PurchaseDetailDTO;
import com.wwd.modules.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface PurchaseDetailService extends CrudService<PurchaseDetailEntity, PurchaseDetailDTO> {

    PageData<PurchaseDetailDTO> search(Map<String, Object> params);

    void merge(Map<String, Object> params);
}