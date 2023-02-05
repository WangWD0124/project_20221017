package com.wwd.modules.member.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.member.dto.MemberReceiveAddressDTO;
import com.wwd.modules.member.entity.MemberReceiveAddressEntity;

import java.util.List;

/**
 * 会员收货地址
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface MemberReceiveAddressService extends CrudService<MemberReceiveAddressEntity, MemberReceiveAddressDTO> {

    List<MemberReceiveAddressDTO> getAddressListByMemberId(Long memberId);
}