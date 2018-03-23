package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.entity.SocketMsg;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/27 14:23
 * @Description:
 **/
public class ConcurrentWebSocket implements Closeable{

    private WebSocketSession session;

    private Map<String,Object> onlineRobots;

    public Map<String,Object> getOnlineRobots() {
        return onlineRobots;
    }

    public void setOnlineRobots(Map<String,Object> onlineRobots) {
        this.onlineRobots = onlineRobots;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public ConcurrentWebSocket(WebSocketSession session) {
        this.session = session;
        onlineRobots = new HashMap<>();
    }

    public synchronized void send(WebSocketMessage<?> webSocketMessage) {

        try {
            session.sendMessage(webSocketMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendBinary(SocketMsg socketMsg) {
        try {
            session.sendMessage(new BinaryMessage(Base64.getDecoder().decode(socketMsg.getBinary()), socketMsg.getStatus()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        this.session.close();
    }

    public synchronized void sendPong(PongMessage pongMessage) {
        try {
            session.sendMessage(pongMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
