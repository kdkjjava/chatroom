package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.entity.SocketMsg;
import org.springframework.web.socket.*;

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
public class ConcurrentWebSocket implements Closeable {

    private WebSocketSession session;

    private Map<String, Object> onlineRobots;

    private int hz;

    private long lastTalking;

    private int talkingStatus;

    public int getTalkingStatus() {
        return talkingStatus;
    }

    public Map<String, Object> getOnlineRobots() {
        return onlineRobots;
    }

    public void setOnlineRobots(Map<String, Object> onlineRobots) {
        this.onlineRobots = onlineRobots;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public ConcurrentWebSocket(WebSocketSession session) {
        this.session = session;
        hz = 0;
        lastTalking = System.currentTimeMillis();
    }

    public synchronized void send(WebSocketMessage<?> webSocketMessage) {

        try {
            if (talkingStatus == 0)
                session.sendMessage(webSocketMessage);
            else
                session.sendMessage(new TextMessage("您已被禁言！！！"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized void sendGroupMsg(WebSocketMessage<?> webSocketMessage){
        try {
            if (talkingStatus == 0)
                session.sendMessage(webSocketMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long sendTime = System.currentTimeMillis();
        if (sendTime - lastTalking < 2000)//如果发送时间间隔小于两秒，则计数器+1
            hz++;
        if (sendTime - lastTalking > 30000)
            hz = 0;
        if (hz > 10)//如果超过10次频繁发言则将禁言状态改为1
            talkingStatus = 1;
        lastTalking = sendTime;
        System.out.println(hz);
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

    /*public synchronized void sendPong(PongMessage pongMessage) {
        try {
            session.sendMessage(pongMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
