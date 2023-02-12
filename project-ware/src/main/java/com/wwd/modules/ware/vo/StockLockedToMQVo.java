package com.wwd.modules.ware.vo;

import lombok.Data;

/**
 * 封装库存锁定消息
 */
@Data
public class StockLockedToMQVo {

    private Long taskId;
//    private Long taskDetailId;
}
