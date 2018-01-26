package com.kdkj.intelligent.controller;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.AdminMsg;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Result;
import com.kdkj.intelligent.websocket.AdminHandler;
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
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/admin")
public class AdminController {

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
