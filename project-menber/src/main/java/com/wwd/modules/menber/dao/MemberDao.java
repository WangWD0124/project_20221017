package com.wwd.modules.menber.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.modules.menber.entity.MemberEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface MemberDao extends BaseDao<MemberEntity> {
	
}