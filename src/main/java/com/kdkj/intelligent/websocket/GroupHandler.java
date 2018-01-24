package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.SocketMsg;
import com.kdkj.intelligent.service.GroupTeamService;
import com.kdkj.intelligent.service.UsersService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.*;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GroupHandler implements WebSocketHandler {

    @Autowired
    private GroupTeamService groupTeamService;

    @Autowired
    private UsersService usersService;

    //concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。其中key为房间号标识
    protected static Map<String, List<WebSocketSession>> sessionPools;

    static {
        sessionPools = new ConcurrentHashMap();
    }

    //握手实现连接后
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String groupId = getGroupId(webSocketSession);
        //将连接地址的参数groupId的值放入变量roomCode中
        if (sessionPools.containsKey(groupId)) {
            sessionPools.get(groupId).add(webSocketSession);
        } else {
            sessionPools.put(groupId, new CopyOnWriteArrayList());
            sessionPools.get(groupId).add(webSocketSession);
        }
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
        String groupId = getGroupId(webSocketSession);
        sessionPools.get(groupId).remove(webSocketSession);
        webSocketSession.close();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    //发送信息前的处理
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        //将用户发送的json消息解析为java对象
        SocketMsg socketMsg = JSON.parseObject(webSocketMessage.getPayload().toString(), SocketMsg.class);
        Integer masterId = groupTeamService.selectMasterIdByGroupId(Integer.parseInt(socketMsg.getGroupId()));
        socketMsg.setStatus("success");
        if (groupTeamService.findMembership(socketMsg.getMsgFrom(), socketMsg.getGroupId()) && ProxyHandler.masterSessionPools.containsKey(masterId)) {
            sendToClient(socketMsg, masterId);
        }
        //调用普通信息的发送方法
        new Thread(() -> sendUsualMessage(webSocketSession, socketMsg)).start();
    }

    /**
     * 本方法用于处理玩家发送的普通消息
     *
     * @param webSocketSession 当前session对象
     */
    private void sendUsualMessage(WebSocketSession webSocketSession, SocketMsg socketMsg) {
        String groupId = (String) webSocketSession.getAttributes().get("groupId");
        if (!groupId.equals(socketMsg.getGroupId())) {
            try {
                webSocketSession.sendMessage(new TextMessage("{\"errorCode\":\"请求参数错误！\"}"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //遍历map集合，将消息发送至同一个房间下的session
        if (sessionPools.containsKey(socketMsg.getGroupId())) {
            sessionPools.get(socketMsg.getGroupId()).forEach((item) -> {
                try {
                    item.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File file = new File("D:/aaa.jpg");
                InputStream is = null;
                try {
                    is = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (is == null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    BufferedInputStream bis = new BufferedInputStream(is);
                    OutputStream os = new ByteArrayOutputStream();
                    BufferedOutputStream bos = new BufferedOutputStream(os);
                    byte[] b = new byte[1024];
                    int len;
                    try {
                        int i =0;
                        while ((len = bis.read(b)) != -1) {
                            bos.write(b, 0, len);
                            Boolean flag =false;
                            if (len !=1024)
                                flag=true;
                            item.sendMessage(new BinaryMessage(b, flag));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    /**
     * 本方法用于发送信息至客户端
     *
     * @param socketMsg 消息对象
     * @param masterId  代理
     */
    private void sendToClient(SocketMsg socketMsg, Integer masterId) {
        WebSocketSession session = ProxyHandler.masterSessionPools.get(masterId);
        socketMsg.setMasterName(usersService.selectByPrimaryKey(masterId).getUsername());
        socketMsg.setStatus("command");
        try {
            session.sendMessage(new TextMessage(JSON.toJSONString(socketMsg)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过webSocketSession得到作用域中的groupId
     *
     * @param webSocketSession 当前session对象
     * @return
     */
    private String getGroupId(WebSocketSession webSocketSession) {
        return (String) webSocketSession.getAttributes().get("groupId");
    }


}