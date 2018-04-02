package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.TipsMsg;
import com.kdkj.intelligent.service.MembersService;
import com.kdkj.intelligent.util.Variables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import java.io.*;
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

    private static final Logger logger = LogManager.getLogger(TotalHandler.class);

    @Autowired
    private MembersService membersService;

    //该变量用于存储用户的总的session
    protected static Map<String, ConcurrentWebSocket> totalSessions;

    //该变量用于存放用户所有websocket的引用
    protected static Map<String,List<ConcurrentWebSocket>> sessionContainer;

    static {
        totalSessions = new ConcurrentHashMap<>();
        sessionContainer = new ConcurrentHashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        ConcurrentWebSocket concurrentWebSocket = new ConcurrentWebSocket(webSocketSession);
        if (msgFrom != null) {
            sessionContainer.put(msgFrom,new ArrayList<>());
            if (!totalSessions.containsKey(msgFrom)) {
                totalSessions.put(msgFrom, concurrentWebSocket);
            }
            if (FriendHandler.unsentMessages.containsKey(msgFrom)) {
                pushMsg(concurrentWebSocket, msgFrom);
            }
            return;
        }
        if (webSocketSession.isOpen()) {
            try {
                webSocketSession.close(new CloseStatus(1007));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        if (webSocketMessage.getPayload().equals("ping")) {
            try {
                webSocketSession.sendMessage(new TextMessage("pong"));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        logger.error(throwable.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        membersService.selectGroupIdByUsername(msgFrom).forEach(item -> GroupHandler.leaveMsg.get(item).remove(msgFrom));//删除群缓存里的个人消息
        if (msgFrom != null && totalSessions.containsKey(msgFrom)) {
            totalSessions.remove(msgFrom);
        }
        try {
            webSocketSession.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        if (closeStatus.getCode()==4888){
            for (ConcurrentWebSocket concurrentWebSocket:sessionContainer.get(msgFrom)){
                    concurrentWebSocket.close(4888, Variables.OFFLINEREASON);
            }
        }

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 推送消息提醒，用户上线后对其总的websocket发送消息提醒，发送消息内容为消息类型，和消息来源
     *
     * @param webSocketSession session对象
     * @param currentUsername  当前用户名
     */
    private void pushMsg(ConcurrentWebSocket webSocketSession, String currentUsername) {
        FriendHandler.unsentMessages.get(currentUsername).keySet().forEach(str ->
            webSocketSession.send(new TextMessage(JSON.toJSONString(new TipsMsg().setMsgFrom(str)
                    .setMsgType("friend").setCount(FriendHandler.unsentMessages.get(currentUsername).get(str).size())))));
    }
}
