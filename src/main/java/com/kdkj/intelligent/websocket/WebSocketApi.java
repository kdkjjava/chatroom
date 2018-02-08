package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.service.GroupTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/2/6 16:35
 * @Description:
 **/
@Component
public class WebSocketApi {

    @Autowired
    private GroupHandler groupHandler;

    @Autowired
    private GroupTeamService groupTeamService;

    /**
     * 传入群号关闭该群所有webSocket链接
     * @param groupId
     */
    public void dissolve(String groupId){
        GroupHandler.sessionPools.get(groupId).forEach((key,value) ->{
            try {
                value.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 添加群时添加至缓存
     * @param groupTeam
     */
    public void insertGroup(GroupTeam groupTeam){
        GroupHandler.leaveMsg.put(groupTeam.getGroupId(),new ConcurrentHashMap<>());
    }

    /**
     * 移除缓存里的群
     * @param id
     */
    public void deleteGroup(Integer id){
        GroupHandler.leaveMsg.remove(groupTeamService.selectByPrimaryKey(id).getGroupId());
    }

}