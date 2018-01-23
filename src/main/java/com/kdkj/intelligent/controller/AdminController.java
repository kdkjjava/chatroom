package com.kdkj.intelligent.controller;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("getProxyList")
    public Result getProxyList(){
        return Result.ok("success", JSON.toJSONString(usersService.selectProxyList()));
    }

    @GetMapping("getUserList")
    public Result getUserList(){

        return null;
    }

    /**
     * 初始化用户列表
     * @return 返回用户的初始化列表，默认返回10条
     */
    @GetMapping("initialUserList")
    public Result initialUserList(){

        return null;
    }

}
