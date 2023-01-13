package com.wwd.modules.member.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.member.dto.MemberDTO;
import com.wwd.modules.member.dto.UserLoginDTO;
import com.wwd.modules.member.dto.UserRegistDTO;
import com.wwd.modules.member.entity.MemberEntity;
import com.wwd.modules.member.exception.PhoneExsitException;
import com.wwd.modules.member.exception.UserNameExsitException;

/**
 * 会员
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface MemberService extends CrudService<MemberEntity, MemberDTO> {

    void register(UserRegistDTO userRegistDTO);

    void checkUserName(String userName) throws UserNameExsitException;

    void checkPhone(String phone) throws PhoneExsitException;

    MemberEntity login(UserLoginDTO userLoginDTO);
}