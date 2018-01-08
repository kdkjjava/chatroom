package com.kdkj.intelligent.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.MD5Encryption;
import com.kdkj.intelligent.util.Result;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UsersService usersService;

    @RequestMapping(value = "/selectListByUser", method = RequestMethod.POST)
    public Result selectListByUser(HttpServletRequest request, Users record) {
    	PageHelper.startPage(record.getCurrent(), record.getPageSize());
    	PageInfo<Users> page=usersService.selectListByUser(record);
		return Result.ok("查询成功", page);
    }
    
    @RequestMapping(value = "/selectById", method = RequestMethod.GET)
    public Result selectById(HttpServletRequest request,int id) {
    	Users user=usersService.selectByPrimaryKey(id);
		return Result.ok("查询成功", user);
    }
    
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result update(HttpServletRequest request,Users record) {
    	record.setPassword(null);
    	usersService.updateByPrimaryKey(record);
		return Result.ok("修改成功");
    }
    
    @RequestMapping(value = "/modifyPwd", method = RequestMethod.POST)
    public Result modifyPwd(HttpServletRequest request,Users record) {
    	try {
			record.setPassword(MD5Encryption.getEncryption("111111"));
			usersService.updateByPrimaryKey(record);
			return Result.ok("修改成功");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Result.error("修改密码失败，请重试！");
		}
    }
    
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public Result addUser(HttpServletRequest request,Users record) {
    	Users user=new Users();
    	user.setUsername(record.getUsername()==null?null:record.getUsername());
    	user.setPhone(record.getPhone()==null?null:record.getPhone());
    	List<Users> list=usersService.selectListByUser(user).getList();
    	if(list.size()>0)
    	return Result.error("用户名或电话号码已存在！");
    	try {
			usersService.insert(record);
			return Result.ok("新增用户成功",record);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Result.error("密码加密失败，请重试！");
		}
    }
    
    
    
    
}
