package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.SocketMsg;
import com.kdkj.intelligent.interceptor.ChatRoomInterceptor;
import com.kdkj.intelligent.service.MembersService;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Variables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/27 14:23
 * @Description:
 **/
public class ConcurrentWebSocket{

    private static final Logger logger = LogManager.getLogger(ConcurrentWebSocket.class);

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
            if (session.isOpen())
                session.sendMessage(webSocketMessage);
        } catch (IOException e) {
            if (!session.isOpen()) {
                try {
                    session.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            logger.error(e.getMessage());
        }

    }

    public synchronized void sendBinary(SocketMsg socketMsg) {
        try {
            session.sendMessage(new BinaryMessage(Base64.getDecoder().decode(socketMsg.getBinary()), socketMsg.getStatus()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void close() {
        if (session.isOpen()) {
            try {
                this.session.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public void close(int code,String reason) {
        if (session.isOpen()) {
            try {
                this.session.close(new CloseStatus(code,reason));
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public int getHz() {
        return hz;
    }

    public void setHz(int hz) {
        this.hz = hz;
    }

    public long getLastTalking() {
        return lastTalking;
    }

    public void setLastTalking(long lastTalking) {
        this.lastTalking = lastTalking;
    }

    public void setTalkingStatus(int talkingStatus) {
        this.talkingStatus = talkingStatus;
    }
}
