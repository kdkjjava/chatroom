package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.entity.AdminMsg;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/16 18:29
 * @Description:
 **/
@Component
public class AdminHandler  implements WebSocketHandler {

    //该变量用于存储普通用户的建议
    public static List<AdminMsg> adviceMsg;
    //该变量用于存储管理员的广播消息
    public static List<AdminMsg> broadCastMsg;

    static {
        adviceMsg=new ArrayList();
        broadCastMsg=new ArrayList();

        AdminMsg adminMsg1 =new AdminMsg().setAdmin("admin").setMsg("消息111111").setDate(String.valueOf(System.currentTimeMillis())).setTitle("title1");
        AdminMsg adminMsg2 =new AdminMsg().setAdmin("admin").setMsg("消息222222").setDate(String.valueOf(System.currentTimeMillis())).setTitle("title2");
        AdminMsg adminMsg3 =new AdminMsg().setAdmin("admin").setMsg("消息333333").setDate(String.valueOf(System.currentTimeMillis())).setTitle("title3");
        AdminMsg adminMsg4 =new AdminMsg().setAdmin("admin").setMsg("消息444444").setDate(String.valueOf(System.currentTimeMillis())).setTitle("title4");

        broadCastMsg.add(adminMsg1);
        broadCastMsg.add(adminMsg2);
        broadCastMsg.add(adminMsg3);
        broadCastMsg.add(adminMsg4);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        //暂时不需要总的webSocket发送消息
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        //暂时不需要总的webSocket发送消息
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        //暂时不需要总的webSocket发送消息
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        //暂时不需要总的webSocket发送消息
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
