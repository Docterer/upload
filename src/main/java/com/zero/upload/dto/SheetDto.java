package com.zero.upload.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author:danyiran
 * @Date: 2021/3/18 09:24
 */
@Data
public class SheetDto {

    /**
     * 表名
     */
    private String targetTableName;

    /**
     * 表内字段
     */
    private Map<String, String> targetTable;

    /**
     * 是否新增
     */
    private boolean added;
}
