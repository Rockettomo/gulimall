package com.wcj.common.constant;

public class WareConstant {
    public enum PurchaseStatus {
        GREATED(0, "新建"),
        ASSINED(1, "已分配"),
        RECEIVE(2, "已领取"),
        FINISHED(3, "已完成"),
        HASEXCEPTION(4, "有异常");

        private int code;
        private String msg;

        PurchaseStatus(int code, String msg) {
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
    public enum PurchaseDetailStatus {
        GREATED(0, "新建"),
        ASSINED(1, "已分配"),
        BUYING(2, "已领取"),
        FINISHED(3, "已完成"),
        FAIL(4, "采购失败");

        private int code;
        private String msg;

        PurchaseDetailStatus(int code, String msg) {
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
}
