package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.SocketMsg;
import com.kdkj.intelligent.entity.TipsMsg;
import com.kdkj.intelligent.service.MembersService;
import com.kdkj.intelligent.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/15 17:12
 * @Description: 用于proxy的webSocket
 **/
@Component
public class ProxyHandler implements WebSocketHandler {

    @Autowired
    private UsersService usersService;

    @Autowired
    private MembersService membersService;

    //该变量用于保存master的session
    protected static Map<String, ConcurrentWebSocket> masterSessionPools;

    static {
        masterSessionPools = new ConcurrentHashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        webSocketSession.setBinaryMessageSizeLimit(524288);
        webSocketSession.setTextMessageSizeLimit(524288);
        if (masterSessionPools.containsKey(msgFrom) && masterSessionPools.get(msgFrom).getSession().isOpen())
            masterSessionPools.get(msgFrom).getSession().close();
        masterSessionPools.put(msgFrom, new ConcurrentWebSocket(webSocketSession));
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        if (webSocketMessage instanceof PingMessage){
            masterSessionPools.get(msgFrom).sendPong(new PongMessage(((PingMessage) webSocketMessage).getPayload()));
            return;
        }
        SocketMsg socketMsg = JSON.parseObject(webSocketMessage.getPayload().toString(), SocketMsg.class);
        if (socketMsg.getMsg() != null) {
            //将用户发送的json消息解析为java对象
            new Thread(() -> sendUsualMsg(masterSessionPools.get(msgFrom), socketMsg,webSocketMessage)).start();
            return;
        }
        if (socketMsg.getBinary() != null) {
            new Thread(() -> pushBinaryMsg(masterSessionPools.get(msgFrom), socketMsg)).start();
            return;
        }
        masterSessionPools.get(msgFrom).send(new TextMessage("{\"errorCode\":\"非法的消息类型\"}"));

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        System.out.println("代理用户："+ webSocketSession.getAttributes().get("msgFrom")+"\n关闭码:"+closeStatus.getCode()+"\n关闭原因:"+closeStatus.getReason()+"\n");
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
     *  @param webSocketSession 当前session对象
     * @param socketMsg        待发送的消息对象
     * @param webSocketMessage
     */
    private void sendUsualMsg(ConcurrentWebSocket webSocketSession, SocketMsg socketMsg, WebSocketMessage<?> webSocketMessage) {
        if ("3".equals(usersService.selectTypeByUserName(socketMsg.getMsgFrom()))){
            webSocketSession.send(webSocketMessage);
        }
        if (GroupHandler.sessionPools.containsKey(socketMsg.getGroupId())) {
            //将客户端的信息发送至指定的群聊天中
            GroupHandler.sessionPools.get(socketMsg.getGroupId()).forEach((key, item) -> {
                //将本条消息通过WebSocketSession发送至客户端
                item.send(webSocketMessage);
            });
            sendOffMsg(socketMsg);
            if (GroupHandler.sessionPools.get(socketMsg.getGroupId()).isEmpty()) {
                webSocketSession.send(new TextMessage("{\"errorCode\":\"NO_ONLINE_USERS\"}"));
            }
        } else {
            webSocketSession.send(new TextMessage("{\"errorCode\":\"群号不正确或者无在线用户\"}"));
        }
    }

    /**
     * 该方法用于发送二进制文件
     *  @param webSocketSession 当前session对象
     * @param socketMsg
     */
    private void pushBinaryMsg(ConcurrentWebSocket webSocketSession, SocketMsg socketMsg) {
        if (GroupHandler.sessionPools.containsKey(socketMsg.getGroupId())) {
            //将客户端的信息发送至指定的群聊天中
            GroupHandler.sessionPools.get(socketMsg.getGroupId()).forEach((key, item) -> {
                //将本条消息通过WebSocketSession发送至客户端
                item.sendBinary(socketMsg);
            });
            sendOffMsg(socketMsg);
            if (GroupHandler.sessionPools.get(socketMsg.getGroupId()).isEmpty()) {
                webSocketSession.send(new TextMessage("{\"errorCode\":\"NO_ONLINE_USERS\"}"));
            }
        }
    }

    private void sendOffMsg(SocketMsg socketMsg){
        List<String> groupMembers = membersService.selectUsernameInGroup(socketMsg.getGroupId());
        groupMembers.forEach(item ->{
            if (TotalHandler.totalSessions.containsKey(item) && !GroupHandler.sessionPools.get(socketMsg.getGroupId()).containsKey(item)){
                /*TotalHandler.totalSessions.get(item).send(new TextMessage(JSON.toJSONString(new TipsMsg().setGroupId(socketMsg.getMsgFrom())
                        .setMsgType("group").setCount(1))));*/
                if (!GroupHandler.leaveMsg.get(socketMsg.getGroupId()).containsKey(item))
                    GroupHandler.leaveMsg.get(socketMsg.getGroupId()).put(item,new CopyOnWriteArrayList<>());
                GroupHandler.leaveMsg.get(socketMsg.getGroupId()).get(item).add(socketMsg);
            }
        });
    }

}
