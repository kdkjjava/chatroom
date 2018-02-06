package com.kdkj.intelligent.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Members;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.GroupTeamService;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Result;

@RestController
@RequestMapping("/group")
public class GroupTeamController {
	@Autowired
	private GroupTeamService groupTeamService;
	@Autowired
	private UsersService usersService;

	@RequestMapping(value = "/selectListByGroup", method = RequestMethod.POST)
	public Result selectListByGroup(HttpServletRequest request, @RequestBody GroupTeam record) {
		List<GroupTeam> list = groupTeamService.selectListByGroup(record);
		return Result.ok("查询成功", list);
	}

	@RequestMapping(value = "/selectById", method = RequestMethod.GET)
	public Result selectById(HttpServletRequest request, int id) {
		GroupTeam groupTeam = groupTeamService.selectByPrimaryKey(id);
		return Result.ok("查询成功", groupTeam);
	}

	// 解散群
	@RequestMapping(value = "/deleteGroup", method = RequestMethod.GET)
	public Result deleteGroup(HttpServletRequest request, Integer userId, Integer groupId) {
		if (userId == null || groupId == null) 
			return Result.error("参数不能为空，请检查！");
		boolean bl = ifGroupMaster(request, groupId);
		if (bl && !"2".equals(getUser(request).getType()))
			return Result.error("您没有此权限!");
		groupTeamService.deleteByPrimaryKey(groupId);
		return Result.ok("删除成功");
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Result update(HttpServletRequest request, @RequestBody GroupTeam record) {
		Users user = getUser(request);
		if (!"2".equals(user.getType()))
			return Result.error("您没有此权限");
		groupTeamService.updateByPrimaryKey(record);
		return Result.ok("修改成功");
	}

	@RequestMapping(value = "/updateGroupName", method = RequestMethod.POST)
	public Result updateGroupName(HttpServletRequest request, @RequestBody GroupTeam record) {
		// 检查登录用户是否是此群群主
		if (ifGroupMaster(request, record.getId()))
			return Result.error("用户无此权限！");
		// 群主只能修改群名称等字段
		record.setBuildTime(null);
		record.setGroupId(null);
		record.setMasterId(null);
		record.setStatus(null);
		record.setType(null);
		record.setUpperLimit(null);
		groupTeamService.updateByPrimaryKey(record);
		return Result.ok("修改成功");
	}

	@RequestMapping(value = "/addGroup", method = RequestMethod.POST)
	public Result addGroup(HttpServletRequest request, @RequestBody GroupTeam record) {
		if (!"1".equals(getUser(request).getType()))
			return Result.error("当前用户无此权限!");
		if (record.getMasterId() == null || record.getGroupName() == null) 
			return Result.error("请检查参数是否填写完整");
		groupTeamService.insert(record);
		return Result.ok("新建群成功", record);
	}

	/**
	 * 增加群成员
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addMembers", method = RequestMethod.POST)
	public Result addMembers(HttpServletRequest request, @RequestBody Map map) {
		Integer id = (Integer) map.get("id");
		String userIds = (String) map.get("userIds");
		if (!"1".equals(getUser(request).getType()))
			return Result.error("您无权限添加用户");
		if (groupTeamService.selectByPrimaryKey(id) == null)
			return Result.error("该群不存在，请检查后重新提交！");
		String[] ids = userIds.split(",");
		for (String userId : ids) {
			if (usersService.selectByPrimaryKey(Integer.valueOf(userId)) == null)
				return Result.error("用户id错误，请检查后再试");
		}
		Integer masterId = getUser(request).getId();
		int i = groupTeamService.addMember(masterId, id, userIds);
		if (i > 0)
			return Result.error(1, "有" + i + "个用户是其他代理商的用户，不能添加");
		return Result.ok("添加用户成功！");
	}

	/**
	 * 删除群成员
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delMembers", method = RequestMethod.POST)
	public Result delMembers(HttpServletRequest request, @RequestBody Members record) {
		if (record.getGroupId() == null || record.getUserId() == null)
			return Result.error("请确认参数是否输入！");
		if (ifGroupMaster(request, record.getGroupId()) && !record.getUserId().equals(getUser(request).getId())) 
			return Result.error("您没有此权限!");
		if (getUser(request).getId().equals(record.getUserId()) && "1".equals(getUser(request).getType()))
			return Result.error("您不能删除自己！");
		if (groupTeamService.findMembership(record)) {
			groupTeamService.deleteMemberShip(record);
			return Result.ok();
		} else 
			return Result.error("群中无此用户");
	}

	/**
	 * 更改群成员状态
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/modifyMembers", method = RequestMethod.POST)
	public Result modifyMembers(HttpServletRequest request, @RequestBody Members record) {
		if (ifGroupMaster(request, record.getGroupId()))
			return Result.error("您无权限做此操作！");
		if (groupTeamService.findMembership(record)) {
			groupTeamService.updateMemberShip(record);
			return Result.ok();
		} else
			return Result.error("群中无此用户");
	}

	/**
	 * 查找群成员
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/findMembers", method = RequestMethod.GET)
	public Result findMembers(HttpServletRequest request, Integer id) {
		List<Users> list = groupTeamService.selectUserByGroupId(id);
		GroupTeam gp=groupTeamService.selectByPrimaryKey(id);
		if (list != null && list.size() > 0)
			return Result.ok("", list).put("id", gp.getId()).put("groupId", gp.getGroupId());
		return Result.error("群成员列表为空").put("id", gp.getId()).put("groupId", gp.getGroupId());
	}

	private Users getUser(HttpServletRequest request) {
		if (request.getSession().getAttribute("user") != null)
			return (Users) request.getSession().getAttribute("user");
		return null;
	}

	private boolean ifGroupMaster(HttpServletRequest request, Integer groupId) {
		boolean bl = true;
		GroupTeam gt = new GroupTeam();
		gt.setId(groupId);
		gt.setMasterId(getUser(request).getId());
		List<GroupTeam> list = groupTeamService.selectListByGroup(gt);
		if (list != null && !list.isEmpty())
			bl = false;
		return bl;
	}

}
