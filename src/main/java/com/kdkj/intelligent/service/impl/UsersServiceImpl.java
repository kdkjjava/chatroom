package com.kdkj.intelligent.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdkj.intelligent.dao.GroupTeamMapper;
import com.kdkj.intelligent.dao.UsersMapper;
import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.MD5Encryption;

@Service
public class UsersServiceImpl implements UsersService{

	@Autowired
	UsersMapper usersMapper;
	@Autowired
	GroupTeamMapper groupTeamMapper;
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		return usersMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(Users record) throws UnsupportedEncodingException {
		/*if("1".equals(record.getType())) {
			KeyWord keyword=new KeyWord();
			keyword.setMasterId(String.valueOf(record.getId()));
			keyWordMapper.insert(keyword);
		}*/
		record.setUsername(record.getPhone());
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

	@Override
	public String selectTypeByUserName(String userName) {
		Users user=new Users();
		user.setUsername(userName);
		return usersMapper.selectListByUser(user).get(0).getType();
	}

	@Override
	public List<Users> findMyFriends(Long id) {
		// TODO Auto-generated method stub
		return usersMapper.findMyFriends(id);
	}

	@Override
	public List<Users> selectMemberIds() {
		return usersMapper.selectMemberIds();
	}

	@Override
	public List<Users> selectByPaging(Users user) {
		return usersMapper.selectByPaging(user);
	}

	@Override
	public Boolean hasRecords(Users user) {
		Integer amount = usersMapper.selectCount(user);
		if ((user.getCurrent()-1)*user.getPageSize()>=amount)
			return false;
		return true;
	}

	@Override
	public Users selectProxyNameAndTel(Integer id) {
		return usersMapper.selectProxyNameAndTel(id);
	}

	@Override
	public Boolean hasExpired(String username) {
		return usersMapper.selectExpireDate(username).before(new Date());
	}

	@Override
	public void changetoLs(String username) {
		usersMapper.changetoLs(username);
		
	}
}