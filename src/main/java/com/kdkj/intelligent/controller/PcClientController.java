package com.kdkj.intelligent.controller;

import com.kdkj.intelligent.entity.KeyWord;
import com.kdkj.intelligent.service.KeyWordService;
import com.kdkj.intelligent.util.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * powered by IntelliJ IDEA
 * 该
 * @Author: unknown
 * @Date: 2018/1/11 17:00
 * @Description:
 **/
@RestController
@RequestMapping("pcClient")
public class PcClientController {

    @Resource
    private KeyWordService keyWordService;

    @PutMapping("modifyKeyWords")
    public Result modifyKeyWords(@RequestBody KeyWord keyWord){
        Integer i = keyWordService.update(keyWord);
        if (i>0)
            return Result.ok("success");
        else return Result.ok("defeat");
    }

    @GetMapping("getKeyWords")
    public Result getKeyWords(@RequestParam("masterId") String masterId){
        return Result.ok("success",keyWordService.getByMaster(masterId)).put("status","select");
    }

}
