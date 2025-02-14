package com.kingcent.handler;

import com.kingcent.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutionException;


@ControllerAdvice
@Slf4j
public class MyExceptionHandler {

    @ExceptionHandler(value = {ExecutionException.class})
    @ResponseBody
    public Result<?> connect(ExecutionException e){
        return Result.fail("连接超时，请稍后重试");
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public Result<?> exceptionHandler(Exception e){
        log.error("服务异常:{}", e.getMessage());
        e.printStackTrace();
        return Result.fail("服务异常，请稍后重试");
    }
}
