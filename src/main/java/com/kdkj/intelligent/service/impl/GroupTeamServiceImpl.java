package com.kdkj.intelligent.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdkj.intelligent.dao.GroupTeamMapper;
import com.kdkj.intelligent.dao.KeyWordMapper;
import com.kdkj.intelligent.dao.MembersMapper;
import com.kdkj.intelligent.dao.UsersMapper;
import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Members;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.GroupTeamService;

@Service
public class GroupTeamServiceImpl implements GroupTeamService {

	@Autowired
	GroupTeamMapper groupTeamMapper;

	@Autowired
	MembersMapper membersMapper;

	@Autowired
	UsersMapper usersMapper;

	@Autowired
	KeyWordMapper keyWordMapper;

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return groupTeamMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(GroupTeam record) {
		GroupTeam groupTeam = new GroupTeam();
		groupTeam.setGroupId(getGroupId());
		List<GroupTeam> list = groupTeamMapper.selectListByGroup(groupTeam);
		while (!list.isEmpty()) {
			groupTeam.setGroupId(getGroupId());
			list = groupTeamMapper.selectListByGroup(groupTeam);
		}
		record.setGroupId(groupTeam.getGroupId());
		record.setBuildTime(new Date());
		int i = groupTeamMapper.insert(record);
		Members member = new Members();
		member.setBuildTime(new Date());
		member.setGroupId(record.getId());
		member.setUserId(record.getMasterId());
		member.setStatus("0");
		membersMapper.insert(member);
		return i;
	}

	@Override
	public GroupTeam selectByPrimaryKey(Integer id) {
		return groupTeamMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<GroupTeam> selectListByGroup(GroupTeam record) {
		return groupTeamMapper.selectListByGroup(record);
	}

	@Override
	public List<GroupTeam> selectAll() {
		return groupTeamMapper.selectAll();
	}

	@Override
	public int updateByPrimaryKey(GroupTeam record) {
		return groupTeamMapper.updateByPrimaryKey(record);
	}

	@Override
	public Boolean findMembership(Members record) {
		List<Members> list = membersMapper.findMemberShip(record);
		if (list != null && list.size() > 0)
			return true;
		return false;
	}

	@Override
	public Boolean findMembership(String username, String groupId) {
		return groupTeamMapper.isPlayer(username).contains(groupId);
	}

	@Override
	public int addMember(Integer masterId,Integer id, String userIds) {
		int i=0;
		// 需要检查用户是否为其他代理商的用户 是否已经加群
		String[] ids = userIds.split(",");
		for (String userId : ids) {
			List<GroupTeam> grouplist=groupTeamMapper.selectGroupByUserId(Integer.valueOf(userId));
			if(grouplist!=null && !grouplist.isEmpty()) {
				GroupTeam groupTeam=grouplist.get(0);
				if(masterId.equals(groupTeam.getMasterId())) {
					i++;
					continue;
				}
			}
			Members member = new Members();
			member.setGroupId(id);
			member.setUserId(Integer.valueOf(userId));
			List<Members> list = membersMapper.findMemberShip(member);
			if (list == null || list.isEmpty()) {
				member.setBuildTime(new Date());
				member.setStatus("0");
				membersMapper.insert(member);
			}
		}
		return i;
	}

	@Override
	public int deleteMemberShip(Members record) {
		return membersMapper.deleteMemberShip(record);
	}

	@Override
	public int updateMemberShip(Members record) {
		return membersMapper.update(record);
	}

	@Override
	public List<Users> selectUserByGroupId(Integer groupId) {
		return usersMapper.selectUserByGroupId(groupId);
	}

	public String getGroupId() {
		int i = (int) (Math.random() * 10000000);
		return String.valueOf(i);
	}

	@Override
	public int selectMasterIdByGroupId(Integer groupId) {
		return groupTeamMapper.selectMasterIdByGroupId(groupId);
	}

	@Override
	public Integer selectMasterIdByUsername(String username) {
		return groupTeamMapper.selectMasterIdByUsername(username);
	}

}