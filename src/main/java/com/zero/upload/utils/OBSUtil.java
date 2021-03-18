package com.zero.upload.utils;

import com.alibaba.fastjson.JSON;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.DeleteObjectsRequest;
import com.obs.services.model.PutObjectResult;

import com.zero.upload.config.huawei.HuaWeiConfig;
import com.zero.upload.constant.HuaweiDataConstant;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class OBSUtil {
    /**
     * 文件类型
     */
    private static final Set<String> TYPE = new HashSet<String>(){{
        add(".bmp");
        add(".jpg");
        add(".jpeg");
        add(".gif");
        add(".png");
    }};

    /**
     * 允许上传的文件大小
     */
    //private static final Integer FILE_MAX_SIZE=5 * 1024 * 1024;


    /**
     * 图片文件上传
     * @param multipartFile 文件对象
     * @param objectName 对象名
     * @param filePath 文件路径
     * @param fileUrl 文件url
     * @return String
     */
    public static String uploadFile(MultipartFile multipartFile, String objectName, String filePath, String fileUrl) {

        //是否合法
        boolean isLegal = false;
        //获取拓展名
        String filename = multipartFile.getOriginalFilename();

        assert filename != null : "filename为空";
        String fileType = filename.substring(filename.lastIndexOf(".")).toLowerCase();

        if(TYPE.contains(fileType)){
            isLegal = true;
        }

        if (!isLegal) {
            throw new RuntimeException("图片格式错误");
        }
        if (multipartFile.getSize() > HuaweiDataConstant.FILE_MAX_SIZE) {
            throw new RuntimeException("图片格式错误");
        }
        String url_return = "";
        try {
            //文件的名字
            String originalFilename = multipartFile.getOriginalFilename();
            //文件后缀
            String substring = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

            if (null == objectName || "".equals(objectName)) {
                objectName = "user";
            }
            if (null == filePath || "".equals(filePath)) {
                //组合新的文件名加后缀
                filePath = Calendar.getInstance().getTimeInMillis() + UUID.randomUUID().toString() + substring;
            }

            ObsClient obsClient = new ObsClient(HuaWeiConfig.ak, HuaWeiConfig.sk, HuaWeiConfig.endPoint);
            // 上传文件流。
            InputStream inputStream = multipartFile.getInputStream();
            PutObjectResult response = obsClient.putObject(fileUrl, objectName + "/" + filePath, inputStream);
            if (response.getStatusCode() == 200) {
//                url_return = response.getObjectUrl();
//                url_return = url_return.replace("%2F", "/");
                url_return = "https://" + fileUrl + HuaweiDataConstant.HUAWEI_OBS_RUL + "/" + objectName + "/" + filePath;
            } else {
                throw new RuntimeException("上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 上传失败
            throw new RuntimeException("上传失败");
        }
        return url_return;
    }


    /**
     * 上传byte数组图片
     * @param file 文件对象
     * @param objectName 文件名称
     * @param fileUrl 文件路径
     * @return String
     */
    public static String uploadFileByte(String file, String objectName, String fileUrl) {
        String url_return = "";
        ObsClient obsClient = new ObsClient(HuaWeiConfig.ak, HuaWeiConfig.sk, HuaWeiConfig.endPoint);
        try {
            byte[] buffer = Base64.decodeBase64(file);
            //组合新的文件名加后缀
            String filePath = Calendar.getInstance().getTimeInMillis() + UUID.randomUUID().toString() + ".jpg";
            PutObjectResult response = obsClient.putObject(fileUrl, "robot/" + objectName + "/" + filePath, new ByteArrayInputStream(buffer));
            if (response.getStatusCode() == 200) {
                url_return = "https://" + fileUrl + HuaweiDataConstant.HUAWEI_OBS_RUL + "/" + objectName + "/" + filePath;
            } else {
                throw new RuntimeException("上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 上传失败
            throw new RuntimeException("上传失败");
        } finally {
            try {
                obsClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return url_return;
    }


    /**
     * 上传附件集合
     *
     * @param multipartFiles 文件对象集合
     * @param objectName 对象名称
     * @param filePath 文件路径
     * @return List<String>
     */
    public static List<String> uploadFileList(List<MultipartFile> multipartFiles, String objectName, String filePath, String fileUrl) {
        List<String> urlList = new ArrayList<>();
        ObsClient obsClient = new ObsClient(HuaWeiConfig.ak, HuaWeiConfig.sk, HuaWeiConfig.endPoint);
        int i = 0;
        for (MultipartFile multipartFile : multipartFiles) {
            i++;
            boolean isLegal = false;
            //获取拓展名
            String filename = multipartFile.getOriginalFilename();

            assert filename != null : "filename为空";
            String fileType = filename.substring(filename.lastIndexOf(".")).toLowerCase();

            if(TYPE.contains(fileType)){
                isLegal = true;
            }

            if (!isLegal) {
                throw new RuntimeException("图片格式错误");
            }
            if (multipartFile.getSize() > HuaweiDataConstant.FILE_MAX_SIZE) {
                throw new RuntimeException("图片格式错误");
            }
            String url_return = "";
            try {
                //文件的名字
                String originalFilename = multipartFile.getOriginalFilename();
                //文件后缀
                String substring = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

                if (null == objectName || "".equals(objectName)) {
                    objectName = "user";
                }
                //组合新的文件名加后缀
                filePath = Calendar.getInstance().getTimeInMillis() + UUID.randomUUID().toString() + i + substring;


                /**
                 * 上传文件流
                 */
                InputStream inputStream = multipartFile.getInputStream();
                PutObjectResult response = obsClient.putObject(fileUrl, objectName + "/" + filePath, inputStream);
//                url_return = response.getObjectUrl();
//                url_return = url_return.replace("%2F", "/");
                if (response.getStatusCode() == 200) {
//                url_return = response.getObjectUrl();
//                url_return = url_return.replace("%2F", "/");
                    url_return = "https://" + fileUrl + HuaweiDataConstant.HUAWEI_OBS_RUL + "/" + objectName + "/" + filePath;
                    urlList.add(url_return);
                } else {
                    throw new RuntimeException("上传失败");
                }

            } catch (Exception e) {
                e.printStackTrace();
                // 上传失败
                throw new RuntimeException("上传失败");
            }
        }
        try {
            obsClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlList;
    }

    /**
     * 图片文件上传
     * @param multipartFile 文件对象
     * @param fileUrl 路径url
     * @return String
     */
    public static String uploadVoice(MultipartFile multipartFile, String fileUrl) {
        ObsClient obsClient = new ObsClient(HuaWeiConfig.ak, HuaWeiConfig.sk, HuaWeiConfig.endPoint);
        String url_return = "";
        try {
            String objectName = "voice";
            //文件的名字
            String originalFilename = multipartFile.getOriginalFilename();
            //文件后缀
            assert originalFilename != null :"文件名为空";
            String substring = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            //组合新的文件名加后缀
            String filePath = Calendar.getInstance().getTimeInMillis() + UUID.randomUUID().toString() + substring;

            // 上传文件流。
            InputStream inputStream = multipartFile.getInputStream();
            PutObjectResult response = obsClient.putObject(fileUrl, objectName + "/" + filePath, inputStream);
            if (response.getStatusCode() == 200) {
                url_return = "https://" + fileUrl + HuaweiDataConstant.HUAWEI_OBS_RUL + "/" + objectName + "/" + filePath;
            } else {
                throw new RuntimeException("上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 上传失败
            throw new RuntimeException("上传失败");
        } finally {
            try {
                obsClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return url_return;
    }

    /**
     * 删除图片
     * @param url url
     * @param fileUrl fileUrl
     * @return boolean
     */
    public static boolean delImgF(String url, String fileUrl) {
        if(StringUtils.isEmpty(url)){
            throw new RuntimeException("删除失败！");
        }
        if (!url.contains(HuaweiDataConstant.HUAWEI_OBS_RUL)) {
            throw new RuntimeException("删除失败！");
        }
        String str = "https://" + fileUrl + HuaweiDataConstant.HUAWEI_OBS_RUL + "/";
        url = url.replace(str, "");
        ObsClient obsClient = new ObsClient(HuaWeiConfig.ak, HuaWeiConfig.sk, HuaWeiConfig.endPoint);
        try {
            DeleteObjectsRequest request = new DeleteObjectsRequest();
//            DeleteObjectResult result =  obsClient.deleteObject(HuaWeiConfig.bucketname, url);
            DeleteObjectResult result = obsClient.deleteObject(fileUrl, url);
            System.out.println(result.getVersionId());
            System.out.println(result.getObjectKey());

            System.out.println(JSON.toJSONString(result));
        } catch (ObsException e) {
            StringBuilder sb;
            sb = new StringBuilder();
            sb.append("Response Code:").append(e.getResponseCode()).append("\n");
            sb.append("Error Message:").append(e.getErrorMessage()).append("\n");
            sb.append("Error Code:").append(e.getErrorCode()).append("\n");
            sb.append("Request ID:").append(e.getErrorRequestId()).append("\n");
            sb.append("HOST ID").append(e.getErrorHostId()).append("\n");
            System.out.println(sb.toString());
        } finally {
            try {
                obsClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
