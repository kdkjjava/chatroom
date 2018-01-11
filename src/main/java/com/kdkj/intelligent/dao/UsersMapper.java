package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.Users;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UsersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Users record);

    Users selectByPrimaryKey(Integer id);
    
    List<Users> selectListByUser(Users record);
    
    List<Users> selectUserByGroupId(Integer groupId);
    
    List<Users> selectAll();

    int updateByPrimaryKey(Users record);
}