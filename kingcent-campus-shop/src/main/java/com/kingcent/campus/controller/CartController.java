package com.kingcent.campus.controller;

import com.kingcent.campus.shop.entity.CartGoodsEntity;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.cart.CartCheckVo;
import com.kingcent.campus.shop.entity.vo.cart.CartVo;
import com.kingcent.campus.shop.entity.vo.purchase.PutCartGoodsVo;
import com.kingcent.campus.service.CartGoodsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kingcent.campus.shop.util.RequestUtil.getUserId;


@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartGoodsService cartGoodsService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    @ResponseBody
    public Result<?> add(HttpServletRequest request, @RequestBody PutCartGoodsVo cartGoodsVo) {
        return cartGoodsService.put(getUserId(request), cartGoodsVo);
    }

    /**
     * 获取用户的购物车列表
     */
    @GetMapping("/list")
    public Result<CartVo> list(HttpServletRequest request){
        return Result.success(cartGoodsService.listByUserId(getUserId(request)));
    }

    /**
     * 更改购物车商品的规格
     */
    @PutMapping("/update_sku/{cartGoodsCode}")
    public Result<CartGoodsEntity> updateSku(HttpServletRequest request, @PathVariable String cartGoodsCode,@RequestParam String specInfo){
        return cartGoodsService.updateSku(getUserId(request),cartGoodsCode,specInfo);
    }

    /**
     * 更改购物车商品的数量
     */
    @PutMapping("/update_count/{cartGoodsCode}")
    public Result<?> updateCount(HttpServletRequest request, @PathVariable String cartGoodsCode,@RequestParam Integer count){
        return cartGoodsService.updateCount(getUserId(request),cartGoodsCode,count);
    }

    @PutMapping("/update_check")
    public Result<?> updateCheck(HttpServletRequest request,@RequestBody CartCheckVo check){
        return cartGoodsService.updateCheck(getUserId(request),check);
    }

    @DeleteMapping("/delete")
    public Result<?> delete(HttpServletRequest request, @RequestParam("cartGoodsCode") List<String> cartGoodsCodes){
        return cartGoodsService.delete(getUserId(request), cartGoodsCodes);
    }
}
