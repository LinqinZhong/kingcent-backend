package com.kingcent.campus.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.goods.GoodsDetailsCommentsPreviewVo;
import com.kingcent.campus.service.GoodsCommentService;
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
