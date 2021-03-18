package com.zero.upload.exception;

/**
 * @Author danyiran
 * @create 2020/8/11 14:19
 */
public class RootException extends RuntimeException {

    protected String code;

    public RootException(String s, String code) {
        super(s);
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
