package com.wwd.projectcart.dto;

import lombok.Data;

@Data
public class UserInfoDTO {

    private Long userId;
    private String userKey;
    private Boolean tempUser = false;
}
