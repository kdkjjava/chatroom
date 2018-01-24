package com.kdkj.intelligent.controller;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Result;
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

}
