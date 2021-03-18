package com.zero.upload.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.util.Date;

/**
 * @Author danyiran
 * @create 2021/3/17 14:27
 */
@Data
public class Resource {

    @CsvBindByName(column = "id")
    private String id;
    @CsvBindByName(column = "createTime")
    private Date createTime;
    @CsvBindByName(column = "instrumentId")
    private String instrumentId;
    @CsvBindByName(column = "resourceDesc")
    private String resourceDesc;
    @CsvBindByName(column = "resourceId")
    private String resourceId;
}
