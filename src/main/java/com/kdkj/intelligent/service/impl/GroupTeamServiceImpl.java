package com.kdkj.intelligent.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdkj.intelligent.dao.GroupTeamMapper;
import com.kdkj.intelligent.dao.MembersMapper;
import com.kdkj.intelligent.dao.UsersMapper;
import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Members;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.GroupTeamService;

@Service
public class GroupTeamServiceImpl implements GroupTeamService{

	@Autowired
	GroupTeamMapper groupTeamMapper;
	
	@Autowired
	MembersMapper membersMapper;
	
	@Autowired
	UsersMapper usersMapper;
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		return groupTeamMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(GroupTeam record) {
		return groupTeamMapper.insert(record);
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
		List<Members> list=membersMapper.findMemberShip(record);
		if(list!=null && list.size()>0)
		return true;
		return false;
	}

	@Override
	public int addMember(Members record) {
		return membersMapper.insert(record);
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


	
}