package com.only4.common.entity;

import lombok.Data;

/**
 * 接口返回状态
 * @author cap4j-ddd-codegen
 */
@Data
public class Status {

    /**
     * 业务状态码
     */
    private Integer code;

    /**
     * 业务状态信息
     */
    private String msg;

    public Status() {
    }

    public Status(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
