package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.service.GroupTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/15 11:14
 * @Description: 本类用于处理用户总的webSocket
 **/
@Component
public class TotalHandler implements WebSocketHandler {

    @Autowired
    private GroupTeamService groupTeamService;

    //该变量用于存储用户的总的session
    protected static volatile Map<Integer,Map<String,WebSocketSession>> totalSessions;

    static {
        totalSessions=new ConcurrentHashMap();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        Integer masterId = groupTeamService.selectMasterIdByUsername(msgFrom);
        if (!totalSessions.get(masterId).containsKey(msgFrom)){
            totalSessions.get(masterId).put(msgFrom,webSocketSession);
        }
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        Integer masterId = groupTeamService.selectMasterIdByUsername(msgFrom);
        if (totalSessions.get(masterId).containsKey(msgFrom)){
            totalSessions.get(masterId).remove(msgFrom);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
