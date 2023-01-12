package com.wwd.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.member.dao.MemberDao;
import com.wwd.modules.member.dto.MemberDTO;
import com.wwd.modules.member.dto.UserRegistDTO;
import com.wwd.modules.member.entity.MemberEntity;
import com.wwd.modules.member.service.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 会员
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class MemberServiceImpl extends CrudServiceImpl<MemberDao, MemberEntity, MemberDTO> implements MemberService {

    @Override
    public QueryWrapper<MemberEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public void register(UserRegistDTO userRegistDTO) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername(userRegistDTO.getUserName());//用户名
        memberEntity.setPassword(userRegistDTO.getPassword());//密码  //TODO 待加密
        memberEntity.setMobile(userRegistDTO.getPhone());//手机号码
        //其他未包含在提交信息中的数据需要初始化
        memberEntity.setLevelId(1L);//会员等级默认-1
        memberEntity.setStatus(0);//用户状态默认-0
        memberEntity.setCreateTime(new Date());//注册时间

        baseDao.insert(memberEntity);

    }
}