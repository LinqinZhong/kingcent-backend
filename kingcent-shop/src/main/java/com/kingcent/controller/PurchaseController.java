package com.kingcent.controller;

import com.kingcent.common.entity.result.Result;
import com.kingcent.common.shop.entity.vo.purchase.CheckPurchaseVo;
import com.kingcent.service.PurchaseService;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    /**
     * 获取购买商品信息
     */
    @PostMapping("/check")
    @ResponseBody
    public Result<?> getPurchaseInfo(
            HttpServletRequest request, @RequestBody CheckPurchaseVo vo
            ){
        return purchaseService.getPurchaseInfo(RequestUtil.getUserId(request), vo);
    }
}
