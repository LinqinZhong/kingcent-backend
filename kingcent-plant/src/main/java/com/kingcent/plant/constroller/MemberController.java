package com.kingcent.plant.constroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.utils.RequestUtil;
import com.kingcent.common.utils.NumberUtils;
import com.kingcent.plant.entity.LandEntity;
import com.kingcent.plant.entity.MemberEntity;
import com.kingcent.plant.service.LandService;
import com.kingcent.plant.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            Integer pageSize,
            @RequestParam(required = false)
            String name,
            @RequestParam(required = false)
            String no,
            @RequestParam(required = false)
            String username,
            @RequestParam(required = false)
            String email,
            @RequestParam(required = false)
            String mobile
    ){
        Page<MemberEntity> page = memberService.getPage(pageNum, pageSize,name, no, username, email, mobile);
        return Result.success(page);
    };

    @GetMapping("/listByIds")
    public Result<List<MemberEntity>> list(@RequestParam String memberIds){
        List<Long> ids = NumberUtils.splitLong(memberIds,",",true);
        if(ids == null) return Result.fail("请指定ID");
        return Result.success(memberService.listByIds(ids));
    };

    @PostMapping
    public Result<?> addOrUpdate(@RequestBody MemberEntity memberEntity) throws KingcentSystemException {
        return memberService.addOrUpdate(memberEntity);
    }

    @GetMapping("/current")
    public Result<MemberEntity> getCurrent(HttpServletRequest request){
        Long userId = RequestUtil.getUserId(request);
        return memberService.getByUserId(userId);
    }

    @DeleteMapping("/{memberId}")
    public Result<?> delete(@PathVariable Long memberId){
        return memberService.delete(memberId);
    }
}
