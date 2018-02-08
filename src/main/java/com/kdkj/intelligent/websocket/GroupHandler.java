package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.SocketMsg;
import com.kdkj.intelligent.service.GroupTeamService;
import com.kdkj.intelligent.service.MembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GroupHandler implements WebSocketHandler {

    @Autowired
    private GroupTeamService groupTeamService;

    @Autowired
    private MembersService membersService;

    //concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。其中key为房间号标识
    protected static Map<String, Map<String, ConcurrentWebSocket>> sessionPools;
    //外层key为用户名，内层key为房间号
    protected static Map<String, Map<String, List<SocketMsg>>> leaveMsg;

    static {
        sessionPools = new ConcurrentHashMap<>();
        leaveMsg = new ConcurrentHashMap<>();
    }

    @PostConstruct
    private void leaveMsgInit() {
        groupTeamService.selectAll().forEach(item -> leaveMsg.put(item.getGroupId(), new ConcurrentHashMap<>()));
    }

    //握手实现连接后
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String groupId = getParam(webSocketSession, "groupId");
        String msgFrom = getParam(webSocketSession, "msgFrom");
        webSocketSession.setBinaryMessageSizeLimit(524288);
        webSocketSession.setTextMessageSizeLimit(524288);
        //将连接地址的参数groupId的值放入变量roomCode中
        if (sessionPools.containsKey(groupId)) {
            sessionPools.get(groupId).put(msgFrom, new ConcurrentWebSocket(webSocketSession));
        } else {
            sessionPools.put(groupId, new ConcurrentHashMap<>());
            sessionPools.get(groupId).put(msgFrom, new ConcurrentWebSocket(webSocketSession));
        }
        if (leaveMsg.get(groupId).containsKey(msgFrom))
            getUnReceivedMsg(groupId, msgFrom);
    }

    /**
     * 获取在线期间未接受的消息
     *
     * @param groupId
     * @param msgFrom
     */
    private void getUnReceivedMsg(String groupId, String msgFrom) {
        leaveMsg.get(groupId).get(msgFrom).forEach(item -> {
            if (item.getMsg() != null)
                sessionPools.get(groupId).get(msgFrom).send(new TextMessage(JSON.toJSONString(item)));
            if (item.getBinary()!=null)
                sessionPools.get(groupId).get(msgFrom).sendBinary(item);
        });
        leaveMsg.get(groupId).remove(msgFrom);
    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    /**
     * 在此刷新页面就相当于断开WebSocket连接,原本在静态变量userSocketSessionMap中的
     * WebSocketSession会变成关闭状态(close)，但是刷新后的第二次连接服务器创建的
     * 新WebSocketSession(open状态)又不会加入到userSocketSessionMap中,所以这样就无法发送消息
     * 因此应当在关闭连接这个切面增加去除userSocketSessionMap中当前处于close状态的WebSocketSession，
     * 让新创建的WebSocketSession(open状态)可以加入到userSocketSessionMap中
     *
     * @param webSocketSession 当前session
     * @param closeStatus      关闭状态码
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        //System.out.println("group群链接：" + getParam(webSocketSession, "msgFrom") + "\n关闭码:" + closeStatus.getCode() + "\n关闭原因:" + closeStatus.getReason());
        String groupId = getParam(webSocketSession, "groupId");
        String msgFrom = getParam(webSocketSession, "msgFrom");
        sessionPools.get(groupId).remove(msgFrom);
        webSocketSession.close();

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    //发送信息前的处理
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        if (webSocketMessage.getPayload().equals("ping")) {
            new Thread(() -> {
                if (sessionPools.get(getParam(webSocketSession, "groupId")).containsKey(getParam(webSocketSession, "msgFrom")))
                    sessionPools.get(getParam(webSocketSession, "groupId")).get(getParam(webSocketSession, "msgFrom")).send(new TextMessage("pong"));
            }
            ).start();
            return;
        }
        //将用户发送的json消息解析为java对象
        SocketMsg socketMsg = JSON.parseObject(webSocketMessage.getPayload().toString(), SocketMsg.class);
        String masterName = groupTeamService.selectMasterNameByGroupId(socketMsg.getGroupId());
        if (groupTeamService.findMembership(socketMsg.getMsgFrom(), socketMsg.getGroupId()) && ProxyHandler.masterSessionPools.containsKey(masterName)) {
            sendToClient(socketMsg, masterName);
        }
        //调用普通信息的发送方法
        new Thread(() -> sendUsualMessage(webSocketSession, socketMsg,webSocketMessage)).start();
    }

    /**
     * 本方法用于处理玩家发送的普通消息
     *
     * @param webSocketSession 当前session对象
     * @param webSocketMessage
     */
    private void sendUsualMessage(WebSocketSession webSocketSession, SocketMsg socketMsg, WebSocketMessage<?> webSocketMessage) {
        String groupId = (String) webSocketSession.getAttributes().get("groupId");
        if (!groupId.equals(socketMsg.getGroupId())) {
            try {
                webSocketSession.sendMessage(new TextMessage("{\"errorCode\":\"请求参数错误！\"}"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //遍历map集合，将消息发送至同一个房间下的session
        if (sessionPools.containsKey(socketMsg.getGroupId()))
            sessionPools.get(socketMsg.getGroupId()).forEach((key, item) -> item.send(webSocketMessage));
        List<String> groupMembers = membersService.selectUsernameInGroup(groupId);
        groupMembers.forEach(item -> {
            if (TotalHandler.totalSessions.containsKey(item) && !sessionPools.get(groupId).containsKey(item)) {
                /*TotalHandler.totalSessions.get(item).send(new TextMessage(JSON.toJSONString(new TipsMsg().setGroupId(socketMsg.getMsgFrom())
                        .setMsgType("group").setCount(1))));*/
                synchronized (this){
                    if (!leaveMsg.get(groupId).containsKey(item))
                        leaveMsg.get(groupId).put(item, new CopyOnWriteArrayList<>());
                }
                leaveMsg.get(groupId).get(item).add(socketMsg);
            }
        });
    }

    /**
     * 本方法用于发送信息至客户端
     *  @param socketMsg  消息对象
     * @param masterName 代理
     */
    private void sendToClient(SocketMsg socketMsg, String masterName) {
        ConcurrentWebSocket session = ProxyHandler.masterSessionPools.get(masterName);
        socketMsg.setMasterName(masterName);
        session.send(new TextMessage(JSON.toJSONString(socketMsg)));
    }

    /**
     * 通过webSocketSession得到作用域中的groupId
     *
     * @param webSocketSession 当前session对象
     * @return
     */
    private String getParam(WebSocketSession webSocketSession, String param) {
        return (String) webSocketSession.getAttributes().get(param);
    }


}