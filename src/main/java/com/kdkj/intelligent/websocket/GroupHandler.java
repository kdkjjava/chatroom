package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.SocketMsg;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.GroupTeamService;
import com.kdkj.intelligent.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GroupHandler implements WebSocketHandler {

    @Autowired
    private UsersService usersService;

    @Autowired
    private GroupTeamService groupTeamService;

    //concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。其中key为房间号标识
    protected static volatile Map<Integer, Map<String, List<WebSocketSession>>> sessionPools;

    static {
        sessionPools = new ConcurrentHashMap();
    }

    @PostConstruct
    public void sessionPoolsInitial() {
        Users user = new Users();
        user.setType("1");
        List<Users> masters = usersService.selectListByUser(user);
        for (Users u : masters) {
            sessionPools.put(u.getId(), new ConcurrentHashMap());
        }

    }

    //握手实现连接后
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String groupId = (String) webSocketSession.getAttributes().get("groupId");
        Integer masterId = groupTeamService.selectMasterIdByGroupId(Integer.parseInt(groupId));
        //将连接地址的参数groupId的值放入变量roomCode中
        if (sessionPools.get(masterId).containsKey(groupId)) {
            sessionPools.get(masterId).get(groupId).add(webSocketSession);
        } else {
            sessionPools.get(masterId).put(groupId, new ArrayList());
            sessionPools.get(masterId).get(groupId).add(webSocketSession);
        }
    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {

        throw new UnsupportedOperationException("发生handleTransportError错误！");
    }

    /**
     * 在此刷新页面就相当于断开WebSocket连接,原本在静态变量userSocketSessionMap中的
     * WebSocketSession会变成关闭状态(close)，但是刷新后的第二次连接服务器创建的
     * 新WebSocketSession(open状态)又不会加入到userSocketSessionMap中,所以这样就无法发送消息
     * 因此应当在关闭连接这个切面增加去除userSocketSessionMap中当前处于close状态的WebSocketSession，
     * 让新创建的WebSocketSession(open状态)可以加入到userSocketSessionMap中
     *
     * @param webSocketSession
     * @param closeStatus
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        String groupId = (String) webSocketSession.getAttributes().get("groupId");
        Integer masterId = groupTeamService.selectMasterIdByGroupId(Integer.parseInt(groupId));
        sessionPools.get(masterId).get(groupId).remove(webSocketSession);
        webSocketSession.close();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    //发送信息前的处理
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

        System.out.println(webSocketMessage.getPayload().getClass());
        //将用户发送的json消息解析为java对象
        SocketMsg socketMsg = JSON.parseObject(webSocketMessage.getPayload().toString(), SocketMsg.class);

        Integer masterId = groupTeamService.selectMasterIdByGroupId(Integer.parseInt(socketMsg.getGroupId()));
        socketMsg.setStatus("success");
        if (groupTeamService.findMembership(socketMsg.getMsgFrom(), socketMsg.getMsgTo()) && ProxyHandler.masterSessionPools.containsKey(masterId)) {
                sendToClient(webSocketMessage, socketMsg, masterId);
        }
        //调用普通信息的发送方法
        sendUsualMessage(webSocketSession, socketMsg, masterId);
    }


    /**
     * 本方法用于处理玩家发送的普通消息
     *
     * @param webSocketSession
     */

    public void sendUsualMessage(WebSocketSession webSocketSession, SocketMsg socketMsg, Integer masterId) {
        //遍历map集合，将消息发送至同一个房间下的session
        Iterator<Map.Entry<String, List<WebSocketSession>>> iterator = sessionPools.get(masterId).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<WebSocketSession>> entry = iterator.next();
            if (entry.getKey().equals(webSocketSession.getAttributes().get("groupId"))) {
                //判断若是为同一个房间，则遍历房间内的session，并发送消息
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
    }


    /**
     * 本方法用于处理玩家发送给管理员的建议
     *
     * @param webSocketSession
     * @param webSocketMessage
     */
    public void sendAdviceMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
        //TODO
    }


    /**
     * 本方法用于处理客户端发送的图片
     *
     * @param webSocketSession
     * @param webSocketMessage
     */
    public void pictureFromClient(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
        //TODO
    }

    /**
     * 本方法用于发送信息至客户端
     *
     * @param webSocketMessage
     * @param socketMsg
     * @param masterId
     */
    public void sendToClient(WebSocketMessage<?> webSocketMessage, SocketMsg socketMsg, Integer masterId) {
        WebSocketSession session = ProxyHandler.masterSessionPools.get(masterId);
        socketMsg.setStatus("command");
        try {
            session.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}