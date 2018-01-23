package com.kdkj.intelligent.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kdkj.intelligent.entity.Friendship;
import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.FriendshipService;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.MD5Encryption;
import com.kdkj.intelligent.util.Result;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UsersService usersService;
	@Autowired
	private FriendshipService friendshipService;

	@RequestMapping(value = "/selectListByUser", method = RequestMethod.POST)
	public Result selectListByUser(HttpServletRequest request, @RequestBody Users record) {
		PageHelper.startPage(record.getCurrent(), record.getPageSize());
		List<Users> list = usersService.selectListByUser(record);
		PageInfo<Users> page = new PageInfo(list);
		return Result.ok("查询成功", page);
	}

	@RequestMapping(value = "/selectById", method = RequestMethod.GET)
	public Result selectById(HttpServletRequest request, int id) {
		Users user = usersService.selectByPrimaryKey(id);
		return Result.ok("查询成功", user);
	}
	
	@RequestMapping(value = "/deletUserById", method = RequestMethod.GET)
	public Result deletUserById(HttpServletRequest request, int id) {
		if(!"1".equals(getUser(request).getType()))
			return Result.error("您无此权限！");
		usersService.deleteByPrimaryKey(id);
		return Result.ok("删除成功");
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Result update(HttpServletRequest request, @RequestBody Users record) {
		record.setPassword(null);
		usersService.updateByPrimaryKey(record);
		return Result.ok("修改成功");
	}

	@RequestMapping(value = "/modifyPwd", method = RequestMethod.POST)
	public Result modifyPwd(HttpServletRequest request, @RequestBody Users record) {
		Users user = usersService.selectByPrimaryKey(record.getId());
		try {
			String newPwd = MD5Encryption.getEncryption(record.getPassword());
			if (newPwd.equals(user.getPassword())) {
				user.setPassword(MD5Encryption.getEncryption(record.getNickname()));
				usersService.updateByPrimaryKey(user);
				return Result.ok("修改成功");
			} else {
				return Result.error("密码不正确");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Result.error("修改密码失败");
		}
	}

	@RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
	public Result resetPwd(HttpServletRequest request, @RequestBody Users record) {
		Users nowUser = getUser(request);
		if (!"1".equals(nowUser.getType())) {
			return Result.error("当前用户无此权限!");
		}
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
	public Result addUser(HttpServletRequest request, @RequestBody Users record) {
		Users user = new Users();
		user.setUsername(record.getUsername() == null ? null : record.getUsername());
		user.setPhone(record.getPhone() == null ? null : record.getPhone());
		List<Users> list = usersService.selectListByUser(user);
		if (list != null && !list.isEmpty())
			return Result.error("用户名或电话号码已存在！");
		try {
			if (StringUtils.isEmpty(record.getType())) {
				record.setType("0");
			} else {
				if ("1".equals(record.getType())) {
					if (!"2".equals(getUser(request).getType())) {
						return Result.error("你无权限添加代理商!");
					}
				} else if (!"0".equals(record.getType())) {
					return Result.error("添加的用户类型有误！");
				}
			}
			usersService.insert(record);
			return Result.ok("新增用户成功", record);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Result.error("密码加密失败，请重试！");
		}
	}

	@RequestMapping(value = "/findGroups", method = RequestMethod.GET)
	public Result findGroups(HttpServletRequest request) {
		List<GroupTeam> list = usersService.selectGroupByUserId(getUser(request).getId());
		if (list != null && !list.isEmpty())
			return Result.ok("", list);
		return Result.ok();
	}

	@RequestMapping(value = "/findMyFriends", method = RequestMethod.GET)
	public Result findMyFriends(HttpServletRequest request, Long id) {
		List<Users> list = usersService.findMyFriends(id);
		return Result.ok("", list);
	}
	
	@RequestMapping(value = "/findNoGroupUsers", method = RequestMethod.GET)
	public Result findNoGroupUsers(HttpServletRequest request) {
		if(!"1".equals(getUser(request).getType()))
			return Result.error("您无此权限！");
		List<Users> list = usersService.selectMemberIds();
		return Result.ok("", list);
	}

	@RequestMapping(value = "/addFriend", method = RequestMethod.GET)
	public Result addFriend(HttpServletRequest request, Integer id,String remarkName) {
		if(usersService.selectByPrimaryKey(id)==null)
			return Result.error("不存在该用户，请检查后再提交！");
		Users user = getUser(request);
		Integer myId = user.getId();
		if(myId==id) {
			return Result.error("不能添加自己为好友!");
		}
		Friendship record = new Friendship();
		record.setUid1(id);
		record.setUid2(myId);
		List<Friendship> list = friendshipService.selectByAttribute(record);
		if (list == null || list.isEmpty()) {
			record.setUid1(myId);
			record.setUid2(id);
			list = friendshipService.selectByAttribute(record);
			if (list == null || list.isEmpty()) {
				record.setBuildTime(new Date());
				record.setRemarkName1(remarkName);
				friendshipService.insert(record);
				return Result.ok("","添加好友成功");
			}
			return Result.error("你们已经是好友，不能重复添加！");
		}
		return Result.error("你们已经是好友，不能重复添加！");
	}
	
	@RequestMapping(value = "/modifyRemarkName", method = RequestMethod.GET)
	public Result modifyRemarkName(HttpServletRequest request, Integer id,String remarkName) {
		Integer myId=getUser(request).getId();
		Friendship record = new Friendship();
		record.setUid1(myId);
		record.setUid2(id);
		List<Friendship> list = friendshipService.selectByAttribute(record);
		if(list!=null && !list.isEmpty()) {
			record=list.get(0);
			record.setRemarkName1(remarkName);
			friendshipService.updateByPrimaryKey(record);
			return Result.ok("修改备注成功");
		}else {
			record.setUid1(id);
			record.setUid2(myId);
			list = friendshipService.selectByAttribute(record);
			if (list != null && !list.isEmpty()) {
				record = list.get(0);
				record.setRemarkName2(remarkName);
				friendshipService.updateByPrimaryKey(record);
				return Result.ok("修改备注成功");
			}
			return Result.error("修改备注失败");
		}
	}
	
	@RequestMapping(value = "/delFriend", method = RequestMethod.GET)
	public Result delFriend(HttpServletRequest request, Integer id) {
		Integer myId=getUser(request).getId();
		Friendship record = new Friendship();
		record.setUid1(myId);
		record.setUid2(id);
		List<Friendship> list = friendshipService.selectByAttribute(record);
		if(list!=null && !list.isEmpty()) {
			record=list.get(0);
			friendshipService.deleteByPrimaryKey(record.getId());
			return Result.ok("","删除成功");
		}else {
			record.setUid1(id);
			record.setUid2(myId);
			list = friendshipService.selectByAttribute(record);
			if(list!=null && !list.isEmpty()) {
				record=list.get(0);
				friendshipService.deleteByPrimaryKey(record.getId());
				return Result.ok("","删除成功");
			}
			return Result.error("删除失败，你们不是好友");
		}
	}

	@GetMapping("getProxyList")
	public Result getProxyList(){
		return Result.ok("success", JSON.toJSONString(usersService.selectProxyList()));
	}

	private Users getUser(HttpServletRequest request) {
		return (Users) request.getSession().getAttribute("user");
	}



}
