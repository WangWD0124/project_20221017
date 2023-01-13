package com.wwd.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.member.dao.MemberDao;
import com.wwd.modules.member.dto.MemberDTO;
import com.wwd.modules.member.dto.UserLoginDTO;
import com.wwd.modules.member.dto.UserRegistDTO;
import com.wwd.modules.member.entity.MemberEntity;
import com.wwd.modules.member.exception.PhoneExsitException;
import com.wwd.modules.member.exception.UserNameExsitException;
import com.wwd.modules.member.service.MemberLevelService;
import com.wwd.modules.member.service.MemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private MemberLevelService memberLevelService;

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
        //手机号码查重
        checkPhone(userRegistDTO.getPhone());
        //用户名查重
        checkUserName(userRegistDTO.getUserName());

        memberEntity.setUsername(userRegistDTO.getUserName());//用户名
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberEntity.setPassword(passwordEncoder.encode(userRegistDTO.getPassword()));//密码  //TODO MD5加盐加密
        memberEntity.setMobile(userRegistDTO.getPhone());//手机号码
        //其他未包含在提交信息中的数据需要初始化
        memberEntity.setLevelId(memberLevelService.getLevelIdByDefaultStatus());//会员默认等级
        memberEntity.setStatus(0);//用户状态默认-0
        memberEntity.setCreateTime(new Date());//注册时间

        baseDao.insert(memberEntity);

    }

    @Override
    public void checkUserName(String userName) throws UserNameExsitException {//TODO 异常机制

        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getUsername, userName);
        if (baseDao.exists(wrapper)){
            throw new UserNameExsitException();
        }
    }

    @Override
    public void checkPhone(String phone) throws PhoneExsitException {

        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getMobile, phone);
        if (baseDao.exists(wrapper)){
            throw new PhoneExsitException();
        }
    }

    /**
     * 登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public MemberEntity login(UserLoginDTO userLoginDTO) {
        String loginacct = userLoginDTO.getLoginacct();
        String password = userLoginDTO.getPassword();

        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper();
        wrapper.eq(MemberEntity::getUsername, loginacct).or().eq(MemberEntity::getMobile, loginacct);
        MemberEntity memberEntity = baseDao.selectOne(wrapper);
        if (memberEntity == null){
            return null;
        } else {
            String memberEntityPassword = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(password, memberEntityPassword);
            if (matches){
                return memberEntity;
            } else {
                return null;
            }
        }
    }
}