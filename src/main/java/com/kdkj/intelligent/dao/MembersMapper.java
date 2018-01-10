package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.Members;
import java.util.List;

public interface MembersMapper {
    int deleteMemberShip(Members record);

    int insert(Members record);
    
    List<Members> findMemberShip(Members record);

    int update(Members record);
}