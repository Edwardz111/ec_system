package com.ec.pojo.vo;

/**
 * 统一接口返回格式
 * {"code":200,"msg":"成功","data":{}}
 */
public class Result<T> {

    private Integer code;
    private String  msg;
    private T       data;

    private Result() {}

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg  = "成功";
        r.data = data;
        return r;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.code = 500;
        r.msg  = msg;
        return r;
    }

    public static <T> Result<T> unauthorized() {
        Result<T> r = new Result<>();
        r.code = 401;
        r.msg  = "未登录或登录已过期";
        return r;
    }

    public Integer getCode() { return code; }
    public String  getMsg()  { return msg;  }
    public T       getData() { return data; }
}