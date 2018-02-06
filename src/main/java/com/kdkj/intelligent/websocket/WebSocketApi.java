package com.kdkj.intelligent.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/2/6 16:35
 * @Description:
 **/
@Component
public class WebSocketApi {

    @Autowired
    private GroupHandler groupHandler;

    /**
     * 传入群号关闭该群所有webSocket链接
     * @param groupId
     */
    public void dissolve(String groupId){
        GroupHandler.sessionPools.get(groupId).forEach((key,value) ->{
            try {
                value.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
