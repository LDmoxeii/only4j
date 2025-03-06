package com.only4.web.handler;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.only4.common.entity.R;
import com.only4.common.enums.CodeEnum;
import com.only4.common.exception.ErrorException;
import com.only4.common.exception.KnownException;
import com.only4.common.exception.WarnException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * 全局异常处理器
 *
 * @author LD_moxeii
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public String getRequestInfo(HttpServletRequest request) {
        RequestMsg requestMsg = new RequestMsg();
        Map<String, String[]> param = request.getParameterMap();
        String url = request.getRequestURI();
        requestMsg.setParams(param);
        requestMsg.setUrl(url);
        return JSON.toJSONString(requestMsg);
    }

    @Data
    @JsonPropertyOrder({"url", "params"})
    public static class RequestMsg {
        private String url;
        private Map<String, String[]> params;
    }

    /**
     * 找不到路由
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<Object> handleError(NoHandlerFoundException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}'不存在.", requestURI);
        return R.fail(CodeEnum.NOT_FOUND);
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<Void> handleError(HttpRequestMethodNotSupportedException e,
                               HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.warn("请求地址'{}',不支持'{}'请求", requestURI, e.getMessage());
        return R.fail(CodeEnum.METHOD_NOT_SUPPORTED);
    }

    /**
     * 参数校验异常
     *
     * @param e exception
     * @return ResponseData
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> handleError(HttpServletRequest request, ConstraintViolationException e) {
        String requestInfo = getRequestInfo(request);
        log.warn("参数校验异常:{} request:{} ", e.getMessage(), requestInfo, e);
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            String message = constraintViolation.getMessage();
            return R.fail(CodeEnum.PARAM_INVALIDATE.getCode(), message);
        }
        return R.fail(CodeEnum.PARAM_INVALIDATE.getCode(), e.getLocalizedMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> handleError(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数:{}", e.getMessage(), e);
        String message = String.format("缺少必要的请求参数: %s", e.getParameterName());
        return R.fail(CodeEnum.PARAM_INVALIDATE.getCode(), message);
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> handleError(MethodArgumentTypeMismatchException e,
                                 HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.warn("请求参数类型不匹配'{}',发生系统异常.", requestURI);
        String message = String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), e.getRequiredType().getName(), e.getValue());
        return R.fail(CodeEnum.PARAM_INVALIDATE.getCode(), message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> handleError(MethodArgumentNotValidException e) {
        log.warn("参数验证失败:{}", e.getMessage(), e);
        return R.fail(CodeEnum.PARAM_INVALIDATE.getCode(), "参数[" + e.getBindingResult().getFieldError().getField() + "]不正确：" + e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> handleError(BindException e) {
        log.warn("参数绑定失败:{}", e.getMessage(), e);
        return R.fail(CodeEnum.PARAM_INVALIDATE.getCode(), "参数不正确");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> handleError(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.error("消息不能读取:{} request:{}", e.getMessage(), getRequestInfo(request), e);
        return R.fail(CodeEnum.MESSAGE_NOT_READABLE);
    }

    @ExceptionHandler(value = MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Object> headerParamException(HttpServletRequest request, MissingRequestHeaderException e) {
        log.warn("缺少header参数:{} request:{}", e.getHeaderName(), getRequestInfo(request), e);
        return R.fail(CodeEnum.PARAM_INVALIDATE.getCode(), "缺少header参数");
    }

    @ExceptionHandler(value = KnownException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> knownException(KnownException e, HttpServletRequest request) {
        if (Level.ERROR.toString().equalsIgnoreCase(e.getLevel())) {
            log.error("发生业务错误: ", e);
        } else if (Level.WARN.toString().equalsIgnoreCase(e.getLevel())) {
            log.warn("发生业务警告: ", e);
        } else if (log.isDebugEnabled()) {
            log.debug("业务失败返回: ", e);
        }
        return R.fail(e);
    }

    @ExceptionHandler(value = WarnException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Object> warnException(WarnException be) {
        log.warn("发生业务警告: ", be);
        return R.fail(be);
    }

    @ExceptionHandler(value = ErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> errorException(ErrorException be) {
        log.error("发生业务错误: ", be);
        return R.fail(be);
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Object> handleError(HttpServletRequest request, Throwable e) {
        KnownException ke = getKnownException(e);
        if (ke != null) {
            return knownException(ke, request);
        }
        log.error("发生未知异常:{} request:{}", e.getMessage(), getRequestInfo(request), e);
        return R.fail(CodeEnum.ERROR);
    }

    /**
     * servlet异常
     */
    @ExceptionHandler(ServletException.class)
    public R<Void> handleServletException(ServletException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return R.fail(e.getMessage());
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public R<Void> handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI);
        return R.fail(String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public void handleRuntimeException(IOException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        if (requestURI.contains("sse")) {
            // sse 经常性连接中断 例如关闭浏览器 直接屏蔽
            return;
        }
        log.error("请求地址'{}',连接中断", requestURI, e);
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return R.fail(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return R.fail(e.getMessage());
    }

    private KnownException getKnownException(Throwable e) {
        if (e instanceof KnownException) {
            return (KnownException)e;
        }
        if (e.getCause() != null && e.getCause() != e) {
            return getKnownException(e.getCause());
        }
        return null;
    }

}
