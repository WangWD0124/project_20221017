package com.wwd.modules.member.exception;


//TODO 异常机制
public class UserNameExsitException extends RuntimeException{

    public UserNameExsitException() {
        super("用户名已存在");
    }
}
