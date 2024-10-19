package com.kingcent.community.controller;

import com.kingcent.common.entity.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.community.dto.CreatePostDto;
import com.kingcent.community.service.PostService;
import com.kingcent.community.vo.DocumentVo;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2023/12/12 14:43
 */
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/list/{page}/{pageSize}")
    public Result<VoList<DocumentVo>> list(
            HttpServletRequest request,
            @PathVariable Integer page,
            @PathVariable Integer pageSize
    ){
        String uid = request.getHeader("uid");
        Long userId = uid != null ? Long.valueOf(uid) : null;
        return postService.list(userId, page, pageSize);
    }

    @GetMapping("/detail/{id}")
    public Result<DocumentVo> detail(
            HttpServletRequest request,
            @PathVariable Long id
    ){
        String uid = request.getHeader("uid");
        Long userId = uid != null ? Long.valueOf(uid) : null;
        return postService.get(userId, id);
    }

    @PostMapping("/create")
    public Result<?> create(HttpServletRequest request, @RequestBody CreatePostDto dto){
        Long userId = RequestUtil.getUserId(request);
        return postService.create(userId, dto);
    }

    @PutMapping("/like/{id}")
    public Result<?> like(HttpServletRequest request, @PathVariable Long id){
        Long userId = RequestUtil.getUserId(request);
        return postService.like(userId, id);
    }

    @DeleteMapping ("/delete/{id}")
    public Result<?> delete(HttpServletRequest request, @PathVariable Long id){
        Long userId = RequestUtil.getUserId(request);
        return postService.delete(userId, id);
    }
}
