package com.kingcent.campus.auth.servcice;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.auth.entity.AdminLoginEntity;

public interface AdminLoginService extends IService<AdminLoginEntity> {
    Long check(JSONObject object);
}
