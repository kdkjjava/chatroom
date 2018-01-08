package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyWebSocketHandler implements WebSocketHandler {

    //concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。其中key为房间号标识
    private volatile static Map<String, List<WebSocketSession>> sessionPools;
    static {
        sessionPools = new ConcurrentHashMap();
    }
    //握手实现连接后
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String roomCode= (String) webSocketSession.getAttributes().get("roomNum");
        //将连接地址的参数roomcode的值放入变量roomCode中
        if (sessionPools.containsKey(roomCode)) {
            sessionPools.get(roomCode).add(webSocketSession);
        } else {
            sessionPools.put(roomCode, new LinkedList<WebSocketSession>());
            sessionPools.get(roomCode).add(webSocketSession);
        }


    }

    //发送信息前的处理
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        // 把客户端的消息解析为JSON对象
        JSON json=JSON.parseObject(webSocketMessage.getPayload().toString());
        //调用普通信息的发送方法
        sendUsualMessage(webSocketSession,webSocketMessage,json);

    }




    public  void  handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    /**
     * 在此刷新页面就相当于断开WebSocket连接,原本在静态变量userSocketSessionMap中的
     * WebSocketSession会变成关闭状态(close)，但是刷新后的第二次连接服务器创建的
     * 新WebSocketSession(open状态)又不会加入到userSocketSessionMap中,所以这样就无法发送消息
     * 因此应当在关闭连接这个切面增加去除userSocketSessionMap中当前处于close状态的WebSocketSession，
     * 让新创建的WebSocketSession(open状态)可以加入到userSocketSessionMap中
     * @param webSocketSession
     * @param closeStatus
     * @throws Exception
     */
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        sessionPools.get(webSocketSession.getAttributes().get("roomNum")).remove(webSocketSession);
        webSocketSession.close();
    }

    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 本方法用于处理玩家发送的普通消息
     * @param webSocketSession
     * @param webSocketMessage
     */
    public void sendUsualMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage,JSON json){
//遍历map集合，将消息发送至同一个房间下的session
        Iterator<Map.Entry<String, List<WebSocketSession>>> iterator = sessionPools.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<WebSocketSession>> entry = iterator.next();
            if (entry.getKey().equals(webSocketSession.getAttributes().get("roomNum"))) {
                //判断若是为同一个房间，则遍历房间内的session，并发送消息
                for (WebSocketSession item : entry.getValue()) {
                    try {
                        //将本条消息通过WebSocketSession发送至客户端
                        item.sendMessage(new TextMessage(json.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }
    /**
     * 本方法用于处理玩家发送的指令消息
     * @param webSocketSession
     * @param webSocketMessage
     */
    public void sendCommandMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage){

    }

    /**
     * 本方法用于处理玩家发送给管理员的建议
     * @param webSocketSession
     * @param webSocketMessage
     */
    public void sendAdviceMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage){

    }

    /**
     * 本方法用于处理客户端发送的指令
     * @param webSocketSession
     * @param webSocketMessage
     */
    public void usualFromClient(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage){

    }

    /**
     * 本方法用于处理客户端发送的图片
     * @param webSocketSession
     * @param webSocketMessage
     */
    public void pictureFromClient(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage){

    }
}