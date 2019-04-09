package com.atguigu.gmall.admin.config;

import com.atguigu.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @ControllerAdvice //统一异常处理类
@RestControllerAdvice
@Slf4j
public class GmalGlobalExceptionHandler {

    @ExceptionHandler(ArithmeticException.class)
    public Object arithmeticException(Exception e){
        log.error("感知到异常...");
        return new CommonResult().validateFailed(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Object exception(Exception e){
        log.error("感知到异常...");
        return new CommonResult().validateFailed(e.getMessage());
    }
}
