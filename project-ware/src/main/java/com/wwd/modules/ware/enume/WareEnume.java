package com.wwd.modules.ware.enume;

public enum WareEnume {

    NO_STOCK_EXCEPTION(1, "订单库存锁定失败");

    private Integer code;
    private String msg;

    WareEnume(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public Integer getCode(){
        return code;
    }
    public String getMsg(){
        return msg;
    }
}

