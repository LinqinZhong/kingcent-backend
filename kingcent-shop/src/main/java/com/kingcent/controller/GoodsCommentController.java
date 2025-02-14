package com.kingcent.controller;

import com.kingcent.common.result.Result;
import com.kingcent.service.GoodsCommentService;
import com.kingcent.common.shop.entity.vo.goods.GoodsDetailsCommentsPreviewVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/goods_comment")
@RestController
public class GoodsCommentController {

    @Autowired
    private GoodsCommentService goodsCommentService;

    @GetMapping("/preview/{spu_id}")
    @ResponseBody
    public Result<GoodsDetailsCommentsPreviewVo> preview(@PathVariable("spu_id") Long spuId){
        return Result.success(goodsCommentService.getGoodsDetailsCommentsPreview(spuId));
    }
}
