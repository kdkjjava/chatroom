package com.kdkj.intelligent.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.kdkj.intelligent.dao.UsersMapper;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;

@Service
public class UsersServiceImpl implements UsersService{

	@Autowired
	UsersMapper usersMapper;
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		return usersMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(Users record) {
		return usersMapper.insert(record);
	}

	@Override
	public Users selectByPrimaryKey(Integer id) {
		return usersMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<Users> selectAll() {
		return usersMapper.selectAll();
	}

	@Override
	public int updateByPrimaryKey(Users record) {
		return usersMapper.updateByPrimaryKey(record);
	}

	@Override
	public PageInfo<Users> selectListByUser(Users record) {
		List<Users> list=usersMapper.selectListByUser(record);
		PageInfo<Users> page = new PageInfo<Users>(list);
		return page;
	}

}