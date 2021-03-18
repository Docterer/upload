package com.zero.upload.constant;

/**
 * @Author danyiran
 * @create 2020/8/11 14:01
 */
public enum StatusCode {

    SC_200("SC_200"),
    SC_201("SC_201"),
    SC_400("SC_400"),
    SC_401("SC_401"),
    SC_404("SC_404"),
    SC_405("SC_405"),
    SC_415("SC_415"),
    SC_422("SC_422"),
    SC_500("SC_500");

    private String code;

    private StatusCode(String code) {
        this.code = code;
    }

    public String val() {
        return this.code;
    }

    public static StatusCode valueOf(int code) {
        switch (code) {
            case 200:
                return SC_200;
            case 201:
                return SC_201;
            case 400:
                return SC_400;
            case 401:
                return SC_401;
            case 404:
                return SC_404;
            case 405:
                return SC_405;
            case 415:
                return SC_415;
            case 422:
                return SC_422;
            case 500:
                return SC_500;
            default:
                return SC_500;
        }
    }
}
