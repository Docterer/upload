package com.zero.upload.service;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Jython脚本工具类
 *
 * @author danyiran
 * @date 2021-01-23
 */
@Slf4j
@Service
public class PyScriptService {
    private PythonInterpreter interpreter;

    @PostConstruct
    private void init() {
        log.info("开始初始化python解析器：" + new DateTime(new Date(System.currentTimeMillis())).toString());
        interpreter = new PythonInterpreter();
        log.info("结束初始化python解析器:" + new DateTime(new Date(System.currentTimeMillis())).toString());
    }

    /**
     * 传进来的脚本应该满足如下格式::
     * def convert(a): return float(a[0:len(a)-1])/1000
     *
     * @param inputValue
     * @param script
     * @return
     */
    public Double evalValueConvert(String inputValue, String script) {
        if (StringUtils.isEmpty(script)) {
            return Double.parseDouble(inputValue);
        }
        interpreter.exec(script);
        PyFunction pyFunction = interpreter.get("convert", PyFunction.class);
        PyFloat pyFloat = (PyFloat) pyFunction.__call__(new PyString(inputValue));
        return pyFloat.getValue();
    }

    public List<Double> evalMeanCalculation(List<Double> vlist, Integer frequency, String script) {
        if (StringUtils.isEmpty(script)) {
            return null;
        }
        if (frequency == null || frequency == 0) {
            return null;
        }
        interpreter.exec(script);
        PyFunction pyFunction = interpreter.get("calculation", PyFunction.class);
        PyList pyList = (PyList) pyFunction.__call__(new PyList(vlist), new PyInteger(frequency));
        List<Double> doubleList = new ArrayList<>();
        for (Object obj : pyList) {
            doubleList.add((Double) obj);
        }
        return doubleList;
    }
}
