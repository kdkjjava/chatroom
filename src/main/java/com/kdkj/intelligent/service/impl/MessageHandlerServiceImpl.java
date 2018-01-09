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

    //声明一个map集合用于存储关键字信息
    private volatile static Map<Integer, KeyWord> keyWordList;

    //声明一个map集合用于储存各个代理的命令关键字
    private volatile static Map<Integer, ArrayList<String>> commandWords;

    //用于存储汉字和数字之间的映射关系
    public static Map<String,String> chineseMap;

    static {
        keyWordList = new ConcurrentHashMap<Integer, KeyWord>();
        commandWords = new ConcurrentHashMap<Integer, ArrayList<String>>();
        chineseMap=new HashMap<>();
        chineseMap.put("一", "1");
        chineseMap.put("元", "1");
        chineseMap.put("二", "2");
        chineseMap.put("三", "3");
        chineseMap.put("四", "4");
        chineseMap.put("五", "5");
        chineseMap.put("六", "6");
        chineseMap.put("七", "7");
        chineseMap.put("八", "8");
        chineseMap.put("九", "9");
        chineseMap.put("〇", "0");
        chineseMap.put("○", "0");
        chineseMap.put("十", "10");
        chineseMap.put("百", "100");
    }

    /**
     * 该方法用于初始化keyWordList的值，当spring容器一加载即将数据库的参数查询出放到该变量中
     */
    @PostConstruct
    private void initialKeyWord() {
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
    }

    @Override
    public List<KeyWord> getKeyWordList() {
        return keyWordMapper.selectAll();
    }

    /**
     * 本方法用于判断用户发送的消息是否是特殊指令，若是则返回转换格式后的字符串，若不是则返回null
     *
     * @param message 用户输入的消息
     * @param proxyId 代理的id
     * @return 返回转换格式后的字符串
     */
    @Override
    public String handleMessage(String message, Integer proxyId) {
        message=cn2num(message);
        //验证用户输入的信息是否为命令指令，若是则返回该指令
        if (isCommand(message, proxyId) != null)
            return message;

        //验证用户输入信息是否为攻击指令，若为攻击指令，则替换相应的分隔符
        String msg = isAttack(message, proxyId);
        if (msg != null)
            return msg;
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
        Pattern commandPattern = Pattern.compile("^[\\u4e00-\\u9fa5]+\\d+$");
        if (message.matches(commandPattern.pattern())) {
            if (commandWords.get(proxyId).contains(message.split("\\d")[0]))
                return message;
        }
        return null;
    }

    /**
     * 本方法用于判断客户发送的信息是否为攻击指令，若是则返回替换分隔符后的字符串，若不是则返回null
     *
     * @param message 用户输入的消息
     * @param proxyId 代理的id
     * @return
     */
    private String isAttack(String message, Integer proxyId) {
        KeyWord keyWord = keyWordList.get(proxyId);
        Pattern attackPattern = Pattern.compile("^((\\d*([" + keyWord.getInnerSpace() + "]\\d*)*){0,1}([前后][1-9])+[" + keyWord.getTypeSpace() + "]{0,1}" +
                "[大小单双龙虎1-9]([" + keyWord.getInnerSpace() + "][大小单双龙虎1-9])*" +
                "[" + keyWord.getTypeSpace() + "]{0,1}\\d+["+keyWord.getGroupSpace()+"]{0,1})+$");
        if (message.matches(attackPattern.pattern()))
            return message.replaceAll("[" + keyWord.getInnerSpace() + "]", ",").replaceAll("[" + keyWord.getTypeSpace() + "]", "/").replaceAll("[" + keyWord.getGroupSpace() + "]", "#");
        return null;
    }

    /**
     * 将字符串中的一二三四五六七八九十转换成数字
     * @return
     */
    public String cn2num(String message){
        if (message.matches(".*[一二三四五六七八九十].*")){
            Iterator it=chineseMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry next = (Map.Entry) it.next();
                message=message.replaceAll((String) next.getKey(),(String) next.getValue());
            }
            return message;
        }
        return message;
    }

    @Test
    public void test1() {
        KeyWord keyWord = keyWordList.get(2);

        //验证用户输入信息是否为攻击指令，若为攻击指令，则替换相应的分隔符
        Pattern pattern = Pattern.compile("^(\\d([" + keyWord.getInnerSpace() + "]\\d)*[" + keyWord.getGroupSpace() + "]" +
                "[大小单双龙虎1-9]([" + keyWord.getInnerSpace() + "][大小单双龙虎1-9])*" +
                "[" + keyWord.getGroupSpace() + "]\\d+[|# ^&]{0,1})+$");
        String msg = "1+2/大,小/6#2/1/10#3/小:龙;虎/5|1+2+3+4/3+双/55";
        Boolean flag = msg.matches(pattern.pattern());
        if (flag) {
            msg = msg.replaceAll("[" + keyWord.getInnerSpace() + "]", ",").replaceAll("[" + keyWord.getTypeSpace() + "]", "/").replaceAll("[" + keyWord.getGroupSpace() + "]", "#");
        }
        System.out.println(msg);
    }

    @Test
    public void test2() {


    }


}
