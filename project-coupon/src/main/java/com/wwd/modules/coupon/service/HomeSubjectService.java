package com.wwd.modules.coupon.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.coupon.dto.HomeSubjectDTO;
import com.wwd.modules.coupon.entity.HomeSubjectEntity;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface HomeSubjectService extends CrudService<HomeSubjectEntity, HomeSubjectDTO> {

}