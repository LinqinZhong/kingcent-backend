package com.kingcent.controller;

import com.kingcent.common.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.service.SiteService;
import com.kingcent.common.shop.entity.vo.site.SiteLocationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2023/10/12 15:07
 */
@RestController
@RequestMapping("/site")
public class SiteController {
    @Autowired
    private SiteService siteService;

    @GetMapping("/search/{page}/{pageSize}")
    public Result<VoList<SiteLocationVo>> search(@PathVariable Integer page, @PathVariable Integer pageSize, @RequestParam(required = false) String keywords){
        return siteService.search(page, pageSize, keywords);
    }
}
