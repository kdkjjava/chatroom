package com.kdkj.intelligent.controller;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.kdkj.intelligent.websocket.ProxyHandler;
import com.kdkj.intelligent.websocket.WebSocketApi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.druid.util.StringUtils;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.MD5Encryption;
import com.kdkj.intelligent.util.MySessionContext;
import com.kdkj.intelligent.util.Result;

@RestController
public class LoginController {

	private static final Logger logger = LogManager.getLogger(LoginController.class);

	@Autowired
	private UsersService usersService;

	@Autowired
	private WebSocketApi webSocketApi;

	@RequestMapping(value = "/phoneifexist", method = RequestMethod.POST)
	public Result phoneifexist(HttpServletRequest request, @RequestBody Users record) {
		List<Users> list = usersService.selectListByUser(record);
		if (list != null && !list.isEmpty())
			return Result.error(10, "");
		return Result.ok();
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result login(HttpServletRequest request, @RequestBody Users record) {
		Result result = loginAction(request, record);
		if ((int)result.get("code")==0){
			webSocketApi.initSocketConnection((Users) result.get("data"),request.getHeader("xxxx"));
		}
		return result;
	}

	@RequestMapping(value = "/tokenLogin", method = RequestMethod.GET)
	public Result getUserByToken(HttpServletRequest request, String token) {
		HttpSession session = request.getSession();
		Users user = new Users();
		user.setToken(token);
		List<Users> list = usersService.selectListByUser(user);
		if (list == null || list.isEmpty())
			return Result.error("您长时间未登录，请重新登录！");
		user = list.get(0);
		Date date = user.getLastLoginTime();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(2, 3);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(new Date());
		boolean bl = cal.after(cal2);
		if (!bl) {
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
	public String keepAlive() {
		return "ok";
	}

	@PostMapping(value = "validate")
	public Result validate(HttpServletRequest request, @RequestBody Users users) {
		Result result = loginAction(request, users);
		if (result.get("code").equals(0)) {
			Users returnUsers = (Users) result.get("data");
			String username = returnUsers.getUsername();
			Users master = usersService.selectByPrimaryKey(Integer.parseInt(returnUsers.getMaster()));
			if (master.getUsername() == null)
				return Result.error("登录失败");
			if (ProxyHandler.masterSessionPools.get(master.getUsername()) != null) {
				ProxyHandler.masterSessionPools.get(master.getUsername()).getOnlineRobots().put(username, true);
			} else {
				return Result.error("请先登陆主账号");
			}
		}
		return result;
	}

	@PostMapping(value = "robotLogout")
	public Result robotLogout(@RequestBody Users users) {
		if (users.getUsername() == null || users.getPassword() == null)
			return Result.error("请求参数不完整");

		Users returnUser = usersService.selectByUsername(users.getUsername());
		try {
			if (!returnUser.getPassword().equals(MD5Encryption.getEncryption(users.getPassword()))) {
				return Result.error("账号密码错误！");
			}
			Users master = usersService.selectMasterByUsername(users.getUsername());
			if (master != null) {
				if (ProxyHandler.masterSessionPools.containsKey(master.getUsername())
						&& !ProxyHandler.masterSessionPools.get(master.getUsername()).getOnlineRobots().isEmpty()) {
					ProxyHandler.masterSessionPools.get(master.getUsername()).getOnlineRobots()
							.remove(users.getUsername());
					return Result.ok("success");
				} else
					return Result.error("masterError");
			} else {
				return Result.error("错误！");
			}

		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return Result.error("服务器错误");
		}
	}

	private Result loginAction(HttpServletRequest request, Users record) {
		String pc = request.getHeader("xxxx");
		Users user = new Users();
		user.setUsername(record.getUsername());
		user.setPhone(record.getPhone());
		if (user.getUsername() == null && user.getPhone() == null) {
			return Result.error("用户名或密码不能为空");
		}
		List<Users> list = usersService.selectListByUser(user);
		if (list == null || list.isEmpty()) {
			return Result.error("用户名或密码错误，请重新登录!");
		}
		user = list.get(0);
		if ("0".equals(user.getStatus()))
			return Result.error("该用户已被禁用，请联系管理员！");
		if (!StringUtils.isEmpty(pc) && "pc".equals(pc) && !"1".equals(user.getType()))
			return Result.error("您不是管理员用户!");
		if ("1".equals(user.getType()) && user.getExpireDate() != null && user.getExpireDate().before(new Date()))
			return Result.error("您的代理商已经过期，请联系管理员!");
		try {
			String newpassword = MD5Encryption.getEncryption(record.getPassword());
			if (newpassword.equals(user.getPassword())) {
				user.setToken("newToken");
				user.setLastLoginTime(new Date());
				usersService.updateByPrimaryKey(user);
				user = usersService.selectByPrimaryKey(user.getId());
				HttpSession session = request.getSession();
				user.setPassword(null);
				session.setAttribute("user", user);
				if ("2".equals(user.getType()))// 若为管理员登陆，则往session域里添加一个标记
					session.setAttribute("admin", "admin");
				if (!StringUtils.isEmpty(pc) && "pc".equals(pc)) {
					MySessionContext.getInstance().delSession(user.getUsername() + "pcxx");
					MySessionContext.getInstance().addSession(user.getUsername() + "pcxx", session);
				} else {
					MySessionContext.getInstance().delSession(user.getUsername());
					MySessionContext.getInstance().addSession(user.getUsername(), session);
				}

				return Result.ok("登录成功", user);
			} else {
				return Result.error("用户名或密码错误，请重新登录!");
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return Result.error("密码加密错误，请重试！");
		}
	}

}
