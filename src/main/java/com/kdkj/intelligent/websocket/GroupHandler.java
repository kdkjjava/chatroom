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
    private static volatile Map<Integer, Map<String,List<WebSocketSession>>> sessionPools;
    //该变量用于保存master的session
    private static volatile Map<String, WebSocketSession> masterSessionPools;
    //该变量用于保存管理员的session
    private static WebSocketSession adminSession;

    static {
        sessionPools = new ConcurrentHashMap();
        masterSessionPools = new ConcurrentHashMap();
    }

    @PostConstruct
    public void sessionPoolsInitial(){
        Users user =new Users();
        user.setType("1");
        List<Users> masters = usersService.selectListByUser(user);
        for (Users u :masters){
            sessionPools.put(u.getId(),new ConcurrentHashMap());
        }

    }


    //握手实现连接后
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String groupId= (String) webSocketSession.getAttributes().get("groupId");
        Integer masterId = groupTeamService.selectMasterIdByGroupId(Integer.parseInt(groupId));
        String msgFrom = (String) webSocketSession.getAttributes().get("msgFrom");
        String userType = usersService.selectTypeByUserName(msgFrom);
        if ("1".equals(userType)){
            if (!masterSessionPools.containsKey(msgFrom))
                masterSessionPools.put(msgFrom,webSocketSession);
        }else if("0".equals(userType)){
            adminSession = webSocketSession;
        }else if("2".equals(userType)){
            //将连接地址的参数groupId的值放入变量roomCode中
            if (sessionPools.get(masterId).containsKey(groupId)) {
                sessionPools.get(masterId).get(groupId).add(webSocketSession);
            } else {
                sessionPools.get(masterId).put(groupId, new ArrayList<>());
                sessionPools.get(masterId).get(groupId).add(webSocketSession);
            }
        }
    }


    @Override
    public  void  handleTransportError(WebSocketSession webSocketSession, Throwable throwable){
        throw new UnsupportedOperationException("发生handleTransportError错误！");
    }

    /**
     * 在此刷新页面就相当于断开WebSocket连接,原本在静态变量userSocketSessionMap中的
     * WebSocketSession会变成关闭状态(close)，但是刷新后的第二次连接服务器创建的
     * 新WebSocketSession(open状态)又不会加入到userSocketSessionMap中,所以这样就无法发送消息
     * 因此应当在关闭连接这个切面增加去除userSocketSessionMap中当前处于close状态的WebSocketSession，
     * 让新创建的WebSocketSession(open状态)可以加入到userSocketSessionMap中
     * @param webSocketSession
     * @param closeStatus
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        String userType = usersService.selectTypeByUserName((String) webSocketSession.getAttributes().get("msgFrom"));
        String groupId= (String) webSocketSession.getAttributes().get("groupId");
        Integer masterId = groupTeamService.selectMasterIdByGroupId(Integer.parseInt(groupId));
        if ("1".equals(userType)){
            masterSessionPools.remove(masterId);
        }else if ("2".equals(userType)){
            sessionPools.get(masterId).get(groupId).remove(webSocketSession);
        }
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
        String userType = usersService.selectTypeByUserName(socketMsg.getMsgFrom());

        if("2".equals(userType)){
            Integer masterId = groupTeamService.selectMasterIdByGroupId(Integer.parseInt(socketMsg.getMsgTo()));
            //设至当前消息的发送信息
            socketMsg.setMsgFrom(socketMsg.getMsgFrom());
            socketMsg.setGroupId(socketMsg.getMsgTo());
            if (groupTeamService.findMembership(socketMsg.getMsgFrom(),socketMsg.getMsgTo())){
                if (masterSessionPools.containsKey(masterId)){
                    sendToClient(webSocketMessage,socketMsg,masterId);
                }
            }
            //调用普通信息的发送方法
            sendUsualMessage(webSocketSession,socketMsg,masterId);
            return;
        }else if ("1".equals(userType)){
            Users user =new Users();
            user.setUsername(socketMsg.getMsgFrom());
            Integer masterId = usersService.selectListByUser(user).get(0).getId();
            sendUsualMessage(webSocketSession,socketMsg,masterId);
            return;
        }else if ("0".equals(userType)){

        }

    }


    /**
     * 本方法用于处理玩家发送的普通消息
     * @param webSocketSession
     */

    public void sendUsualMessage(WebSocketSession webSocketSession,SocketMsg socketMsg,Integer masterId){
        //遍历map集合，将消息发送至同一个房间下的session
        Iterator<Map.Entry<String, List<WebSocketSession>>> iterator = sessionPools.get(masterId).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<WebSocketSession>> entry = iterator.next();
            if (entry.getKey().equals(webSocketSession.getAttributes().get("groupId"))) {
                //判断若是为同一个房间，则遍历房间内的session，并发送消息
                for (WebSocketSession item : entry.getValue()) {
                    try {
                        //将本条消息通过WebSocketSession发送至客户端
                        item.sendMessage(new TextMessage(JSON.toJSON(socketMsg).toString()));
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
     * @param webSocketSession
     * @param webSocketMessage
     */
    public void sendAdviceMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage){
        //TODO
    }



    /**
     * 本方法用于处理客户端发送的图片
     * @param webSocketSession
     * @param webSocketMessage
     */
    public void pictureFromClient(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage){
        //TODO
    }

    /**
     * 本方法用于发送信息至客户端
     * @param webSocketMessage
     * @param socketMsg
     * @param masterId
     */
    public void sendToClient( WebSocketMessage<?> webSocketMessage, SocketMsg socketMsg, Integer masterId){
        WebSocketSession session = masterSessionPools.get(masterId);
        try {
            session.sendMessage(new TextMessage(JSON.toJSON(socketMsg).toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}