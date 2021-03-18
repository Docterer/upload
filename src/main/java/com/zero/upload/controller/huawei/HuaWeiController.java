package com.zero.upload.controller.huawei;


import com.zero.upload.service.HuaWeiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author: danyiran
 * @Date: 2019/10/25 18:26
 * @Description
 */

@RestController
@RequestMapping(value = {"/huawei-oss"})
public class HuaWeiController {

    @Resource
    private HuaWeiService huaWeiService;

    private final Logger logger = LoggerFactory.getLogger(HuaWeiController.class);

    /**
     * 图片上传的接口（单个）
     * @param file
     * @param objectName
     * @param filePath
     * @return
     */
    @RequestMapping(value = "/uploadImgF", method = RequestMethod.POST)
    public Map uploadFileStream(@RequestBody MultipartFile file, @RequestParam(defaultValue = "")
            String objectName, @RequestParam(defaultValue = "") String filePath) {
        logger.info("开始上传图片"+file.getOriginalFilename());

        Map<String,String> map=new HashMap();

        String url = huaWeiService.uploadFileStream(file, objectName, filePath);
        if (url != null && !"".equals(url)) {
            map.put("result","success");
            map.put("url",url);
        } else {
            map.put("result","fail");
        }
        return map;
    }

    /**
     * 图片上传的接口（多个）
     * @param fileList
     * @param objectName
     * @param filePath
     * @param request
     * @return
     */
    @RequestMapping(value = "/uploadFileList", method = RequestMethod.POST)
    public Object uploadFileList(@RequestBody List<MultipartFile> fileList, @RequestParam(defaultValue = "")
            String objectName, @RequestParam(defaultValue = "") String filePath, HttpServletRequest request) {
        List<String> urlList = huaWeiService.uploadFileList(fileList, objectName, filePath);
        Map<String,String> map=new HashMap();
        if(urlList!=null){
            map.put("result","success");
        }else {
            map.put("result","fail");
        }

        return map;
    }


    /**
     * 图片上传的接口（单个） 字节流
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadImgByte", method = RequestMethod.POST)
    public Object uploadImgByte(@RequestParam(defaultValue = "") String file) {

        Map<String,String> map=new HashMap();

        System.out.println("图片上传");
        if ("".equals(file)) {
            throw new RuntimeException("操作失败");
        }
        String objectName = "robot";
        String url = huaWeiService.uploadImgByte(file, objectName);
        if (url != null && !"".equals(url)) {
            map.put("result","success");
        } else {
            map.put("result","fail");
        }
        return map;
    }

    /**
     * 图片删除  （单个）
     * @param fileUrl
     * @return
     */
    @RequestMapping(value = "/delImgF", method = RequestMethod.POST)
    public Object delImgF(@RequestParam(defaultValue = "") String fileUrl) {
        Map<String,String> map=new HashMap();
        boolean flg = huaWeiService.delImgF(fileUrl);
        if(flg){
            map.put("result","success");
        }else {
            map.put("result","fail");
        }
        return map;
    }


    /**
     * 语音 上传的接口
     */
    @RequestMapping(value = "/uploadVoice", method = RequestMethod.POST)
    public Object uploadVoice(@RequestBody MultipartFile file) {
        Map<String,String> map=new HashMap();

        String url = huaWeiService.uploadVoice(file);
        if (url != null && !"".equals(url)) {
            map.put("result","success");
        } else {
            map.put("result","fail");
        }
        return map;
    }
}
