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

    //声明一个map集合用于储存各个代理的命令关键字(命令后不接数字)
    private volatile static Map<Integer, ArrayList<String>> commandNoNum;

    //声明一个map集合用于储存各个代理的命令关键字(命令后接数字)
    private volatile static Map<Integer, ArrayList<String>> commandWithNum;

    //用于存储汉字和数字之间的映射关系
    public static Map<String, String> chineseMap;

    static {
        keyWordList = new ConcurrentHashMap<Integer, KeyWord>();
        commandNoNum = new ConcurrentHashMap<Integer, ArrayList<String>>();
        commandWithNum = new ConcurrentHashMap<Integer, ArrayList<String>>();
        chineseMap = new HashMap<>();
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
            commandNoNum.put(keyWord.getMasterId(), new ArrayList<>());
            commandWithNum.put(keyWord.getMasterId(), new ArrayList<>());
            ArrayList<String> noNum = commandNoNum.get(keyWord.getMasterId());
            ArrayList<String> withNum = commandWithNum.get(keyWord.getMasterId());
            //将命令关键字全部添加到commandNoNum的集合中
            withNum.addAll(Arrays.asList(keyWord.getUpperKey().split(" ")));
            withNum.addAll(Arrays.asList(keyWord.getLowerKey().split(" ")));
            noNum.addAll(Arrays.asList(keyWord.getModifyAttack().split(" ")));
            noNum.addAll(Arrays.asList(keyWord.getDeleteAttack().split(" ")));
            noNum.addAll(Arrays.asList(keyWord.getQueryAttack().split(" ")));
            noNum.addAll(Arrays.asList(keyWord.getCountKey().split(" ")));
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
        message = cn2num(message);
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
        Pattern noNumPattern = Pattern.compile("^[\\u4e00-\\u9fa5]+$");
        Pattern withNumPattern = Pattern.compile("^[\\u4e00-\\u9fa5]+\\d+$");

        if (message.matches(noNumPattern.pattern())) {
            if (commandNoNum.get(proxyId).contains(message))
                return message;
        }
        if (message.matches(withNumPattern.pattern())) {
            if (commandWithNum.get(proxyId).contains(message.split("\\d")[0]))
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
        String group=keyWord.getGroupSpace();
        String type=keyWord.getTypeSpace();
        String inner=keyWord.getInnerSpace();
        if (message.split("["+group+"]").length==1){
            //通过验证方法判断是否为攻击指令，若是则返回字符串
            String volidate = volidate(message, group, type, inner);
            if (volidate!=null)
                return volidate;
        }else {
            int flag=1;
            for (String msg : message.split("["+group+"]")){
                if (volidate(msg, group, type, inner)==null){
                    flag=0;
                    break;
                }
            }
           if (flag==1)
                return message.replaceAll("[和合特]","冠亚").replaceAll("[" + inner + "]", ",").replaceAll("[" + type + "]", "/").replaceAll("[" + group + "]", "#");
        }

        return null;
    }

    /**
     * 对单元攻击指令的验证方法
     * @param message 玩家发送的指令
     * @param group 组分隔符
     * @param type 类型分隔符
     * @param inner 同种类型中间的分隔符
     * @return
     */
    public String volidate(String message,String group,String type,String inner){
        Pattern attackPattern = Pattern.compile(regix1(group,type,inner));
        if (message.matches(attackPattern.pattern()))
            return message.replaceAll("[" + inner + "]", ",").replaceAll("[" + type + "]", "/").replaceAll("[" + group + "]", "#");
        Pattern attackPattern2 = Pattern.compile(regix3(group,type,inner));
        if (message.matches(attackPattern2.pattern()))
            return message.replaceAll("[" + inner + "]", ",").replaceAll("[" + type + "]", "/").replaceAll("[" + group + "]", "#");
        Pattern attackPattern3 = Pattern.compile(regix4(group,type,inner));
        if (message.matches(attackPattern3.pattern()))
            return message.replaceAll("[和合特]","冠亚").replaceAll("[" + inner + "]", ",").replaceAll("[" + type + "]", "/").replaceAll("[" + group + "]", "#");
        return null;
    }

    /**
     * 将字符串中的一二三四五六七八九十转换成数字
     *
     * @return
     */
    public String cn2num(String message) {
        if (message.matches(".*[一二三四五六七八九十].*")) {
            Iterator it = chineseMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry next = (Map.Entry) it.next();
                message = message.replaceAll((String) next.getKey(), (String) next.getValue());
            }
            return message;
        }
        return message;
    }
    /**
     * 以下的regix*的方法用于返回正则验证表达式
     * @param group 每组攻击之间的间隔符
     * @param type  单组攻击之间的类型分割符
     * @param inner 单种类型的分隔符
     * @return
     */
    private String regix1(String group,String type,String inner){
        return "^((\\d*([" + inner + "]{0,1}\\d*)*){0,1}(["
                + inner + "]{0,1}[前后][1-9])*[" + type + "]{0,1}" +
                "[大小单双龙虎0-9]([" + inner + "]{0,1}[大小单双龙虎1-9])*" +
                "[" + type + "]{0,1}\\d+)$";
    }
    private String regix2(String group,String type,String inner){
        return  "^((\\d*([" + inner + "]{0,1}\\d*)*){0,1}(["
                + inner + "]{0,1}[前后][1-9])*[" + type + "]{0,1}" +
                "[大小单双龙虎0-9]([" + inner + "]{0,1}[大小单双龙虎1-9])*" +
                "[" + type + "]{0,1}\\d+[" + group + "]{0,1})+$";
    }
    private String regix3(String group,String type,String inner){
        return  "^(((冠亚){0,1})[" + type + "]{0,1}" +
                "[大小单双龙虎0-9]([" + inner + "]{0,1}[大小单双龙虎1-9])*" +
                "[" + type + "]{0,1}\\d+)$";
    }
    private String regix4(String group,String type,String inner){
        return  "^(([合和特])[" + type + "]{0,1}" +
                "[大小单双龙虎0-9]([" + inner + "]{0,1}[大小单双龙虎1-9])*" +
                "[" + type + "]{0,1}\\d+)$";
    }

    @Test
    public void test() {


    }

}
