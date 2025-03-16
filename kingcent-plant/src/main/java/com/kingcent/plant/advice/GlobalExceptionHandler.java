package com.kingcent.plant.advice;

import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理所有未被其他异常处理器捕获的异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleGenericException(Exception ex) {
        ex.printStackTrace(System.out);
        return Result.fail(ex.getMessage());
    }

    // 处理特定的异常，例如自定义异常
    @ExceptionHandler(KingcentSystemException.class)
    public Result<?> handleIllegalArgumentException(KingcentSystemException ex) {
        return Result.fail(ex.getMessage());
    }
}