package com.kingcent.campus.admin.controller;

import com.kingcent.campus.admin.service.ShopService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.ShopEntity;
import com.kingcent.campus.shop.entity.vo.shop.ShopNameVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2023/8/14 19:11
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 获取店铺选项
     * @param keywords 关键词/id
     * @param page 页数
     */
    @GetMapping("/options/{page}")
    public Result<VoList<ShopNameVo>> getShopOptions(
            @RequestParam(required = false) String keywords,
            @PathVariable Integer page
    ){
        return Result.success(shopService.getShopNames(keywords, page));
    }

    @GetMapping("/list/{page}/{pageSize}")
    public Result<VoList<ShopEntity>> list(
            @PathVariable("page") Integer page,
            @PathVariable("pageSize") Integer pageSize
    ){
        return shopService.list(page, pageSize);
    }
}
