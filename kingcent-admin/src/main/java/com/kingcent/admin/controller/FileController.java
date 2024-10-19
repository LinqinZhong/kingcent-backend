package com.kingcent.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.kingcent.common.entity.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

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
        File dir = new File(BASE_UPLOAD_PATH);
        if(!dir.exists() && !dir.mkdirs()){
            return Result.fail("文件创建失败");
        }
        String fName = uploadFile.getOriginalFilename();
        assert fName != null;
        File file = new File(dir,name+"."+fName.substring(fName.lastIndexOf(".")+1));
        try {
            uploadFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject res = new JSONObject();
        res.put("url", VISIT_PATH +"/"+file.getName());
        return Result.success("文件上传成功",res);
    }
}
