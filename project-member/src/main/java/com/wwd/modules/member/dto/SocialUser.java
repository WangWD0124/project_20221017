package com.wwd.modules.member.dto;

import lombok.Data;

@Data
public class SocialUser {
    private String name;
    private String social_uid;
    private String alcess_token;
    private String expires_in;
}
