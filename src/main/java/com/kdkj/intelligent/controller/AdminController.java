package com.kdkj.intelligent.controller;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.AdminMsg;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Result;
import com.kdkj.intelligent.websocket.AdminHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/23 18:14
 * @Description:
 **/
@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LogManager.getLogger(AdminController.class);

    @Resource
    private UsersService usersService;

    /**
     * 返回用户列表
     *
     * @return 返回用户的列表，默认返回10条
     */
    @PostMapping("selectUsersList")
    public Result selectUsersList(@RequestBody Users user) {
        if (usersService.hasRecords(user)) {
            List<Users> userList = usersService.selectByPaging(user);
            if (userList != null && !userList.isEmpty()) {
                return Result.ok("success", JSON.toJSONString(userList));
            }
        }
        return Result.error("没有更多的消息");
    }

    @PostMapping("getProxyMsg")
    public Result getProxyMsg(@RequestBody Users user){

        Users proxyUser = usersService.selectProxyNameAndTel(user.getId());
        if (proxyUser==null)
            return Result.error("该用户为离散用户");
        return Result.ok(JSON.toJSONString(proxyUser));
    }

    @PostMapping("upToProxy")
    public Result upToProxy(@RequestBody Users user){
        Users userMsg = usersService.selectByPrimaryKey(user.getId());
        if (!"0".equals(userMsg.getType()))
            return Result.error("该用户不是普通用户");
        user.setType("1");
        user.setProxyLevel("1");
        Integer affect =usersService.updateByPrimaryKey(user);
        if (affect>0)
            return Result.ok("修改成功！");
        else
            return Result.error("修改失败！");
    }

    @PostMapping("proxyToUser")
    public Result proxyToUser(@RequestBody Users user){
        Users userMsg = usersService.selectByPrimaryKey(user.getId());
        if (!"1".equals(userMsg.getType()))
            return Result.error("修改失败，用户类型错误");
        user.setType("0");
        user.setProxyLevel("0");
        Integer affect = usersService.proxyToUser(user);
        if (affect>0)
            return Result.ok("降级成功！");
        else
            return Result.error("降级失败！");
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
            logger.error(e.getMessage());
            return Result.error("failing");
        }
    }
}
