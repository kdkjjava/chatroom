package com.kdkj.intelligent.service.impl;

import com.kdkj.intelligent.dao.KeyWordMapper;
import com.kdkj.intelligent.entity.KeyWord;
import com.kdkj.intelligent.service.MessageHandlerService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/8 11:56
 * @Description: 本类用于对消息的处理
 **/
//@Service
public class MessageHandlerServiceImpl implements MessageHandlerService {

    @Autowired
    private KeyWordMapper keyWordMapper;

    /**
     * 声明一个map集合用于存储关键字信息
     */
    private static Map<Integer, KeyWord> keyWordList;
    /**
     * 声明一个map集合用于储存各个代理的命令关键字
     */
    private static Map<Integer, ArrayList<String>> commandWords;

    static {
        keyWordList = new ConcurrentHashMap<Integer, KeyWord>();
        commandWords = new ConcurrentHashMap<Integer, ArrayList<String>>();
    }

    /**
     * 该方法用于初始化keyWordList的值，当spring容器一加载即将数据库的参数查询出放到该变量中
     */
  /*  @PostConstruct
    private void initialKeyWord() {
        List<KeyWord> keyWords = keyWordMapper.selectAll();
        for (KeyWord keyWord : keyWords) {
            keyWordList.put(keyWord.getId(), keyWord);

            ArrayList<String> commandList = commandWords.put(keyWord.getId(), new ArrayList<>());

            //将命令关键字全部添加到commandWords的集合中
            commandList.addAll(Arrays.asList(keyWord.getUpperKey().split(" ")));
            commandList.addAll(Arrays.asList(keyWord.getLowerKey().split(" ")));
            commandList.addAll(Arrays.asList(keyWord.getModifyAttack().split(" ")));
            commandList.addAll(Arrays.asList(keyWord.getDeleteAttack().split(" ")));
            commandList.addAll(Arrays.asList(keyWord.getQueryAttack().split(" ")));
            commandList.addAll(Arrays.asList(keyWord.getCountKey().split(" ")));
        }

    }*/


    /**
     * 本方法用于判断用户发送的消息是否是特殊指令，若是则返回转换格式后的字符串，若不是则返回null
     *
     * @param message 用户输入的消息
     * @param proxyId 代理商的id
     * @return 返回转换格式后的字符串
     */
    @Override
    public String handleMessage(String message, Integer proxyId) {
        if (isCommand(message,proxyId)!=null)
            return message;

        return null;
    }

    /**
     * 本方法用于判断客户发送的信息是否为操作互动识别关键字，若是则返回该字符串，若不是则返回null
     *
     * @param message 用户输入的消息
     * @param proxyId 代理商的id
     * @return
     */
    private String isCommand(String message, Integer proxyId) {
        Boolean flag = commandWords.get(proxyId).contains(message);
        if (flag)
            return "yes";
        return null;
    }


}
