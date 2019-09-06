package com.ljtao3.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookieUser {

    private int userId;

    private String username;

    private long lastLogin;

    private String ip;

    private String mac;
}