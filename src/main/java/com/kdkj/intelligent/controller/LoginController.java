package com.kdkj.intelligent.controller;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
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

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.GroupTeamService;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.MD5Encryption;
import com.kdkj.intelligent.util.Result;

@CrossOrigin(origins = "*")
@RestController
public class LoginController {
	@Autowired
	private UsersService usersService;
	@Autowired
	private GroupTeamService groupTeamService;
	

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result login(HttpServletRequest request, @RequestBody Users record) {
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
		try {
			String newpassword = MD5Encryption.getEncryption(record.getPassword());
			if (newpassword.equals(user.getPassword())) {
				user.setToken("newToken");
				user.setLastLoginTime(new Date());
				usersService.updateByPrimaryKey(user);
				user = usersService.selectByPrimaryKey(user.getId());
				HttpSession session = request.getSession();
				user.setPassword(null);
				//添加用户拥有的群到session
				GroupTeam groupTeam=new GroupTeam();
				groupTeam.setMasterId(user.getId());
				List<GroupTeam> grouplist=groupTeamService.selectListByGroup(groupTeam);
				String groups=",";
				for(GroupTeam gt:grouplist) {
					groups+=(String.valueOf(gt.getId())+",");
				}
				session.setAttribute("groups", groups);
				
				session.setAttribute("user", user);
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
		user.setToken((String)request.getParameter("token"));
		user.setToken(token);
		List<Users> list = usersService.selectListByUser(user);
		user=list.get(0); 
		Date date=user.getLastLoginTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(2,3);
		cal.getTime();
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(new Date());
		boolean bl = cal.after(cal2);
		if(!bl) {
			return Result.error("您长时间未登录，请重新登录！");
		}
		user.setLastLoginTime(new Date());
		user.setPassword(null);
		usersService.updateByPrimaryKey(user);
		
		//添加用户拥有的群到session
		GroupTeam groupTeam=new GroupTeam();
		groupTeam.setMasterId(user.getId());
		List<GroupTeam> grouplist=groupTeamService.selectListByGroup(groupTeam);
		String groups=",";
		for(GroupTeam gt:grouplist) {
			groups+=(String.valueOf(gt.getId())+",");
		}
		session.setAttribute("groups", groups);
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
}
