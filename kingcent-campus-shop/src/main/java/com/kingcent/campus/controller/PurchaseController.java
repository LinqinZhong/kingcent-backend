package com.kingcent.campus.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.purchase.CheckPurchaseVo;
import com.kingcent.campus.service.PurchaseService;
import com.kingcent.campus.util.RequestUtil;
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
