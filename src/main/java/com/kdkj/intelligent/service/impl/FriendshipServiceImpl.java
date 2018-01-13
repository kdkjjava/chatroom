package com.kdkj.intelligent.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdkj.intelligent.dao.FriendshipMapper;
import com.kdkj.intelligent.entity.Friendship;
import com.kdkj.intelligent.service.FriendshipService;
@Service
public class FriendshipServiceImpl implements FriendshipService {
	@Autowired
	private FriendshipMapper friendshipMapper;
	@Override
	public int deleteByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return friendshipMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(Friendship record) {
		// TODO Auto-generated method stub
		record.setBuildTime(null);
		List<Friendship> selectByAttribute = friendshipMapper.selectByAttribute(record);
		if(selectByAttribute.size()==0) {
			return friendshipMapper.insert(record);
		}
		return -1;
	}

	@Override
	public Friendship selectByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return friendshipMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<Friendship> selectAll() {
		// TODO Auto-generated method stub
		return friendshipMapper.selectAll();
	}

	@Override
	public int updateByPrimaryKey(Friendship record) {
		// TODO Auto-generated method stub
		return friendshipMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<Friendship> selectByAttribute(Friendship record) {
		// TODO Auto-generated method stub
		return friendshipMapper.selectByAttribute(record);
	}

}
