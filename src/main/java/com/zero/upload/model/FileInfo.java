package com.zero.upload.model;

import com.zero.upload.dto.FileParseProcessDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: yiran
 * @Date: 2020/6/18 17:46
 */
@Data
public class FileInfo {

    private String fileName;

    private String yearDir;

    private String monthDir;

    private String dayDir;

    private List<FileParseProcessDto> fileParseProcessDtoList;
}
