package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.LandEntity;
import com.kingcent.plant.entity.MemberEntity;
import com.kingcent.plant.service.LandService;
import com.kingcent.plant.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:49
 */
@RestController()
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/{pageNum}/{pageSize}")
    public Result<Page<MemberEntity>> list(
            @PathVariable
            Integer pageNum,
            @PathVariable
            Integer pageSize
    ){
        Page<MemberEntity> page = memberService.getPage(pageNum, pageSize);
        return Result.success(page);
    };

    @PostMapping
    public Result<?> addOrUpdate(@RequestBody MemberEntity memberEntity) throws KingcentSystemException {
        return memberService.addOrUpdate(memberEntity);
    }

    @DeleteMapping("/{memberId}")
    public Result<?> delete(@PathVariable Long memberId){
        return memberService.delete(memberId);
    }
}
