package com.zero.upload.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zero.upload.annotation.SkipResponseConvert;
import com.zero.upload.constant.StatusCode;
import com.zero.upload.exception.RootException;
import com.zero.upload.model.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;

/**
 * @Author danyiran
 * @create 2020/8/11 14:06
 */
@Slf4j
@ControllerAdvice(
        annotations = {RestController.class, ControllerAdvice.class}
)
public class ResponseConverter implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        Class returnClass = returnType.getMethod().getReturnType();
        if (returnType.getMethodAnnotation(SkipResponseConvert.class) != null) {
            log.debug("the method has 'SkipResponseConvert' annotation, so its return value will not be converted by this converter.");
            return false;
        } else if (returnClass.equals(Object.class) || !returnClass.isAssignableFrom(ResponseWrapper.class) && !returnClass.isAssignableFrom(ResponseEntity.class)) {
            log.debug("the return value will be converted by this converter.");
            return true;
        } else {
            log.debug("the return value is a 'ResponseWrapper' or 'ResponseEntity', so it will not be converted by this converter.");
            return false;
        }
    }

    @Nullable
    @Override
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        String query = request.getURI().getQuery();
        RootException exception;
        if (!this.isEnvelop(query)) {
            if (body instanceof RootException) {
                exception = (RootException) body;
                ResponseWrapper.Meta meta = new ResponseWrapper.Meta(exception.getCode(), exception.getMessage());
                return meta;
            } else {
                return body;
            }
        } else if (body instanceof RootException) {
            exception = (RootException) body;
            return (new ResponseWrapper()).failure(exception.getCode(), exception.getMessage());
        } else {
            int code = HttpStatus.OK.value();
            Annotation[] annotations = returnType.getMethodAnnotations();
            Annotation[] arr$ = annotations;
            int len$ = annotations.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                Annotation annotation = arr$[i$];
                if (annotation.annotationType().equals(ResponseStatus.class)) {
                    ResponseStatus res = (ResponseStatus) annotation;
                    code = res.value().value();
                    break;
                }
            }

            ResponseWrapper responseWrapper = (new ResponseWrapper()).success(StatusCode.valueOf(code).val(), body);
            if (body instanceof String) {
                log.debug("the return type is string, so convert the response to string");

                try {
                    return (new ObjectMapper()).writeValueAsString(responseWrapper);
                } catch (JsonProcessingException var15) {
                    log.warn(var15.getMessage(), var15);
                    return body;
                }
            } else {
                return responseWrapper;
            }
        }
    }

    private boolean isEnvelop(String query) {
        if (query != null) {
            String[] queries = query.split("&");
            String[] arr$ = queries;
            int len$ = queries.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String s = arr$[i$];
                if (s.trim().equalsIgnoreCase("envelope=false")) {
                    log.info("find envelope=false skip response convert");
                    return false;
                }
            }
        }

        return true;
    }
}
