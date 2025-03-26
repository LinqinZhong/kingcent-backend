package com.kingcent.plant.constroller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.kingcent.common.result.Result;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件控制器
 * @author rainkyzhong
 * @date 2023/8/15 11:53
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("C:/AppServ/www/aaaaaaa")
//    @Value("/www/wwwroot/file.intapter.cn/upload_files")
    private String BASE_UPLOAD_PATH;

    @Value("http://192.168.137.1/aaaaaaa")
//    @Value("http://file.intapter.cn/upload_files")
    private String VISIT_PATH;


    @PostMapping("/upload")
    public Result<JSONObject> upload(@RequestParam("file") MultipartFile uploadFile, String name) {
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        UploadManager uploadManager = new UploadManager(cfg);
        String accessKey = "lei-3-pMikOTUW1wpJguGxeIZjlT1qBKBBLAxUvh";
        String secretKey = "8FHY3P63s40sIaz8C6D71F-w-c4OGNx-Frqw5UP5";
        String bucket = "ssm2018";

        Auth auth = Auth.create(accessKey, secretKey);
        StringMap putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}");
        String upToken = auth.uploadToken(bucket,null,86400000,putPolicy);

        try {
            File file = File.createTempFile("temp", null);
            uploadFile.transferTo(file);
            if(file.length() == 0) return Result.fail("文件不能为空");
            Response response = uploadManager.put(file, null, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url","http://source.intapter.cn/"+putRet.key);
            return Result.success(jsonObject);
        } catch (QiniuException ex) {
            ex.printStackTrace(System.out);
            if (ex.response != null) {
                System.err.println(ex.response);

                try {
                    String body = ex.response.toString();
                    System.err.println(body);
                } catch (Exception ignored) {
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.fail("上传失败");
    }
}
