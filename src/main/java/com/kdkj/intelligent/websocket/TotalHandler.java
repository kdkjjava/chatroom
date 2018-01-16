package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.entity.SocketMsg;
import com.kdkj.intelligent.service.GroupTeamService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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

        if (FriendHandler.unsentMessages.get(masterId).containsKey(msgFrom)){
            pushMsg(webSocketSession,msgFrom,masterId);
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
        webSocketSession.close();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 推送消息提醒，用户上线后对其总的websocket发送消息提醒，发送消息内容为消息类型，和消息来源
     * @param webSocketSession
     * @param currentUsername
     * @param masterId
     */
    private void pushMsg(WebSocketSession webSocketSession, String currentUsername, Integer masterId){
        Set<String> msgFroms = FriendHandler.unsentMessages.get(currentUsername).keySet();
        Iterator<String> iterator = msgFroms.iterator();
        while (iterator.hasNext()){
            try {
                webSocketSession.sendMessage(new TextMessage("{\"msgType\":\"friend\",\"msgFrom\":\""+iterator.next()+"\"}"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
