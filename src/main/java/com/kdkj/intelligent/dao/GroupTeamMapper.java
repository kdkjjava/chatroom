package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.GroupTeam;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GroupTeamMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupTeam record);

    GroupTeam selectByPrimaryKey(Integer id);

    List<GroupTeam> selectListByGroup(GroupTeam record);
    
    List<GroupTeam> selectAll();

    int updateByPrimaryKey(GroupTeam record);
    
    List<GroupTeam> selectGroupByUserId(Integer id);
    
    int selectMasterIdByGroupId(Integer groupId);

    List<String> isPlayer(String username);
    
    Integer selectMasterIdByUsername(String username);

    String selectMasterNameByGroupId(String groupId);

    Integer updateDefenseByMasterId(GroupTeam groupTeam);
}