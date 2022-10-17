package com.wwd.modules.coupon.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.coupon.entity.HomeAdvEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 首页轮播广告
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface HomeAdvDao extends BaseDao<HomeAdvEntity> {
	
}