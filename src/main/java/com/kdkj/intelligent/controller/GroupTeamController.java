package com.kdkj.intelligent.controller;

import java.util.List;

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

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/group")
public class GroupTeamController {
	@Autowired
	private GroupTeamService groupTeamService;
	@Autowired
	private UsersService usersService;

	@RequestMapping(value = "/selectListByGroup", method = RequestMethod.POST)
	public Result selectListByGroup(HttpServletRequest request,@RequestBody GroupTeam record) {
		List<GroupTeam> list = groupTeamService.selectListByGroup(record);
		return Result.ok("查询成功", list);
	}

	@RequestMapping(value = "/selectById", method = RequestMethod.GET)
	public Result selectById(HttpServletRequest request, int id) {
		// 群号需要解决
		GroupTeam groupTeam = groupTeamService.selectByPrimaryKey(id);
		return Result.ok("查询成功", groupTeam);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Result update(HttpServletRequest request,@RequestBody GroupTeam record) {
		// 需要管理员权限
		groupTeamService.updateByPrimaryKey(record);
		return Result.ok("修改成功");
	}

	@RequestMapping(value = "/updateGroupName", method = RequestMethod.POST)
	public Result updateGroupName(HttpServletRequest request,@RequestBody GroupTeam record) {
		// 需要代理商本人权限
		GroupTeam group = new GroupTeam();
		group.setId(record.getId());
		group.setGroupName(record.getGroupName());
		groupTeamService.updateByPrimaryKey(group);
		return Result.ok("修改成功");
	}

	@RequestMapping(value = "/addGroup", method = RequestMethod.POST)
	public Result addGroup(HttpServletRequest request,@RequestBody GroupTeam record) {
		// 需要代理商权限   群号必填，且不能重复
		if(record.getMasterId()==null || record.getGroupName()==null) {
			return Result.error("请检查参数是否填写完整");
		}
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
	public Result addMembers(HttpServletRequest request,Integer id,String userIds) {
		if(groupTeamService.selectByPrimaryKey(id)==null) {
			return Result.error("该群不存在，请检查后重新提交！");
		}
		String[] ids=userIds.split(",");
		for(String userId:ids) {
			if(usersService.selectByPrimaryKey(Integer.valueOf(userId))==null)
				return Result.error("用户id错误，请检查后再试");
		}
		Integer masterId=((Users)request.getAttribute("user")).getId();
		int i=groupTeamService.addMember(masterId,id,userIds);
		if(i>0) {
			return Result.ok("有"+i+"个用户是其他代理商的用户，不能添加");
		}
		return Result.ok("添加用户成功！");
	}

	/**
	 * 删除群成员
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delMembers", method = RequestMethod.POST)
	public Result delMembers(HttpServletRequest request,@RequestBody Members record) {
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
	public Result modifyMembers(HttpServletRequest request,@RequestBody Members record) {
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
	public Result findMembers(HttpServletRequest request, Integer groupId) {
		List<Users> list=groupTeamService.selectUserByGroupId(groupId);
		if(list!=null && list.size()>0)
		return Result.ok("",list);
		return Result.error("群成员列表为空");
	}
	
}
