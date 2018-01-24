package com.kdkj.intelligent.controller;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
     * 初始化用户列表
     * @return 返回用户的初始化列表，默认返回10条
     */
    @PostMapping("selectUsersList")
    public Result initialUserList(@RequestBody Users user){
        if (usersService.hasRecords(user)){
                return Result.ok("success",JSON.toJSONString(usersService.selectByPaging(user)));
        }
        return Result.error("没有更多的消息");
    }

}
