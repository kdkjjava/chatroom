package com.kdkj.intelligent.service;

import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Members;
import com.kdkj.intelligent.entity.Users;

import java.util.List;

public interface GroupTeamService {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupTeam record);

    GroupTeam selectByPrimaryKey(Integer id);
    
    List<GroupTeam> selectListByGroup(GroupTeam record);

    List<GroupTeam> selectAll();

    int updateByPrimaryKey(GroupTeam record);
    
    Boolean findMembership(Members record);

    Boolean findMembership(String username,String groupId);
    
    int addMember(Integer masterId,Integer groupId,String userIds);
    
    int deleteMemberShip(Members record);
    
    int updateMemberShip(Members record);
    /*
     * 根据群号查找群成员
     */
    List<Users> selectUserByGroupId(Integer groupId);
    
    int selectMasterIdByGroupId(Integer groupId);
    
    Integer selectMasterIdByUsername(String username);
}