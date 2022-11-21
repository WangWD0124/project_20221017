package com.wwd.modules.member.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.member.entity.UndoLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface UndoLogDao extends BaseDao<UndoLogEntity> {
	
}