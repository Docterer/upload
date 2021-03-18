package com.zero.upload.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author danyiran
 * @version 1.0
 * @date 2019/11/27 11:41
 * @description
 */
public interface HuaWeiService {

    //上传图片
    String uploadFileStream(MultipartFile request, String objectName, String filePath);

    //上传图片集合
    List<String> uploadFileList(List<MultipartFile> fileList, String objectName, String filePath);

    //图片上传的接口（单个） 字节流
    String uploadImgByte(String file, String objectName);

    //图片删除  （单个）
    boolean delImgF(String fileUrl);

    String uploadVoice(MultipartFile file);

}
