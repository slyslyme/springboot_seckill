package com.sly.seckill.result;

/**
 * @author slyslyme
 * @version 1.0
 * @date 2019/8/23 9:14
 * @package com.sly.seckill.result
 **/
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    /**
     * 定义为private是为了防止在controller中直接new
     * @param data
     */
    private Result(T data){
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg severError){
        if(severError == null) return;
        this.code = severError.getCode();
        this.msg = severError.getMsg();
    }

    /**
     * 只有get没有set方法是为了防止在controller使用set对结果进行修改，从而达到一个更好的封装效果
     * @return
     */
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    /**
     * 成功时调用返回结果
     * @param data
     * @param <T>
     * @return
     */
    public static<T> Result<T> success(T data){
        return new Result<T>(data);
    }

    public static<T> Result<T> error(CodeMsg serverError){
        return new Result<T>(serverError);
    }
}
