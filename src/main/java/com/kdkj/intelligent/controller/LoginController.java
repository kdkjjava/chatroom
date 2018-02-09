package com.kdkj.intelligent.controller;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.druid.util.StringUtils;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.GroupTeamService;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.MD5Encryption;
import com.kdkj.intelligent.util.Result;

@RestController
public class LoginController {
	@Autowired
	private UsersService usersService;

	@RequestMapping(value = "/phoneifexist", method = RequestMethod.POST)
	public Result phoneifexist(HttpServletRequest request, @RequestBody Users record) {
		List<Users> list = usersService.selectListByUser(record);
		if(list!=null && !list.isEmpty())
			return Result.error(10, "");
		return Result.ok();
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result login(HttpServletRequest request,@RequestBody Users record) {
		String pc=request.getHeader("xxxx");
		Users user = new Users();
		user.setUsername(record.getUsername() != null ? record.getUsername() : null);
		user.setPhone(record.getPhone() != null ? record.getPhone() : null);
		if (user.getUsername() == null && user.getPhone() == null) {
			return Result.error("用户名或密码不能为空");
		}
		List<Users> list = usersService.selectListByUser(user);
		if (list == null || list.isEmpty()) {
			return Result.error("用户名或密码错误，请重新登录!");
		}
		user = list.get(0);
		if("0".equals(user.getStatus()))
			return Result.error("该用户已被禁用，请联系管理员！");
		if(!StringUtils.isEmpty(pc) &&"pc".equals(pc) && !"1".equals(user.getType())) 
			return Result.error("您不是管理员用户!");
		if("1".equals(user.getType())&&user.getExpireDate()!=null && user.getExpireDate().before(new Date()))
			return Result.error("您的代理商已经过期，请联系管理员!");
		try {
			String newpassword = MD5Encryption.getEncryption(record.getPassword());
			if (newpassword.equals(user.getPassword())) {
				if(user.getLastLoginTime()==null) {
					user.setToken("newToken");
					user.setLastLoginTime(new Date());
					usersService.updateByPrimaryKey(user);
				}else {
					Calendar cal1=Calendar.getInstance();
					cal1.setTime(user.getLastLoginTime());
					cal1.add(2,3);
					if(cal1.getTime().before(new Date())) {
						user.setToken("newToken");
						user.setLastLoginTime(new Date());
						usersService.updateByPrimaryKey(user);
					}
				}
				user = usersService.selectByPrimaryKey(user.getId());
				HttpSession session = request.getSession();
				user.setPassword(null);
				session.setAttribute("user", user);
				if ("2".equals(user.getType()))//若为管理员登陆，则往session域里添加一个标记
				    session.setAttribute("admin","admin");
				return Result.ok("登录成功", user);
			} else {
				return Result.error("用户名或密码错误，请重新登录!");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Result.error("密码加密错误，请重试！");
		}
	}

	@RequestMapping(value = "/tokenLogin", method = RequestMethod.GET)
	public Result getUserByToken(HttpServletRequest request,String token) {
		HttpSession session=request.getSession();
		Users user = new Users();
		user.setToken(token);
		List<Users> list = usersService.selectListByUser(user);
		if(list==null || list.isEmpty())
			return Result.error("您长时间未登录，请重新登录！");
		user=list.get(0); 
		Date date=user.getLastLoginTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(2,3);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(new Date());
		boolean bl = cal.after(cal2);
		if(!bl) {
			return Result.error("您长时间未登录，请重新登录！");
		}
		user.setLastLoginTime(new Date());
		user.setPassword(null);
		usersService.updateByPrimaryKey(user);
		session.setAttribute("user", user);
		return Result.ok("", user);
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public Result logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("user") != null) {
			session.removeAttribute("user");
		}
		if (request.getAttribute("token") != null) {
			request.removeAttribute("token");
		}
		return Result.ok();
	}

	@GetMapping(value = "keepAlive")
	public String keepAlive(){
		return "ok";
	}
}
