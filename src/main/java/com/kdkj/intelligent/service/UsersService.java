package com.kdkj.intelligent.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import com.kdkj.intelligent.entity.Friendship;
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
 
    List<Users> selectMemberIds();
    
    List<Users> findMyFriends(Long id);

    List<Users> selectByPaging(Users user);

    Boolean hasRecords(Users user);

    Users selectProxyNameAndTel(Integer id);

    Boolean hasExpired(String username);

	void changetoLs(String username);

    Integer proxyToUser(Users user);

	List<Users> findNewMembers(Long id, Long groupId);
}
