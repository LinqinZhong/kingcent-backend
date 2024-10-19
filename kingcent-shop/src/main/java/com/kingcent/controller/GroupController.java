package com.kingcent.controller;

import com.kingcent.common.entity.result.Result;
import com.kingcent.service.GroupService;
import com.kingcent.common.shop.entity.vo.group.GroupLocationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/10/12 15:21
 */
@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @GetMapping("/of_site/{siteId}")
    public Result<List<GroupLocationVo>> search(
            @PathVariable Long siteId
    ){
        return groupService.ofSite(siteId);
    }
}
