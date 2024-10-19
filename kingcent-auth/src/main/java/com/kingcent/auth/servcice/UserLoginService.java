package com.kingcent.auth.servcice;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.auth.entity.UserLoginEntity;

public interface UserLoginService extends IService<UserLoginEntity> {
    Long check(JSONObject object);
}
