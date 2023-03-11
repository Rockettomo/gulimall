package com.wcj.common.exception;


public enum BizCodeEnum {
    UNKONW_EXCEPITON(10000,"系统位置异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    VALID_SMS_CODE_EXCEPTION(10002,"短信验证码请求过快"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");

    private int code;
    private String msg;
    BizCodeEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
