package com.zero.upload.config.huawei;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: danyiran
 * @Date: 2019/12/21 16:50
 */
@Component
public class HuaWeiConfig {
    /**
     * your ak
     */
    public static String ak;

    @Autowired
    public void setAK(@Value("${huawei.oss.ak}")String huaweiOssAk) {
        ak=huaweiOssAk;
    }

    /**
     * your sk
     */
    public static String sk;

    @Autowired
    public void setSK(@Value("${huawei.oss.sk}")String huaweiOssSk) {
        sk=huaweiOssSk;
    }

    /**
     * 内容审核服务目前支持华北-北京(cn-north-4)以及亚太-香港(ap-southeast-1)
     */
    public static final String region = "cn-north-4";

    public static String endPoint = "https://obs.cn-north-4.myhuaweicloud.com";
    /**
     * 桶的名称
     */
    public static String bucketname = "cj-erp";


    public static class TestContent{
        /**
         * 构建访问文本内容检测服务需要的参数
         */
        public static final String uri = "/v1.0/moderation/text";
        /**
         * 检测内容
         */
        public static final String[] categories = new String[] {"porn","politics", "ad", "abuse", "contraband"};
        /**
         * 文本类型，默认为“content”，即正文内容，当前只支持“content”类型，未来会扩大支持类型范围
         */
        public static final String type ="content";

    }

    /**
     * 当前支持的场景有默认场景和用户自定义场景：
     *
     *  默认场景为：
     *  politics：涉政
     *  porn：涉黄
     *  ad：广告
     *  abuse：辱骂
     *  contraband：违禁品
     *  flood：灌水
     */
    public static class ImgContent{
        /**
         * 构建访问文本内容检测服务需要的参数
         */
        public static final String uri = "/v1.0/moderation/image/batch";
        /**
         * 检测内容
         */
        public static final String[] categories = new String[] {"politics", "terrorism", "porn", "ad"};
        /**
         * 文本类型，默认为“content”，即正文内容，当前只支持“content”类型，未来会扩大支持类型范围
         */
        public static final int threshold =0;

    }

}
