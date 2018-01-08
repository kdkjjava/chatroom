package com.kdkj.intelligent.service.impl;

import com.kdkj.intelligent.service.MessageHandlerService;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/8 11:56
 * @Description:
 **/
public class MessageHandlerServiceImpl implements MessageHandlerService{




    /**
     * 本方法用于判断用户发送的消息是否是特殊指令，若是则返回转换格式后的字符串，若不是则返回null
     * @param message 用户输入的消息
     * @param proxyId 代理商的id
     * @return 返回转换格式后的字符串
     */
    @Override
    public String isCommand(String message,Integer proxyId) {


        return null;
    }
}
