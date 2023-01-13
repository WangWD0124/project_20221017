package com.wwd.modules.member.exception;

public class PhoneExsitException extends RuntimeException {

    public PhoneExsitException() {
        super("手机号已被注册");
    }
}
