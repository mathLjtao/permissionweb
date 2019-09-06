package com.ljtao3.model;

import lombok.*;

import java.util.Date;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysBase {

    /**
     * 操作者
     */
    private String operator;
    /**
     * 操作者ip
     */
    private String operateIp;
    /**
     * 操作时间
     */
    private Date operateTime;
}