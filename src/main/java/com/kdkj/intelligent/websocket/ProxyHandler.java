package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.SocketMsg;
import com.kdkj.intelligent.service.GroupTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/15 17:12
 * @Description:
 **/
@Component
public class ProxyHandler implements WebSocketHandler {

    @Autowired
    private GroupTeamService groupTeamService;
    //该变量用于保存master的session
    protected static Map<String, WebSocketSession> masterSessionPools;

    static {
        masterSessionPools = new ConcurrentHashMap();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");

        if (!masterSessionPools.containsKey(msgFrom))
            masterSessionPools.put(msgFrom,webSocketSession);

    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

        if (webSocketMessage instanceof TextMessage){
            //将用户发送的json消息解析为java对象
            SocketMsg socketMsg = JSON.parseObject(webSocketMessage.getPayload().toString(), SocketMsg.class);
            sendUsualMsg(webSocketSession,socketMsg);
        }else if (webSocketMessage instanceof BinaryMessage){
            pushBinaryMsg(webSocketSession,new BinaryMessage((byte[]) webSocketMessage.getPayload()));
        }else {
            throw new IllegalStateException("Unexpected webSocket message type!");
        }

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        if (masterSessionPools.containsKey(msgFrom))
            masterSessionPools.remove(msgFrom);
        webSocketSession.close();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 普通文本消息发送方法
     * @param webSocketSession
     * @param socketMsg
     */
    private void sendUsualMsg(WebSocketSession webSocketSession, SocketMsg socketMsg){
        String groupId= (String) webSocketSession.getAttributes().get("groupId");
        if (GroupHandler.sessionPools.containsKey(groupId)){
            Set<Map.Entry<String, List<WebSocketSession>>> entries = GroupHandler.sessionPools.entrySet();
            for (Map.Entry<String, List<WebSocketSession>> entry : entries){
                if (entry.getKey().equals(socketMsg.getGroupId())) {
                    //将客户端的信息发送至指定的群聊天中
                    for (WebSocketSession item : entry.getValue()) {
                        try {
                            //将本条消息通过WebSocketSession发送至客户端
                            item.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            }
        }else {
            try {
                webSocketSession.sendMessage(new TextMessage("{\"errorCode\":\"NO_ONLINE_USERS\"}"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 该方法用于发送二进制文件
     * @param webSocketSession
     */
    private void pushBinaryMsg(WebSocketSession webSocketSession,BinaryMessage binaryMessage){

        try {
            webSocketSession.sendMessage(binaryMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
