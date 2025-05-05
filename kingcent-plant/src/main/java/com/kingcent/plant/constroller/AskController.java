package com.kingcent.plant.constroller;

import com.kingcent.common.result.Result;
import com.kingcent.plant.service.AskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ask")
public class AskController {

    @Autowired
    private  AskService askService;

    @GetMapping()
    public Result<String> ask(@RequestParam("text") String text) {
        return new Result<String>().success(true).code(200).data(askService.ask(text));
    }
}