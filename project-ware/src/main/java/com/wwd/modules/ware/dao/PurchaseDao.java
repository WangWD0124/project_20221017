package com.wwd.modules.ware.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.ware.entity.PurchaseEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface PurchaseDao extends BaseDao<PurchaseEntity> {
	
}