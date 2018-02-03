package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.SocketMsg;
import com.kdkj.intelligent.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.Map;
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

    @Autowired
    private UsersService usersService;

    //该变量用于保存master的session
    protected static Map<String, ConcurrentWebSocket> masterSessionPools;

    static {
        masterSessionPools = new ConcurrentHashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        webSocketSession.setBinaryMessageSizeLimit(5242880);
        webSocketSession.setTextMessageSizeLimit(5242880);
        if (masterSessionPools.containsKey(msgFrom) && masterSessionPools.get(msgFrom).getSession().isOpen())
            masterSessionPools.get(msgFrom).getSession().close();
        masterSessionPools.put(msgFrom, new ConcurrentWebSocket(webSocketSession));
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        SocketMsg socketMsg = JSON.parseObject(webSocketMessage.getPayload().toString(), SocketMsg.class);
        if (socketMsg.getMsg() != null) {
            //将用户发送的json消息解析为java对象
            new Thread(() -> sendUsualMsg(masterSessionPools.get(msgFrom), socketMsg)).start();

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
     *
     * @param webSocketSession 当前session对象
     * @param socketMsg        待发送的消息对象
     */
    private void sendUsualMsg(ConcurrentWebSocket webSocketSession, SocketMsg socketMsg ) {
        String s = usersService.selectTypeByUserName(socketMsg.getMsgFrom());
        if ("3".equals(usersService.selectTypeByUserName(socketMsg.getMsgFrom()))){
            webSocketSession.send(new TextMessage(JSON.toJSONString(socketMsg)));
        }
        if (GroupHandler.sessionPools.containsKey(socketMsg.getGroupId())) {
            //将客户端的信息发送至指定的群聊天中
            GroupHandler.sessionPools.get(socketMsg.getGroupId()).forEach((key, item) -> {
                //将本条消息通过WebSocketSession发送至客户端
                item.send(new TextMessage(JSON.toJSONString(socketMsg)));
            });
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
            if (GroupHandler.sessionPools.get(socketMsg.getGroupId()).isEmpty()) {
                webSocketSession.send(new TextMessage("{\"errorCode\":\"NO_ONLINE_USERS\"}"));
            }
        }
    }

}
