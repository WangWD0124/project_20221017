package com.wwd.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.common.utils.ConvertUtils;
import com.wwd.modules.member.dao.MemberLevelDao;
import com.wwd.modules.member.dto.MemberLevelDTO;
import com.wwd.modules.member.entity.MemberLevelEntity;
import com.wwd.modules.member.service.MemberLevelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 会员等级
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class MemberLevelServiceImpl extends CrudServiceImpl<MemberLevelDao, MemberLevelEntity, MemberLevelDTO> implements MemberLevelService {

    @Override
    public QueryWrapper<MemberLevelEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<MemberLevelEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<MemberLevelDTO> list() {
        return ConvertUtils.sourceToTarget(baseDao.selectList(null), MemberLevelDTO.class);
    }
}