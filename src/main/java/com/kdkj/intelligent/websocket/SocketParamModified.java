package com.kdkj.intelligent.websocket;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/16 15:00
 * @Description: 该类用于提供修改websocket包下面的参数的接口
 **/
@Component
public class SocketParamModified {

    /**
     * @param proxyId
     */
    public void addProxyCache(Integer proxyId){
        GroupHandler.sessionPools.put(proxyId,new ConcurrentHashMap());
        TotalHandler.totalSessions.put(proxyId, new ConcurrentHashMap());
    }

    public void removeProxyCache(Integer proxyId){
        GroupHandler.sessionPools.remove(proxyId);
        TotalHandler.totalSessions.remove(proxyId);
    }

}
