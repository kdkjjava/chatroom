package com.kdkj.intelligent.websocket;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/27 14:23
 * @Description:
 **/
public class ConcurrentWebSocket {

    private WebSocketSession session;

    public WebSocketSession getSession() {
        return session;
    }

    public ConcurrentWebSocket(WebSocketSession session) {
        this.session = session;
    }

    public synchronized void send(WebSocketMessage<?> webSocketMessage) {
        try {
            session.sendMessage(webSocketMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendBinary(BufferedInputStream bis, BufferedOutputStream bos) {
        byte[] b = new byte[1024];
        int len;
        try {
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
                Boolean flag = false;
                if (bis.available() == 0)
                    flag = true;
                session.sendMessage(new BinaryMessage(b, flag));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
