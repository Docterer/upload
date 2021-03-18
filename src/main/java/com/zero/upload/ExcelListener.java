package com.zero.upload;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author danyiran
 * @create 2021/3/16 15:56
 */
@Slf4j
public class ExcelListener extends AnalysisEventListener<Map<String, String>> {

    //TODO:放到全局缓存中，方便清理
    private List<Map<String, String>> fileEntities = new ArrayList<>();

    /**
     * sheet和column头关联的map
     */
    public static Map<String, List<String>> headMapper = new HashMap<>();

    /**
     * sheet和数据的关联map
     */
    public static Map<String, List<Map<String, String>>> dataMap = new HashMap<>();

    /**
     * 思考如何处理多个sheet的时候保存读出来的数据的问题
     *
     * @param obj
     * @param analysisContext
     */
    @Override
    public void invoke(Map<String, String> obj, AnalysisContext analysisContext) {
        log.info("解析到一条数据:{}", JSON.toJSONString(obj));
        fileEntities.add(obj);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据：{}, currentRowHolder: {}", headMap.toString(), context.readRowHolder().getRowIndex());
        headMapper.put(context.readSheetHolder().getSheetName(), headMap.values().stream().collect(Collectors.toList()));
        Map<Integer, Map<Integer, String>> map = new HashMap<>();
        map.put(context.readRowHolder().getRowIndex(), headMap);
        log.info("headMap:{}", map);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("after");
        //deep copy
        List<Map<String, String>> fileEntitiesTmp = new ArrayList<>();
        fileEntitiesTmp.addAll(fileEntities);
        dataMap.put(analysisContext.readSheetHolder().getSheetName(), fileEntitiesTmp);
        fileEntities.clear();
    }
}
