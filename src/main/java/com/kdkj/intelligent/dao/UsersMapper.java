package com.kdkj.intelligent.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.kdkj.intelligent.entity.GroupTeam;
import org.springframework.stereotype.Repository;
import com.kdkj.intelligent.entity.Users;
@Repository
public interface UsersMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Users record);

    Users selectByPrimaryKey(Integer id);
    
    List<Users> selectListByUser(Users record);
    
    List<Users> selectUserByGroupId(Integer id);
    
    List<Users> selectAll();

    int updateByPrimaryKey(Users record);
    
    int updateNogroupMemberTime(Integer id);
    
    List<Users> selectMemberIds();
    
    List<Users> findMyFriends(Long id);

    List<Users> selectByPaging(Users user);

    Integer selectCount(Users user);

    Users selectProxyNameAndTel(Integer id);

    Date selectExpireDate(String username);

	void changetoLs(String master);

    String selectTypeByUserName(String userName);

    void updateMaster(Integer id);

	List<Users> findNewMembers(Map<String,Long> map);


    Users selectByUserName(String username);

    Users selectMasterByUsername(String username);
}