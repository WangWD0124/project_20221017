package com.wwd.modules.member.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.member.dto.MemberLevelDTO;
import com.wwd.modules.member.entity.MemberLevelEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员等级
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface MemberLevelService extends CrudService<MemberLevelEntity, MemberLevelDTO> {

    List<MemberLevelDTO> list();

    Long getLevelIdByDefaultStatus();
}