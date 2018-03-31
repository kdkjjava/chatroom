package com.kdkj.intelligent.service;

import com.kdkj.intelligent.entity.Members;

import java.util.List;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/2/7 16:46
 * @Description:
 **/
public interface MembersService {
    List<String> selectUsernameInGroup(String groupId);

    List<String> selectGroupIdByUsername(String username);

    Integer deleteMemberShip(String groupId, String msgFrom);

    Boolean updateSpeakStatus(String msgFrom, String groupId,Integer value);

    Members selectBlockStatus(String groupId, String msgFrom);

    Members selectBlockStatus(Members members);
}
