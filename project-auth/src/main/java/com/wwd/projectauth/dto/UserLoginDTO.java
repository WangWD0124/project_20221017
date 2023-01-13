package com.wwd.projectauth.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserLoginDTO {

    @NotEmpty(message = "邮箱/用户名/已验证手机必须填写")
    private String loginacct;

    @NotEmpty(message = "密码必须填写")
    private String password;
}
