package com.focus.focus.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.focus.focus.api.util.FileUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class QiNiuService {
    private static final Logger logger = LoggerFactory.getLogger(QiNiuService.class);

    // 设置好账号的ACCESS_KEY和SECRET_KEY
    private final String ACCESS_KEY = "K-b_ew9m6lJ1qBKgKjHJeNB9X54E0gfF-zR0mZaU";
    private final String SECRET_KEY = "m7eL539Mgk1VOP-9wZJ5Cw2KhZbeicpYzKsX6pTj";
    // 要上传的空间
    private final String bucketName = "focus-platform";

    // 密钥配置
    private Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    // 构造一个带指定Zone对象的配置类
    private Configuration cfg = new Configuration(Zone.huanan());

    private UploadManager uploadManager = new UploadManager(cfg);

    // 测试域名，只有30天有效期
    private final static String QINIU_IMAGE_DOMAIN = "http://r9gseewjp.hn-bkt.clouddn.com/";

    // 简单上传，使用默认策略，只需要设置上传的空间名就可以了
    private String getUpToken() {
        return auth.uploadToken(bucketName);
    }

    public String saveImage(MultipartFile file,String fileName) throws IOException {
        try {
            logger.info("PRE dotPos");
            int dotPos = fileName.lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            logger.info("PRE fileExt");
            String fileExt = fileName.substring(dotPos + 1).toLowerCase();
            // 判断是否是合法的文件后缀
            if (!FileUtil.isFileAllowed(fileExt)) {
                return null;
            }
            String finalFileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            logger.info("QI NIU FILENAME: {}",finalFileName);
            // 调用put方法上传
            Response res = uploadManager.put(file.getBytes(), finalFileName, getUpToken());
            // 打印返回的信息
            if (res.isOK() && res.isJson()) {
                // 返回这张存储照片的地址
                return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(res.bodyString()).get("key");
            } else {
                logger.error("七牛异常:" + res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            // 请求失败时打印的异常的信息
            logger.error("七牛异常:" + e.getMessage());
            return null;
        }
    }
}
