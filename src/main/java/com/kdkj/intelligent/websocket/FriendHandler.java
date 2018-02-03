package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.SocketMsg;
import com.kdkj.intelligent.entity.TipsMsg;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;
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
    private static Map<String, Map<String, WebSocketSession>> friendSessionPools;

    //该变量存储好友不在线时发送的消息
    protected static Map<String, Map<String, List<SocketMsg>>> unsentMessages;

    static {
        friendSessionPools = new ConcurrentHashMap<>();
        unsentMessages = new ConcurrentHashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        String msgTo = (String) webSocketSession.getAttributes().get("msgTo");
        if (msgFrom != null && msgTo != null) {
            handleFriendSessions(msgFrom, msgTo, webSocketSession);
        } else {
            if (webSocketSession.isOpen()) {
                try {
                    webSocketSession.close(new CloseStatus(1007));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws IOException {

        //将用户发送的json消息解析为java对象
        SocketMsg socketMsg = JSON.parseObject(webSocketMessage.getPayload().toString(), SocketMsg.class);
        String key = getKey(socketMsg.getMsgFrom(), socketMsg.getMsgTo());
        sendUsualMsg(webSocketSession, socketMsg, key);
        webSocketSession.sendMessage(new TextMessage(JSON.toJSONString(new SocketMsg().setMsg("aaaaaaaaaaaaaaa\\nbbbbbbbbbb\\nccccccccc\\n").setMsgFrom(socketMsg.getMsgFrom()).setMsgTo(socketMsg.getMsgTo()))));
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        String msgTo = (String) webSocketSession.getAttributes().get("msgTo");
        String key = getKey(msgFrom, msgTo);
        if (key != null) {
            friendSessionPools.get(key).remove(msgFrom);
            if (friendSessionPools.get(key).isEmpty())
                friendSessionPools.remove(key);
        }
        try {
            webSocketSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 该方法用于返回好友聊天的key
     *
     * @param msgFrom 发起聊天者
     * @param msgTo   接受聊天者
     * @return 返回缓存存储的key
     */
    private String getKey(String msgFrom, String msgTo) {
        if (friendSessionPools.containsKey(msgFrom + "_" + msgTo))
            return msgFrom + "_" + msgTo;
        if (friendSessionPools.containsKey(msgTo + "_" + msgFrom))
            return msgTo + "_" + msgFrom;
        return null;
    }

    //若对方好友不在线则保存至缓存待对方好友上线后进行提醒,若在线则发送提醒
    private void sendToOffline(SocketMsg socketMsg) {

        if (TotalHandler.totalSessions.containsKey(socketMsg.getMsgTo())) {
            TotalHandler.totalSessions.get(socketMsg.getMsgTo()).send(new TextMessage(JSON.toJSONString(new TipsMsg().setMsgFrom(socketMsg.getMsgFrom())
                    .setMsgType("friend").setCount(1))));
        }
        if (unsentMessages.containsKey(socketMsg.getMsgTo())) {
            if (unsentMessages.get(socketMsg.getMsgTo()).containsKey(socketMsg.getMsgFrom())) {
                unsentMessages.get(socketMsg.getMsgTo()).get(socketMsg.getMsgFrom()).add(socketMsg);
            } else {
                unsentMessages.get(socketMsg.getMsgTo()).put(socketMsg.getMsgFrom(), new ArrayList<>());
                unsentMessages.get(socketMsg.getMsgTo()).get(socketMsg.getMsgFrom()).add(socketMsg);
            }
        } else {
            unsentMessages.put(socketMsg.getMsgTo(), new ConcurrentHashMap<>());
            unsentMessages.get(socketMsg.getMsgTo()).put(socketMsg.getMsgFrom(), new ArrayList<>());
            unsentMessages.get(socketMsg.getMsgTo()).get(socketMsg.getMsgFrom()).add(socketMsg);
        }
    }

    /**
     * 发送消息方法
     *
     * @param webSocketSession session对象
     * @param socketMsg        消息对象
     * @param key              好友聊天的key
     */
    private void sendUsualMsg(WebSocketSession webSocketSession, SocketMsg socketMsg, String key) {
        try {
            webSocketSession.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        friendSessionPools.get(key).size();
        if (friendSessionPools.get(key).size() > 1) {
            try {
                friendSessionPools.get(key).get(socketMsg.getMsgTo()).sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
//                TextMessage textMessage = new TextMessage(JSON.toJSONString(new SocketMsg().setMsg("aaaaaaaaaaaaaaa\\nbbbbbbbbbb\\nccccccccc\\n").setMsgFrom(socketMsg.getMsgFrom()).setMsgTo(socketMsg.getMsgTo())));
//                friendSessionPools.get(key).get(socketMsg.getMsgTo()).sendMessage(textMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else //如果对方不在线或者未打开聊天窗口，则保存到缓存中或进行消息提醒
            sendToOffline(socketMsg);
    }

    private void handleFriendSessions(String msgFrom, String msgTo, WebSocketSession webSocketSession) {
        String key = getKey(msgFrom, msgTo);
        if (key != null) {//如果有同个用户的session，则关闭先前的session
            if (friendSessionPools.get(key).containsKey(msgFrom)) {
                if (friendSessionPools.get(key).get(msgFrom) != webSocketSession && friendSessionPools.get(key).get(msgFrom).isOpen()) {
                    try {
                        friendSessionPools.get(key).get(msgFrom).close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    friendSessionPools.get(key).put(msgFrom, webSocketSession);
                }
            } else {
                friendSessionPools.get(key).put(msgFrom, webSocketSession);
            }
        } else {
            key = msgFrom + "_" + msgTo;
            friendSessionPools.put(key, new ConcurrentHashMap<>());
            friendSessionPools.get(key).put(msgFrom, webSocketSession);
        }
        //判断缓存内是否有自己未读的消息
        if (unsentMessages.containsKey(msgFrom) && unsentMessages.get(msgFrom).containsKey(msgTo)) {
            unsentMessages.get(msgFrom).get(msgTo).forEach(socketMsg -> {
                try {
                    webSocketSession.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            unsentMessages.get(msgFrom).remove(msgTo);
        }
    }

}
