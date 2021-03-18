package com.zero.upload.service.impl;

import com.zero.upload.model.FileException;
import com.zero.upload.model.FileInfo;
import com.zero.upload.service.FileService;
import com.zero.upload.utils.HcFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author: danyiran
 * @Date: 2020/6/18 14:56
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-dir}")
    private String path;

    @Autowired
    HcFileUtils hcFileUtils;

    /**
     * 存储文件到系统
     *
     * @param file 文件
     * @return 文件名
     */
    @Override
    public FileInfo storeFile(MultipartFile file) {
        try {
            FileInfo fileInfo = hcFileUtils.uploadFile(file);
            return fileInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载文件
     *
     * @param fileInfo 文件info对象
     * @return 文件
     */
    @Override
    public Resource loadFileAsResource(FileInfo fileInfo) {
        try {
            Path filePath = Paths.get(path).resolve(fileInfo.getYearDir()).resolve(fileInfo.getMonthDir()).resolve(fileInfo.getDayDir()).resolve(fileInfo.getFileName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileException("File not found " + fileInfo.getFileName());
            }
        } catch (MalformedURLException ex) {
            throw new FileException("File not found " + fileInfo.getFileName(), ex);
        }
    }
}
