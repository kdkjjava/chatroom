package com.kdkj.intelligent.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kdkj.intelligent.entity.Users;
@Repository
public interface UsersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Users record);

    Users selectByPrimaryKey(Integer id);
    
    List<Users> selectListByUser(Users record);
    
    List<Users> selectUserByGroupId(Integer groupId);
    
    List<Users> selectAll();

    int updateByPrimaryKey(Users record);
    
    List<Users> findMyFriends(Long id);
}