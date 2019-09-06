package com.ljtao3.enums;

import lombok.Getter;

@Getter
public enum Status {

    AVAILABLE(1, "有效"),
    NOT_AVAILABLE(0, "无效"),
    DELETED(2, "删除");

    private int code;
    private String desc;

    Status(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
