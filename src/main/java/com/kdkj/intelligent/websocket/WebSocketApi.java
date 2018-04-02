package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.GroupTeamService;
import com.kdkj.intelligent.util.Variables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
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

    private static final Logger logger = LogManager.getLogger(WebSocketApi.class);
    @Autowired
    private GroupTeamService groupTeamService;

    /**
     * 传入群号关闭该群所有webSocket链接
     *
     * @param groupId
     */
    public void dissolve(String groupId) {
        GroupHandler.sessionPools.get(groupId).forEach((key, value) -> value.close());
    }

    /**
     * 添加群时添加至缓存
     *
     * @param groupTeam
     */
    public void insertGroup(GroupTeam groupTeam) {
        GroupHandler.leaveMsg.put(groupTeam.getGroupId(), new ConcurrentHashMap<>());
    }

    /**
     * 移除缓存里的群
     *
     * @param id
     */
    public void deleteGroup(Integer id) {
        GroupTeam groupTeam = groupTeamService.selectByPrimaryKey(id);
        String groupId;
        if (groupTeam != null)
            groupId = groupTeam.getGroupId();
        else
            return;
        removeParam(GroupHandler.leaveMsg, groupId);
        removeParam(GroupHandler.getDefenseSetting(), groupId);
        removeParam(GroupHandler.sessionPools, groupId);
    }

    private void removeParam(Map map, Object obj) {
        if (map.containsKey(obj))
            map.remove(obj);
    }

    /**
     * 断开被挤用户的websocket链接
     *
     * @param user
     */
    public void initSocketConnection(Users user, String header) {
        String username = user.getUsername();
        if (header == null) {
            if (TotalHandler.totalSessions.containsKey(user.getUsername())) {
                TotalHandler.totalSessions.get(username).close(4888, Variables.OFFLINEREASON);
                TotalHandler.totalSessions.remove(username);
            }
        } else {
            if (ProxyHandler.masterSessionPools.containsKey(username)) {
                ProxyHandler.masterSessionPools.get(username).close(4888, Variables.OFFLINEREASON);
                ProxyHandler.masterSessionPools.remove(username);
            }
        }


    }
}
