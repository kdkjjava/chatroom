package com.kdkj.intelligent.service.impl;

import java.util.Date;
import java.util.List;

import com.kdkj.intelligent.websocket.WebSocketApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdkj.intelligent.dao.GroupTeamMapper;
import com.kdkj.intelligent.dao.MembersMapper;
import com.kdkj.intelligent.dao.UsersMapper;
import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Members;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.GroupTeamService;

@Service
public class GroupTeamServiceImpl implements GroupTeamService {

    @Autowired
    GroupTeamMapper groupTeamMapper;

    @Autowired
    MembersMapper membersMapper;

    @Autowired
    UsersMapper usersMapper;

    @Autowired
    private WebSocketApi webSocketApi;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        Members record = new Members();
        record.setGroupId(id);
        webSocketApi.deleteGroup(id);
        membersMapper.deleteMemberShip(record);
        return groupTeamMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Object[] insert(GroupTeam record) {
        GroupTeam groupTeam = new GroupTeam();
        groupTeam.setGroupId(getGroupId());
        List<GroupTeam> list = groupTeamMapper.selectListByGroup(groupTeam);
        //查询禁言设置
        GroupTeam groupSetting = groupTeamMapper.selectDefenseSetting(record.getMasterId());
        if (groupSetting != null){
            record.setBoomSwitch(groupSetting.getBoomSwitch());
            record.setFlushSwitch(groupSetting.getFlushSwitch());
        }else {
            record.setBoomSwitch("off");
            record.setFlushSwitch("off");
        }
        while (!list.isEmpty()) {
            groupTeam.setGroupId(getGroupId());
            list = groupTeamMapper.selectListByGroup(groupTeam);
        }
        record.setGroupId(groupTeam.getGroupId());
        record.setBuildTime(new Date());
        int i = groupTeamMapper.insert(record);
        webSocketApi.insertGroup(record);
        Members member = new Members();
        member.setBuildTime(new Date());
        member.setGroupId(record.getId());
        member.setUserId(record.getMasterId());
        member.setStatus("0");
        membersMapper.insert(member);
        Object[] j = new Object[2];
        j[0] = i;
        j[1] = record.getGroupId();
        return j;
    }

    @Override
    public GroupTeam selectByPrimaryKey(Integer id) {
        return groupTeamMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<GroupTeam> selectListByGroup(GroupTeam record) {
        return groupTeamMapper.selectListByGroup(record);
    }

    @Override
    public List<GroupTeam> selectAll() {
        return groupTeamMapper.selectAll();
    }

    @Override
    public int updateByPrimaryKey(GroupTeam record) {
        return groupTeamMapper.updateByPrimaryKey(record);
    }

    @Override
    public Boolean findMembership(Members record) {
        List<Members> list = membersMapper.findMemberShip(record);
        return list != null && !list.isEmpty();
    }

    @Override
    public Boolean findMembership(String username, String groupId) {
        return groupTeamMapper.isPlayer(username).contains(groupId);
    }

    @Override
    public int addMember(Integer masterId, Integer id, String userIds) {
        int i = 0;
        // 需要检查用户是否为其他代理商的用户 是否已经加群
        String[] ids = userIds.split(",");
        for (String userId : ids) {
            Users user = usersMapper.selectByPrimaryKey(Integer.valueOf(userId));
            if (user != null) {
                if (!masterId.toString().equals(user.getMaster()) && !"1".equals(user.getMaster()) && !"0".equals(user.getMaster())) {
                    i++;
                    continue;
                } else if ("1".equals(user.getMaster()) || "0".equals(user.getMaster())) {
                    user.setMaster(String.valueOf(masterId));
                    usersMapper.updateByPrimaryKey(user);
                }
            } else
                continue;
            Members member = new Members();
            member.setGroupId(id);
            member.setUserId(Integer.valueOf(userId));
            List<Members> list = membersMapper.findMemberShip(member);
            if (list == null || list.isEmpty()) {
                member.setBuildTime(new Date());
                member.setStatus("0");
                membersMapper.insert(member);
            }
        }
        return i;
    }

    @Override
    public int deleteMemberShip(Members record) {
        Users user = usersMapper.selectByPrimaryKey(record.getUserId());
        user.setNogroupTime(new Date());
        usersMapper.updateByPrimaryKey(user);
        return membersMapper.deleteMemberShip(record);
    }

    @Override
    public int updateMemberShip(Members record) {
        return membersMapper.update(record);
    }

    @Override
    public List<Users> selectUserByGroupId(Integer groupId) {
        return usersMapper.selectUserByGroupId(groupId);
    }

    public String getGroupId() {
        int i = (int) (Math.random() * 10000000);
        return String.valueOf(i);
    }

    @Override
    public int selectMasterIdByGroupId(Integer groupId) {
        return groupTeamMapper.selectMasterIdByGroupId(groupId);
    }

    @Override
    public Integer selectMasterIdByUsername(String username) {
        return groupTeamMapper.selectMasterIdByUsername(username);
    }

    @Override
    public Integer updateDefenseStrategy(GroupTeam groupTeam) {
        return groupTeamMapper.updateDefenseByMasterId(groupTeam);
    }

    @Override
    public List<String> selectGroupIdByMasterId(GroupTeam groupTeam) {
        return usersMapper.selectGroupIdByMasterId(groupTeam);
    }

    @Override
    public String selectMasterNameByGroupId(String groupId) {
        return groupTeamMapper.selectMasterNameByGroupId(groupId);
    }


}