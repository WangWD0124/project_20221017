package com.wwd.modules.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {

    private MemberReceiveAddressDTO address;//地址信息

    private BigDecimal farePrice;//运费
}
