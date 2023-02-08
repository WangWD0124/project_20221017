package com.wwd.modules.ware.service.impl;

import com.wwd.common.utils.Result;
import com.wwd.modules.ware.feign.MemberServiceFeign;
import com.wwd.modules.ware.service.FareService;
import com.wwd.modules.ware.vo.FareVo;
import com.wwd.modules.ware.vo.MemberReceiveAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FareServiceImpl implements FareService {

    @Autowired
    private MemberServiceFeign memberServiceFeign;

    @Override
    public FareVo getFare(Long addrId) {

        FareVo fareVo = new FareVo();
        MemberReceiveAddressDTO addressDTO = memberServiceFeign.get(addrId).getData();
        fareVo.setAddress(addressDTO);
        fareVo.setFarePrice(new BigDecimal("10"));
        return fareVo;
    }
}
