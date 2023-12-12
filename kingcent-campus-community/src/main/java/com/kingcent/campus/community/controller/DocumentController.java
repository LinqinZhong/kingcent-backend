package com.kingcent.campus.community.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.community.dto.CreateDocumentDto;
import com.kingcent.campus.community.entity.DocumentEntity;
import com.kingcent.campus.community.service.DocumentService;
import com.kingcent.campus.community.vo.DocumentVo;
import com.kingcent.campus.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2023/12/12 14:43
 */
@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/list/{page}/{pageSize}")
    public Result<VoList<DocumentVo>> list(
            @PathVariable Integer page,
            @PathVariable Integer pageSize
    ){
        return documentService.list(page, pageSize);
    }

    @PostMapping("/create")
    public Result<?> create(HttpServletRequest request, @RequestBody CreateDocumentDto dto){
        Long userId = RequestUtil.getUserId(request);
        return documentService.create(userId, dto);
    }
}
