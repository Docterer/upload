package com.zero.upload.model;

/**
 * @Author: danyiran
 * @Date: 2020/6/18 14:53
 */
public class FileException extends RuntimeException {
    public FileException(String message) {
        super(message);
    }

    public FileException(String message, Throwable cause) {
        super(message, cause);
    }
}
