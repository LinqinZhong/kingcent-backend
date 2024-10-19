package com.kingcent.common.entity.result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private T data;
    private Boolean success;
    private String message;

    public Result<T> code(Integer code){
        this.code = code;
        return this;
    }

    public Result<T> data(T data){
        this.data = data;
        return this;
    }

    public Result<T> success(Boolean success){
        this.success = success;
        return this;
    }

    public static <T> Result<T> busy(){
        return fail("系统繁忙，请稍后重试");
    }

    public Result<T> message(String message){
        this.message = message;
        return this;
    }

    public static <T> Result<T> success(String message, T data){
        return new Result<T>().success(true).code(200).message(message).data(data);
    }

    public static Result<?> success(){
        return Result.success("成功");
    }

    public static Result<?> success(String message){
        return success(message, null);
    }

    public static <T> Result<T> success(T data){
        return success("成功", data);
    }


    public static <T> Result<T> fail(Integer code, String message){
        return new Result<T>().success(false).code(code).message(message);
    }

    public static <T> Result<T> fail(String message){
        return fail(400, message);
    }
}
