package com.wwd.modules.product.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.product.entity.BrandEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface BrandDao extends BaseDao<BrandEntity> {
	
}