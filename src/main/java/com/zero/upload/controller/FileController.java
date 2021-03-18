package com.zero.upload.controller;

import com.zero.upload.model.FileInfo;
import com.zero.upload.model.Response;
import com.zero.upload.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import java.io.IOException;
import org.springframework.http.MediaType;

/**
 * @author: danyiran
 * @Date: 2020/6/18 14:54
 * @Description: 参考https://www.jianshu.com/p/e25b3c542553
 */
@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private FileService fileService;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    public FileInfo uploadFile(@RequestParam("file") MultipartFile file){
        FileInfo fileInfo = fileService.storeFile(file);
        String fileName = fileInfo.getFileName();

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("file/download/")
                .path(fileInfo.getYearDir()+"/").
                        path(fileInfo.getMonthDir()+"/").
                        path(fileInfo.getDayDir()+"/").
                        path(fileInfo.getFileName())
                .toUriString();

        return fileInfo;
    }


    @RequestMapping(value = "/uploadMultipleFiles",method = RequestMethod.POST)
    public List<FileInfo> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }


    @RequestMapping(value = "/download/{year}/{month}/{day}/{fileName}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable(name = "year") String year,
            @PathVariable(name = "month") String month,
            @PathVariable(name = "day") String day,
            @PathVariable(name = "fileName") String fileName,
            HttpServletRequest request
                                                 ) {
        logger.info("----------------");

        FileInfo fileInfo=new FileInfo();
        fileInfo.setYearDir(year);
        fileInfo.setMonthDir(month);
        fileInfo.setDayDir(day);
        fileInfo.setFileName(fileName);

        // Load file as Resource
        Resource resource = fileService.loadFileAsResource(fileInfo);


        // Try to determine file's content type
        String contentType = null;
        try {
            System.out.println(resource.getFile().getAbsolutePath());
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
