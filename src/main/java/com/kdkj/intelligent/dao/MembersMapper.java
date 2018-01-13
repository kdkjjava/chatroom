package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.Members;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MembersMapper {
    int deleteMemberShip(Members record);

    int insert(Members record);
    
    List<Members> findMemberShip(Members record);

    int update(Members record);

}