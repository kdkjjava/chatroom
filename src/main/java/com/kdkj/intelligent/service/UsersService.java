package com.kdkj.intelligent.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.kdkj.intelligent.entity.Users;

public interface UsersService {
	int deleteByPrimaryKey(Integer id);

    int insert(Users record);

    Users selectByPrimaryKey(Integer id);
    
    PageInfo<Users> selectListByUser(Users record);

    List<Users> selectAll();

    int updateByPrimaryKey(Users record);
}
