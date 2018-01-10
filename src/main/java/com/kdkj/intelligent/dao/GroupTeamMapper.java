package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.GroupTeam;
import java.util.List;

public interface GroupTeamMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupTeam record);

    GroupTeam selectByPrimaryKey(Integer id);

    List<GroupTeam> selectListByGroup(GroupTeam record);
    
    List<GroupTeam> selectAll();

    int updateByPrimaryKey(GroupTeam record);
    
    List<GroupTeam> selectGroupByUserId(Integer id);
}