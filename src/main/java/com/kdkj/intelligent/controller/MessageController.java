package com.kdkj.intelligent.controller;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.AdminMsg;
import com.kdkj.intelligent.util.Result;
import com.kdkj.intelligent.websocket.AdminHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/17 14:09
 * @Description:
 **/
@RestController
@RequestMapping("message")
public class MessageController {
    private static final Logger logger = LogManager.getLogger(MessageController.class);
    /**
     * 获取用户建议
     *
     * @return
     */
    @GetMapping("getAdvice")
    public Result advice() {
        return Result.ok("success", AdminHandler.adviceMsg);
    }

    /**
     * 获得管理员推送消息
     *
     * @return
     */
    @GetMapping("broadcast")
    public Result getBroadCast() {
        return Result.ok("success", AdminHandler.broadCastMsg);
    }

    /**
     * 新增建议消息
     *
     * @param adminMsg
     * @return
     */
    @PostMapping("addAdvice")
    public Result addService(@RequestBody AdminMsg adminMsg) {
        try {
            AdminHandler.adviceMsg.add(adminMsg);
            return Result.ok("success");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Result.error("failing");
        }
    }


}
