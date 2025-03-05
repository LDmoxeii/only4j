package com.only4.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.only4.common.enums.CodeEnum;
import com.only4.common.exception.ErrorException;
import com.only4.common.exception.KnownException;
import com.only4.common.exception.WarnException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 接口响应格式
 *
 * @author cap4j-ddd-codegen
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class R<T> {

    /**
     * 成功
     */
    public static final Status SUCCESS = new Status(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getName());

    /**
     * 失败
     */
    public static final Status FAIL = new Status(CodeEnum.FAIL.getCode(), CodeEnum.FAIL.getName());


    /**
     * 状态码
     */
    private Status status;


    /**
     * 结果集
     */
    private T data;

    /**
     * 服务器时间
     */
    private long timestamp;

    private static <T> R<T> restResult(Integer code, String msg,T data) {
        R<T> r = new R<>();
        r.setStatus(new Status(code, msg));

        if (code == null) {
            r.status.setCode(CodeEnum.FAIL.getCode());
        }

        if (msg == null) {
            r.status.setMsg("");
        }

        r.setData(data);
        r.setTimestamp(System.currentTimeMillis());
        return r;
    }

    public static <T> R<T> ok() {
        return restResult(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getName(), null);
    }

    public static <T> R<T> ok(T data) {
        return restResult(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getName(), data);
    }

    public static <T> R<T> ok(String msg, T data) {
        return restResult(CodeEnum.SUCCESS.getCode(), msg, data);
    }

    public static <T> R<T> fail() {
        return restResult(CodeEnum.FAIL.getCode(), CodeEnum.FAIL.getName(), null);
    }

    public static <T> R<T> fail(String msg) {
        return restResult(CodeEnum.FAIL.getCode(), msg, null);
    }

    public static <T> R<T> fail(CodeEnum codeEnum) {
        return restResult(codeEnum.getCode(), codeEnum.getName(), null);
    }

    public static <T> R<T> fail(KnownException knownException) {
        return restResult(knownException.getCode(), knownException.getMsg(), null);
    }

    public static <T> R<T> fail(Integer code, String msg) {
        code = code == null ? CodeEnum.FAIL.getCode() : code;
        return restResult(code, msg, null);
    }

    public static <T> R<T> illegalArgument() {
        return restResult(CodeEnum.PARAM_INVALIDATE.getCode(), CodeEnum.PARAM_INVALIDATE.getName(), null);
    }

    public static <T> R<T> systemError() {
        return restResult(CodeEnum.ERROR.getCode(), CodeEnum.ERROR.getName(), null);
    }

    @JsonIgnore
    public boolean isFail() {
        Integer retCode = status.getCode();
        return !Objects.equals(CodeEnum.SUCCESS.getCode(), retCode);
    }

    public void throwKnownException(){
        throw new KnownException(status.getCode(), status.getMsg());
    }

    public void throwWarnException(){
        throw new WarnException(status.getCode(), status.getMsg());
    }

    public void throwErrorException(){
        throw new ErrorException(status.getCode(), status.getMsg());
    }

    public void tryThrowKnownException(){
        if(isFail()){
            throwKnownException();
        }
    }

    public void tryThrowWarnException(){
        if(isFail()){
            throwWarnException();
        }
    }

    public void tryThrowErrorException(){
        if(isFail()){
            throwErrorException();
        }
    }
}
