package com.kdkj.intelligent.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kdkj.intelligent.dao.GroupTeamMapper;
import com.kdkj.intelligent.dao.KeyWordMapper;
import com.kdkj.intelligent.dao.UsersMapper;
import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.KeyWord;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.MD5Encryption;

@Service
public class UsersServiceImpl implements UsersService{

	@Autowired
	UsersMapper usersMapper;
	@Autowired
	GroupTeamMapper groupTeamMapper;
	@Autowired
	KeyWordMapper keyWordMapper;
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		return usersMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(Users record) throws UnsupportedEncodingException {
		if("1".equals(record.getType())) {
			KeyWord keyword=new KeyWord();
			keyword.setMasterId(String.valueOf(record.getId()));
			keyWordMapper.insert(keyword);
		}
		record.setRegistTime(new Date());
		record.setPassword(MD5Encryption.getEncryption(record.getPassword()));
		record.setType(record.getType()!=null?record.getType():"0");
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
	public List<Users> selectListByUser(Users record) {
		List<Users> list=usersMapper.selectListByUser(record);
		return list;
	}

	@Override
	public List<GroupTeam> selectGroupByUserId(Integer userId) {
		return groupTeamMapper.selectGroupByUserId(userId);
	}


}