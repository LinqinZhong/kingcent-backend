package com.kingcent.controller;

import com.kingcent.common.entity.result.Result;
import com.kingcent.service.WantKeywordService;
import com.kingcent.service.WantService;
import com.kingcent.common.shop.entity.vo.want.WantKeywordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/17 7:07
 */
@RestController
@RequestMapping("/want")
public class WantController {

    @Autowired
    private WantService wantService;

    @Autowired
    private WantKeywordService wantKeywordService;

    @GetMapping("/keywords/{groupId}")
    public Result<List<WantKeywordVo>> list(@PathVariable Long groupId){
        return Result.success(wantKeywordService.getTopKeywords(groupId));
    }
}