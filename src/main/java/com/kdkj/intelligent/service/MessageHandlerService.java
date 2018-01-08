package com.kdkj.intelligent.service;

import com.kdkj.intelligent.entity.KeyWord;

import java.util.List;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/8 11:53
 * @Description: 该接口用于处理玩家发送的信息
 **/
public interface MessageHandlerService {

    String handleMessage(String message,Integer proxyId);

    List<KeyWord> getKeyWordList();
}
