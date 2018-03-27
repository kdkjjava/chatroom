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
		return friendshipMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(Friendship record) {
			return friendshipMapper.insert(record);
	}

	@Override
	public Friendship selectByPrimaryKey(Long id) {
		return friendshipMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<Friendship> selectAll() {
		return friendshipMapper.selectAll();
	}

	@Override
	public int updateByPrimaryKey(Friendship record) {
		return friendshipMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<Friendship> selectByAttribute(Friendship record) {
		return friendshipMapper.selectByAttribute(record);
	}

}
