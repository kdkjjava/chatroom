package com.kdkj.intelligent.websocket;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.GroupTeam;
import com.kdkj.intelligent.entity.Members;
import com.kdkj.intelligent.entity.SocketMsg;
import com.kdkj.intelligent.service.GroupTeamService;
import com.kdkj.intelligent.service.MembersService;
import com.kdkj.intelligent.util.Variables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(GroupHandler.class);

    @Autowired
    private GroupTeamService groupTeamService;

    @Autowired
    private MembersService membersService;

    //concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。其中key为房间号标识
    protected static Map<String, Map<String, ConcurrentWebSocket>> sessionPools;
    //外层key为用户名，内层key为房间号
    protected static Map<String, Map<String, List<SocketMsg>>> leaveMsg;

    private static Map<String, Map<String, String>> defenseSetting;

    static {
        sessionPools = new ConcurrentHashMap<>();
        leaveMsg = new ConcurrentHashMap<>();
        defenseSetting = new ConcurrentHashMap<>();
    }


    public static Map<String, Map<String, String>> getDefenseSetting() {
        return defenseSetting;
    }

    @PostConstruct
    private void leaveMsgInit() {
        List<GroupTeam> groupTeams = groupTeamService.selectAll();
        for (GroupTeam item : groupTeams) {
            String groupId = item.getGroupId();
            leaveMsg.put(groupId, new ConcurrentHashMap<>());
            defenseSetting.put(groupId, new ConcurrentHashMap<>());
            defenseSetting.get(groupId).put("flushSwitch", item.getFlushSwitch());
            defenseSetting.get(groupId).put("boomSwitch", item.getBoomSwitch());
        }
    }

    //握手实现连接后
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String groupId = getParam(webSocketSession, Variables.GROUPID);
        String msgFrom = getParam(webSocketSession, Variables.MSGFROM);
        webSocketSession.setBinaryMessageSizeLimit(524288);
        webSocketSession.setTextMessageSizeLimit(524288);
        Members members = membersService.selectBlockStatus(groupId, msgFrom);
        ConcurrentWebSocket concurrentWebSocket = new ConcurrentWebSocket(webSocketSession);
        //判断当前用户是否被禁言
        if (members != null && isBlock(members, concurrentWebSocket))
            concurrentWebSocket.setTalkingStatus(1);
        //将连接地址的参数groupId的值放入变量roomCode中
        if (sessionPools.containsKey(groupId)) {
            sessionPools.get(groupId).put(msgFrom, concurrentWebSocket);
        } else {
            sessionPools.put(groupId, new ConcurrentHashMap<>());
            sessionPools.get(groupId).put(msgFrom, concurrentWebSocket);
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
            if (item.getBinary() != null)
                sessionPools.get(groupId).get(msgFrom).sendBinary(item);
        });
        leaveMsg.get(groupId).remove(msgFrom);
    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        logger.error(throwable.getMessage());
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
        String groupId = getParam(webSocketSession, Variables.GROUPID);
        String msgFrom = getParam(webSocketSession, Variables.MSGFROM);
        if (sessionPools.get(groupId) != null)
            sessionPools.get(groupId).remove(msgFrom);
        if (webSocketSession.isOpen())
            webSocketSession.close();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    //发送信息前的处理
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        String groupId = getParam(webSocketSession, Variables.GROUPID);
        String msgFrom = getParam(webSocketSession, Variables.MSGFROM);
        if (Variables.PING.equals(webSocketMessage.getPayload())) {
            new Thread(() -> {
                if (sessionPools.containsKey(groupId) && sessionPools.get(groupId).containsKey(msgFrom))
                    sessionPools.get(groupId).get(msgFrom).send(new TextMessage(Variables.PONG));
            }).start();
            return;
        }
        //将用户发送的json消息解析为java对象
        SocketMsg socketMsg = JSON.parseObject(webSocketMessage.getPayload().toString(), SocketMsg.class);

        if (defenseSetting.get(groupId) != null && "on".equals(defenseSetting.get(groupId).get("boomSwitch")) && socketMsg.getMsg().length() > 200) {
            webSocketSession.close(new CloseStatus(4995, "消息内容过长，您将被踢出群聊"));
            membersService.deleteMemberShip(groupId, msgFrom);
            return;
        }
        ConcurrentWebSocket concurrentWebSocket = sessionPools.get(groupId).get(msgFrom);
        //记录发送间隔
        recording(concurrentWebSocket);
        if (concurrentWebSocket.getTalkingStatus() != 0) {
            concurrentWebSocket.send(new TextMessage("禁言"));
            return;
        }
        //调用普通信息的发送方法
        new Thread(() -> sendUsualMessage(concurrentWebSocket, socketMsg, webSocketMessage)).start();
        String masterName = groupTeamService.selectMasterNameByGroupId(socketMsg.getGroupId());
        if (groupTeamService.findMembership(socketMsg.getMsgFrom(), socketMsg.getGroupId()) && ProxyHandler.masterSessionPools.containsKey(masterName)) {
            sendToClient(socketMsg, masterName);
        }

    }

    /**
     * 本方法用于处理玩家发送的普通消息
     *
     * @param concurrentWebSocket 当前session对象
     */
    private void sendUsualMessage(ConcurrentWebSocket concurrentWebSocket, SocketMsg socketMsg, WebSocketMessage<?> webSocketMessage) {


        String groupId = (String) concurrentWebSocket.getSession().getAttributes().get(Variables.GROUPID);

        if (!groupId.equals(socketMsg.getGroupId())) {
            concurrentWebSocket.send(new TextMessage("{\"errorCode\":\"请求参数错误！\"}"));
        }
        if (concurrentWebSocket.getTalkingStatus() == 0) {
            //遍历map集合，将消息发送至同一个房间下的session
            if (sessionPools.containsKey(socketMsg.getGroupId()))
                sessionPools.get(socketMsg.getGroupId()).forEach((key, item) -> item.send(webSocketMessage));
            List<String> groupMembers = membersService.selectUsernameInGroup(groupId);
            groupMembers.forEach(item -> {
                if (TotalHandler.totalSessions.containsKey(item) && !sessionPools.get(groupId).containsKey(item)) {
                    synchronized (this) {
                        if (!leaveMsg.get(groupId).containsKey(item))
                            leaveMsg.get(groupId).put(item, new CopyOnWriteArrayList<>());
                    }
                    leaveMsg.get(groupId).get(item).add(socketMsg);
                }
            });
        } else {
            concurrentWebSocket.send(new TextMessage("发送频率过高，您已被禁言！！！"));
        }
    }

    /**
     * 本方法用于发送信息至客户端
     *
     * @param socketMsg  消息对象
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

    /**
     * 记录发言时间间隔
     *
     * @param concurrentWebSocket
     */
    private void recording(ConcurrentWebSocket concurrentWebSocket) {
        long sendTime = System.currentTimeMillis();
        if (sendTime - concurrentWebSocket.getLastTalking() < 2000)//如果发送时间间隔小于两秒，则计数器+1
            concurrentWebSocket.setHz(concurrentWebSocket.getHz() + 1);
        if (sendTime - concurrentWebSocket.getLastTalking() > 30000)
            concurrentWebSocket.setHz(0);
        //如果超过10次频繁发言则将禁言状态改为1
        if (concurrentWebSocket.getHz() > 10) {
            concurrentWebSocket.setTalkingStatus(1);
            membersService.updateSpeakStatus((String) concurrentWebSocket.getSession().getAttributes().get(Variables.MSGFROM), (String) concurrentWebSocket.getSession().getAttributes().get(Variables.GROUPID), 1);
        }
        concurrentWebSocket.setLastTalking(sendTime);
    }

    /**
     * 判断是否被禁言
     */
    private boolean isBlock(Members members, ConcurrentWebSocket concurrentWebSocket) {
        if (members.getBlock() == 0)
            return false;
        Date date = members.getBlockTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 1);
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        if (now.before(calendar))
            return true;
        else {
            membersService.updateSpeakStatus((String) concurrentWebSocket.getSession().getAttributes().get(Variables.MSGFROM), (String) concurrentWebSocket.getSession().getAttributes().get(Variables.GROUPID), 0);
            return false;
        }
    }


}