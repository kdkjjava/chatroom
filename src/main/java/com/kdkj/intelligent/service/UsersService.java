package com.kdkj.intelligent.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Users;

public interface UsersService {
	int deleteByPrimaryKey(Integer id);

    int insert(Users record) throws UnsupportedEncodingException;

    Users selectByPrimaryKey(Integer id);
    
    List<Users> selectListByUser(Users record);

    List<Users> selectAll();
    
    List<GroupTeam> selectGroupByUserId(Integer id);

    int updateByPrimaryKey(Users record);
    
    String selectTypeByUserName(String userName);
    
}
