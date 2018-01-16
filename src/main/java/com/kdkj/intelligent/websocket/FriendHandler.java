package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.SocketMsg;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/13 17:17
 * @Description: 用于好友聊天
 **/
@Component
public class FriendHandler implements WebSocketHandler {

    //创建一个保存好友聊天的WebSocketSession
    private static volatile Map<String, List<WebSocketSession>> friendSessionPools;

    //该变量存储好友不在线时发送的消息
    protected static volatile Map<String, Map<String, List<SocketMsg>>> unsentMessages;

    static {
        friendSessionPools = new ConcurrentHashMap();
        unsentMessages = new ConcurrentHashMap();
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        String msgTo = (String) webSocketSession.getAttributes().get("msgTo");
        String key = getKey(msgFrom, msgTo);
        if (key != null)
            friendSessionPools.get(key).add(webSocketSession);
        else {
            friendSessionPools.put(key, new ArrayList<>());
            friendSessionPools.get(key).add(webSocketSession);
        }
        //判断缓存内是否有自己未读的消息
        if (unsentMessages.containsKey(msgFrom) && unsentMessages.get(msgFrom).containsKey(msgTo)) {
            for (SocketMsg socketMsg : unsentMessages.get(msgFrom).get(msgTo))
                webSocketSession.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
            unsentMessages.get(msgFrom).remove(msgTo);
        }


    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
        //将用户发送的json消息解析为java对象
        SocketMsg socketMsg = JSON.parseObject(webSocketMessage.getPayload().toString(), SocketMsg.class);
        String key = getKey(socketMsg.getMsgFrom(), socketMsg.getMsgTo());
        //如果对方用户不在线，则保存到缓存中
        if (friendSessionPools.get(key).size() < 2) {
            sendToOffline(socketMsg);
        }

        for (WebSocketSession session : friendSessionPools.get(key)) {
            if (session == webSocketSession) {
                try {
                    session.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            try {
                session.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void sendErrorMsg(WebSocketSession webSocketSession, SocketMsg socketMsg) {
        socketMsg.setStatus("发送失败，发生未知错误");
        try {
            webSocketSession.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 该方法用于返回好友聊天的key
     *
     * @param
     * @return
     */
    private String getKey(String msgFrom, String msgTo) {
        if (friendSessionPools.containsKey(msgFrom + "_" + msgTo))
            return msgFrom + "_" + msgTo;
        if (friendSessionPools.containsKey(msgTo + "_" + msgFrom))
            return msgTo + "_" + msgFrom;
        return null;
    }

    //若对方好友不在线则保存至缓存待对方好友上线后进行提醒
    private void sendToOffline(SocketMsg socketMsg){
        if (unsentMessages.containsKey(socketMsg.getMsgTo())) {
            if (unsentMessages.get(socketMsg.getMsgTo()).containsKey(socketMsg.getMsgFrom())) {
                unsentMessages.get(socketMsg.getMsgTo()).get(socketMsg.getMsgFrom()).add(socketMsg);
            } else {
                unsentMessages.get(socketMsg.getMsgTo()).put(socketMsg.getMsgFrom(), new ArrayList<>());
                unsentMessages.get(socketMsg.getMsgTo()).get(socketMsg.getMsgFrom()).add(socketMsg);
            }
        } else {
            unsentMessages.put(socketMsg.getMsgTo(), new ConcurrentHashMap());
            unsentMessages.get(socketMsg.getMsgTo()).put(socketMsg.getMsgFrom(), new ArrayList<>());
            unsentMessages.get(socketMsg.getMsgTo()).get(socketMsg.getMsgFrom()).add(socketMsg);
        }
    }


}
