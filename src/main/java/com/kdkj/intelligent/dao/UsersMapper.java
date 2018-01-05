package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.Users;
import java.util.List;

public interface UsersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Users record);

    Users selectByPrimaryKey(Integer id);
    
    List<Users> selectListByUser(Users record);
    
    List<Users> selectAll();

    int updateByPrimaryKey(Users record);
}