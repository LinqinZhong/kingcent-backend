package com.kingcent.campus.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.goods.GoodsListTypeVo;
import com.kingcent.campus.shop.entity.vo.HomeInfoVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {

    private final static HomeInfoVo homeInfoVo = new HomeInfoVo();
    static {
        List<String> swiper = new ArrayList<>();
        swiper.add("https://cdn-we-retail.ym.tencent.com/tsr/home/v2/banner1.png");
        swiper.add("https://cdn-we-retail.ym.tencent.com/tsr/home/v2/banner2.png");
        swiper.add("https://cdn-we-retail.ym.tencent.com/tsr/home/v2/banner3.png");
        swiper.add("https://cdn-we-retail.ym.tencent.com/tsr/home/v2/banner4.png");
        List<GoodsListTypeVo> goodsListTypeList = new ArrayList<>();
        goodsListTypeList.add(new GoodsListTypeVo(0,"人气推荐"));
        goodsListTypeList.add(new GoodsListTypeVo(1,"促销"));
        goodsListTypeList.add(new GoodsListTypeVo(2,"品质推荐"));
        goodsListTypeList.add(new GoodsListTypeVo(3,"女生最爱"));
        goodsListTypeList.add(new GoodsListTypeVo(4,"男生最爱"));
        homeInfoVo.setTabList(goodsListTypeList);
        homeInfoVo.setSwiper(swiper);
        homeInfoVo.setActivityImg("https://cdn-we-retail.ym.tencent.com/tsr/home/v2/banner1.png");
    }


    @GetMapping("/fetch")
    @ResponseBody
    public Result<HomeInfoVo> fetch(){
        return Result.success(homeInfoVo);
    }
}
