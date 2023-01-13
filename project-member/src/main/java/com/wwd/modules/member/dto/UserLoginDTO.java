package com.wwd.modules.member.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginDTO {

    private String loginacct;

    private String password;
}
