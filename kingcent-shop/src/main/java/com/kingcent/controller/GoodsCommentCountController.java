package com.kingcent.controller;

import com.kingcent.common.shop.entity.GoodsCommentCountEntity;
import com.kingcent.common.entity.result.Result;
import com.kingcent.service.GoodsCommentCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods_comment_count")
public class GoodsCommentCountController {
    @Autowired
    private GoodsCommentCountService goodsCommentCountService;

    @GetMapping("/{id}")
    public Result<GoodsCommentCountEntity> getById(@PathVariable("id") Long id) {
        return Result.success(goodsCommentCountService.getById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody GoodsCommentCountEntity goodsCommentCountEntity) {
        if(goodsCommentCountService.save(goodsCommentCountEntity)){
            return Result.success();
        }else{
            return Result.fail("添加失败");
        }
    }

    @PutMapping
    public Result<?> updateById(@RequestBody GoodsCommentCountEntity goodsCommentCountEntity) {
        if(goodsCommentCountService.updateById(goodsCommentCountEntity)){
            return Result.success();
        }else{
            return Result.fail("修改失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> removeById(@PathVariable("id") Long id) {
        if(goodsCommentCountService.removeById(id)){
            return Result.success();
        }else{
            return Result.fail("删除失败");
        }
    }
}
