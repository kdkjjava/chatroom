package com.kdkj.intelligent.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.MD5Encryption;
import com.kdkj.intelligent.util.Result;

@CrossOrigin(origins="*")
@RestController
public class LoginController {
	@Autowired
	private UsersService usersService;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public Result login(HttpServletRequest request,@RequestBody Users record) {
		Users user = new Users();
		user.setUsername(record.getUsername() != null ? record.getUsername() : null);
		user.setPhone(record.getPhone() != null ? record.getPhone() : null);
		if (user.getUsername() == null && user.getPhone() == null) {
			return Result.error("用户名或密码不能为空");
		}
		List<Users> list = usersService.selectListByUser(user);
		if (list == null || list.size() == 0) {
			return Result.error("用户名或密码错误，请重新登录!");
		}
		user = list.get(0);
		user.setPassword(null);
		try {
			String newpassword = MD5Encryption.getEncryption(record.getPassword());
			if (newpassword.equals(user.getPassword())) {
				user.setToken("newToken");
				user.setLastLoginTime(new Date());
				usersService.updateByPrimaryKey(user);
				HttpSession session = request.getSession();
				session.setAttribute("user", user);
				return Result.ok("登录成功",user);
			} else {
				return Result.error("用户名或密码错误，请重新登录!");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Result.error("密码加密错误，请重试！");
		}

	}

}
