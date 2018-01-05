package com.kdkj.intelligent.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Result;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UsersService usersService;

    @RequestMapping(value = "/selectListByUser", method = RequestMethod.GET)
    public Result selectListByUser(Users record) {
    	
    	PageHelper.startPage(record.getCurrent(), record.getPageSize());
    	PageInfo<Users> page=usersService.selectListByUser(record);
		return Result.ok("查询成功", page);
    }
}
