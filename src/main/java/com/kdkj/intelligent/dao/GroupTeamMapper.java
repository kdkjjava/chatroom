package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.GroupTeam;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GroupTeamMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupTeam record);

    GroupTeam selectByPrimaryKey(Integer id);

    List<GroupTeam> selectAll();

    int updateByPrimaryKey(GroupTeam record);
}