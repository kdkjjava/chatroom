package com.kdkj.intelligent.service.impl;

import com.kdkj.intelligent.dao.MembersMapper;
import com.kdkj.intelligent.entity.Members;
import com.kdkj.intelligent.service.MembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/2/7 16:47
 * @Description:
 **/
@Service
public class MembersServiceImpl implements MembersService{

    @Autowired
    private MembersMapper membersMapper;
    @Override
    public List<String> selectUsernameInGroup(String groupId) {
        return membersMapper.selectUsernameInGroup(groupId);
    }

    @Override
    public List<String> selectGroupIdByUsername(String username) {
        return membersMapper.selectGroupIdByUsername(username);
    }

    @Override
    public Integer deleteMemberShip(String groupId, String msgFrom) {
        return membersMapper.deleteMember(groupId,msgFrom);
    }

    @Override
    public Boolean updateSpeakStatus(String msgFrom, String groupId,Integer value) {
        return membersMapper.updateSpeakStatus(msgFrom,groupId,value);
    }

    @Override
    public Members selectBlockStatus(String groupId, String msgFrom) {
        return membersMapper.selectBlockStatus(groupId,msgFrom);
    }

    @Override
    public Members selectBlockStatus(Members members) {
        return membersMapper.selectBlockByIds(members);
    }
}
