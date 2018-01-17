package com.kdkj.intelligent.controller;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.AdminMsg;
import com.kdkj.intelligent.util.Result;
import com.kdkj.intelligent.websocket.AdminHandler;
import org.springframework.web.bind.annotation.*;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/17 14:09
 * @Description:
 **/
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("message")
public class MessageController {

    /**
     * 获取用户建议
     * @return
     */
    @GetMapping("getAdvice")
    public Result advice(){
        return Result.ok("success",AdminHandler.adviceMsg);
    }

    /**
     * 获得管理员推送消息
     * @return
     */
    @GetMapping("broadcast")
    public Result getBroadCast(){
        return Result.ok("success",AdminHandler.broadCastMsg);
    }

    /**
     * 新增建议消息
     * @param adminMsg
     * @return
     */
    @PostMapping("addService")
    public Result addService(@RequestBody AdminMsg adminMsg){
        try{
            AdminHandler.adviceMsg.add(adminMsg);
            return Result.ok("success");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("failing");
        }
    }

    /**
     * 新增广播消息
     * @param adminMsg
     * @return
     */
    @PostMapping("addBroadCast")
    public Result addBroadCast(@RequestBody AdminMsg adminMsg){
        try{
            AdminHandler.broadCastMsg.add(adminMsg);
            return Result.ok("success");
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("failing");
        }
    }
}
