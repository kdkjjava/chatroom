package com.kdkj.intelligent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
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

    @RequestMapping(value = "/selectListByUser", method = RequestMethod.POST)
    public Result selectListByUser(HttpRequest request,Users record) {
    	PageHelper.startPage(record.getCurrent(), record.getPageSize());
    	PageInfo<Users> page=usersService.selectListByUser(record);
		return Result.ok("查询成功", page);
    }
    
    @RequestMapping(value = "/selectById", method = RequestMethod.GET)
    public Result selectById(HttpRequest request,int id) {
    	Users user=usersService.selectByPrimaryKey(id);
		return Result.ok("查询成功", user);
    }
    
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result update(HttpRequest request,Users record) {
    	usersService.updateByPrimaryKey(record);
		return Result.ok("修改成功");
    }
    
    
    
    
}
