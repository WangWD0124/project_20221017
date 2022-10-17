package com.wwd.modules.ware.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.ware.entity.WareOrderTaskEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存工作单
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface WareOrderTaskDao extends BaseDao<WareOrderTaskEntity> {
	
}