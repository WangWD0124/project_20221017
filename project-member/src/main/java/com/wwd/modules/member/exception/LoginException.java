package com.wwd.modules.member.exception;

public class LoginException extends RuntimeException {

    public LoginException() {
        super("账号或密码有误");
    }
}
