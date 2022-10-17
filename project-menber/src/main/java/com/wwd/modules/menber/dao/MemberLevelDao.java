package com.wwd.modules.menber.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.menber.entity.MemberLevelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface MemberLevelDao extends BaseDao<MemberLevelEntity> {
	
}