package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.entity.AdminMsg;
import com.kdkj.intelligent.entity.SocketMsg;
import com.kdkj.intelligent.service.GroupTeamService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;
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
    protected static  Map<String,WebSocketSession> totalSessions;

    //该变量储存用户已读的消息
    //protected static Map<String,List<AdminMsg>> readMsg;

    static {
        totalSessions=new ConcurrentHashMap();
       //readMsg=new ConcurrentHashMap();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        if (!totalSessions.containsKey(msgFrom)){
            totalSessions.put(msgFrom,webSocketSession);
        }

        if (FriendHandler.unsentMessages.containsKey(msgFrom)){
            pushMsg(webSocketSession,msgFrom);
        }

        //向用户提示有未读的系统消息
        //pushSysMsg(webSocketSession,msgFrom);

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
        if (totalSessions.containsKey(msgFrom)){
            totalSessions.remove(msgFrom);
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
     */
    private void pushMsg(WebSocketSession webSocketSession, String currentUsername){
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
