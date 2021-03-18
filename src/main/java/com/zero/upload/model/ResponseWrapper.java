package com.zero.upload.model;

import com.zero.upload.constant.StatusCode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author danyiran
 * @create 2020/8/11 14:03
 */
public class ResponseWrapper implements Serializable {

    private static final String OK = "OK";
    private static final String ERROR = "Error";
    private static final String BOOL_RES_KEY = "result";
    private Meta meta;
    private Object data;

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResponseWrapper() {
    }

    public ResponseWrapper success(String code) {
        return this.success(code, new HashMap(0));
    }

    public ResponseWrapper success(String code, Object data) {
        this.meta = new Meta(code, "OK");
        this.data = data != null ? data : new HashMap(0);
        return this;
    }

    public ResponseWrapper diyReturnMsg(String code, String msg, Object data) {
        this.meta = new Meta(code, msg);
        this.data = data != null ? data : new HashMap(0);
        return this;
    }

    public static ResponseWrapper successRespForBool(boolean result) {
        ResponseWrapper dr = new ResponseWrapper();
        Map<String, Boolean> res = new HashMap();
        res.put("result", result);
        dr.success(StatusCode.SC_200.val(), res);
        return dr;
    }

    public static ResponseWrapper successRespForVoid() {
        ResponseWrapper dr = new ResponseWrapper();
        dr.success(StatusCode.SC_200.val());
        return dr;
    }

    public static ResponseWrapper successRespForString(String result) {
        ResponseWrapper dr = new ResponseWrapper();
        Map<String, String> res = new HashMap();
        res.put("result", result);
        dr.success(StatusCode.SC_200.val(), res);
        return dr;
    }

    public static ResponseWrapper successRespForInt(int result) {
        ResponseWrapper dr = new ResponseWrapper();
        Map<String, Integer> res = new HashMap();
        res.put("result", result);
        dr.success(StatusCode.SC_200.val(), res);
        return dr;
    }

    public ResponseWrapper failure(String code) {
        return this.failure(code, "Error");
    }

    public ResponseWrapper failure(String code, String message) {
        this.meta = new Meta(code, message);
        this.data = new HashMap(0);
        return this;
    }

    public Meta getMeta() {
        return this.meta;
    }

    public Object getData() {
        return this.data;
    }

    public static class Meta implements Serializable {
        private String code;
        private String message;

        public void setCode(String code) {
            this.code = code;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Meta(String code) {
            this(code, "");
        }

        public Meta(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return this.code;
        }

        public String getMessage() {
            return this.message;
        }
    }
}
