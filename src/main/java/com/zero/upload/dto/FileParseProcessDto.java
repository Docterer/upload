package com.zero.upload.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author danyiran
 */
@Data
public class FileParseProcessDto implements Serializable {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * sheet名
     */
    private String sheetName;

    private SheetDto sheetDetail;
}
