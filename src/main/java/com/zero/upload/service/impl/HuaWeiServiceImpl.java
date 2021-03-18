package com.zero.upload.service.impl;

import com.zero.upload.service.HuaWeiService;
import com.zero.upload.utils.OBSUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;


/**
 * @author danyiran
 */
@Service
public class HuaWeiServiceImpl implements HuaWeiService {
    @Value("${file_url}")
    private String fileUrl;

    /**
     * 图片上传的接口（单个）
     * @param file 文件
     * @param objectName 对象名称
     * @param filePath 文件路径
     * @return String
     */
    @Override
    public String uploadFileStream(MultipartFile file, String objectName, String filePath) {
        if (file==null) {
            throw new RuntimeException("file不能为空");
        }
        return OBSUtil.uploadFile(file, objectName, filePath, fileUrl);
    }

    @Override
    public List<String> uploadFileList(List<MultipartFile> fileList, String objectName, String filePath) {
        if(fileList==null){
            throw new RuntimeException("file不能为空");
        }
        return OBSUtil.uploadFileList(fileList, objectName, filePath, fileUrl);
    }

    @Override
    public String uploadImgByte(String file, String objectName) {
        return OBSUtil.uploadFileByte(file, objectName, fileUrl);
    }

    @Override
    public boolean delImgF(String url) {
        return OBSUtil.delImgF(url, fileUrl);
    }

    @Override
    public String uploadVoice(MultipartFile file) {
        if(file==null){
            throw new RuntimeException("file不能为空");
        }
        return OBSUtil.uploadVoice(file, fileUrl);
    }
}
