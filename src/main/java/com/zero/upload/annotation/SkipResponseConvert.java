package com.zero.upload.annotation;

import java.lang.annotation.*;

/**
 * @Author danyiran
 * @create 2020/8/11 14:07
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipResponseConvert {
}
