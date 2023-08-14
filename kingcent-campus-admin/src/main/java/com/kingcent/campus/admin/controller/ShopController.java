package com.kingcent.campus.admin.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.vo.shop.ShopNameVo;
import com.kingcent.campus.shop.service.ShopService;
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
}
