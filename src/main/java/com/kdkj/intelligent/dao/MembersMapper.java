package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Members;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MembersMapper {
    int deleteMemberShip(Members record);

    int insert(Members record);
    
    List<Members> findMemberShip(Members record);

    int update(Members record);

    List<String> selectUsernameInGroup(String groupId);

    List<String> selectGroupIdByUsername(String username);

    Integer deleteMember(@Param("groupId") String groupId, @Param("msgFrom") String msgFrom);

    Boolean updateSpeakStatus(@Param("msgFrom")String msgFrom, @Param("groupId")String groupId,@Param("value")Integer value);

    Members selectBlockStatus(@Param("groupId") String groupId, @Param("msgFrom") String msgFrom);

    Members selectBlockByIds(Members members);
}