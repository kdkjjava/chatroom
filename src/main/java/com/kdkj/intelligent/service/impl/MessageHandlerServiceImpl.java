package com.kdkj.intelligent.service.impl;

import com.kdkj.intelligent.dao.KeyWordMapper;
import com.kdkj.intelligent.entity.KeyWord;
import com.kdkj.intelligent.service.MessageHandlerService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/8 11:56
 * @Description: 本类用于对消息的处理
 **/
@Service
public class MessageHandlerServiceImpl implements MessageHandlerService {

    @Autowired
    private KeyWordMapper keyWordMapper;

    /**
     * 声明一个map集合用于存储关键字信息
     */
    private volatile static Map<Integer, KeyWord> keyWordList;
    /**
     * 声明一个map集合用于储存各个代理的命令关键字
     */
    private volatile static Map<Integer, ArrayList<String>> commandWords;

    static {
        keyWordList = new ConcurrentHashMap<Integer, KeyWord>();
        commandWords = new ConcurrentHashMap<Integer, ArrayList<String>>();

    }

    /**
     * 该方法用于初始化keyWordList的值，当spring容器一加载即将数据库的参数查询出放到该变量中
     */
    @PostConstruct
    public void initialKeyWord() {
        List<KeyWord> keyWords = getKeyWordList();
        for (KeyWord keyWord : keyWords) {
            keyWordList.put(keyWord.getMasterId(), keyWord);

            commandWords.put(keyWord.getMasterId(), new ArrayList<>());
            ArrayList<String> commandList = commandWords.get(keyWord.getMasterId());
            //将命令关键字全部添加到commandWords的集合中
            commandList.addAll(Arrays.asList(keyWord.getUpperKey().split(" ")));
            commandList.addAll(Arrays.asList(keyWord.getLowerKey().split(" ")));
            commandList.addAll(Arrays.asList(keyWord.getModifyAttack().split(" ")));
            commandList.addAll(Arrays.asList(keyWord.getDeleteAttack().split(" ")));
            commandList.addAll(Arrays.asList(keyWord.getQueryAttack().split(" ")));
            commandList.addAll(Arrays.asList(keyWord.getCountKey().split(" ")));
        }
        System.out.println(keyWordList.get(2).getTypeSpace());

    }


    /**
     * 本方法用于判断用户发送的消息是否是特殊指令，若是则返回转换格式后的字符串，若不是则返回null
     *
     * @param message 用户输入的消息
     * @param proxyId 代理商的id
     * @return 返回转换格式后的字符串
     */
    @Override
    public String handleMessage(String message, Integer proxyId) {
        if (isCommand(message, proxyId) != null)
            return message;

        return null;
    }

    @Override
    public List<KeyWord> getKeyWordList() {
        return keyWordMapper.selectAll();
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

    /**
     * 该方法用于判断玩家传输的信息是否为攻击指令，若是，则转换格式化为对应的字符串
     *
     * @param message 用户输入的消息
     * @param proxyId 代理商的id
     * @return
     */
    private String messageFormat(String message, Integer proxyId) {
        KeyWord keyWord=keyWordList.get(proxyId);
        String groupSpace = keyWord.getGroupSpace();
        groupSpace = groupSpace.replaceAll("空格", " ");

        Pattern pattern = Pattern.compile("\\d"+"["+keyWord.getTypeSpace()+"]"+"[大小单双龙虎]"+"["+keyWord.getTypeSpace()+"]"+"[1-9]+\\d*");

        return null;
    }

    @Test
    public void test() {

        String str ="-/\\=、";
        Pattern pattern = Pattern.compile("^\\d"+"["+str+"]"+"[大小单双龙虎]"+"["+str+"]"+"[1-9]+\\d*$");
        System.out.println("1\\大\\9".matches(pattern.pattern()));
        System.out.println(str);
    }

}
