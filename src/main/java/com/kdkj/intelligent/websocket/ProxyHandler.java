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
 * @Description: 用于proxy的webSocket
 **/
@Component
public class ProxyHandler implements WebSocketHandler {

    //该变量用于保存master的session
    protected static Map<String, WebSocketSession> masterSessionPools;

    static {
        masterSessionPools = new ConcurrentHashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        if (masterSessionPools.containsKey(msgFrom) && masterSessionPools.get(msgFrom).isOpen())
            masterSessionPools.get(msgFrom).close();
        masterSessionPools.put(msgFrom, webSocketSession);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

        if (webSocketMessage instanceof TextMessage) {
            //将用户发送的json消息解析为java对象
            SocketMsg socketMsg = JSON.parseObject(webSocketMessage.getPayload().toString(), SocketMsg.class);
            new Thread(() -> sendUsualMsg(webSocketSession, socketMsg)).start();
        } else if (webSocketMessage instanceof BinaryMessage) {
            new Thread(() -> pushBinaryMsg(webSocketSession, new BinaryMessage((byte[]) webSocketMessage.getPayload()))).start();
        } else {
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
     *
     * @param webSocketSession 当前session对象
     * @param socketMsg        待发送的消息对象
     */
    private void sendUsualMsg(WebSocketSession webSocketSession, SocketMsg socketMsg) {
        String groupId = (String) webSocketSession.getAttributes().get("groupId");
        if (!groupId.equals(socketMsg.getGroupId())) {
            try {
                webSocketSession.sendMessage(new TextMessage("{\"errorCode\":\"请求参数错误！\"}"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (GroupHandler.sessionPools.containsKey(socketMsg.getGroupId())) {
            //将客户端的信息发送至指定的群聊天中
            GroupHandler.sessionPools.get(socketMsg.getGroupId()).forEach(item -> {
                try {
                    //将本条消息通过WebSocketSession发送至客户端
                    item.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            try {
                webSocketSession.sendMessage(new TextMessage("{\"errorCode\":\"NO_ONLINE_USERS\"}"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 该方法用于发送二进制文件
     *
     * @param webSocketSession 当前session对象
     */
    private void pushBinaryMsg(WebSocketSession webSocketSession, BinaryMessage binaryMessage) {
        try {
            webSocketSession.sendMessage(binaryMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
