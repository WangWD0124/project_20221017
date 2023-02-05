package com.wwd.modules.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.common.utils.ConvertUtils;
import com.wwd.modules.member.dao.MemberReceiveAddressDao;
import com.wwd.modules.member.dto.MemberReceiveAddressDTO;
import com.wwd.modules.member.entity.MemberReceiveAddressEntity;
import com.wwd.modules.member.service.MemberReceiveAddressService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class MemberReceiveAddressServiceImpl extends CrudServiceImpl<MemberReceiveAddressDao, MemberReceiveAddressEntity, MemberReceiveAddressDTO> implements MemberReceiveAddressService {

    @Override
    public QueryWrapper<MemberReceiveAddressEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<MemberReceiveAddressEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public List<MemberReceiveAddressDTO> getAddressListByMemberId(Long memberId) {
        LambdaQueryWrapper<MemberReceiveAddressEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberReceiveAddressEntity::getMemberId, memberId);
        List<MemberReceiveAddressEntity> memberReceiveAddressEntities = baseDao.selectList(wrapper);
        List<MemberReceiveAddressDTO> addressDTOList = ConvertUtils.sourceToTarget(memberReceiveAddressEntities, MemberReceiveAddressDTO.class);
        return addressDTOList;
    }
}