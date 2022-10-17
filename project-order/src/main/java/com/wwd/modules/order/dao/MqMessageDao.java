package com.wwd.modules.order.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.order.entity.MqMessageEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface MqMessageDao extends BaseDao<MqMessageEntity> {
	
}