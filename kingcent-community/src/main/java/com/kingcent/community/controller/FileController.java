package com.kingcent.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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


    @PostMapping("/upload")
    public Result<JSONObject> upload(HttpServletRequest request, @RequestParam("file") MultipartFile uploadFile, String name) {
        Long uid = RequestUtil.getUserId(request);
        File dir = new File(BASE_UPLOAD_PATH+"/"+ uid);
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
        res.put("url", "/"+uid+"/"+file.getName());
        return Result.success("文件上传成功",res);
    }
}
