package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.entity.AdminMsg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/16 17:21
 * @Description:
 **/
public class TimedTask {


    /**
     * 该方法用于被执行的定时任务，定时清理无用的消息
     */
    public void handlePastMsg() {

        Calendar calForNow = Calendar.getInstance();
        calForNow.setTime(new Date());
        int dayNow = calForNow.get(Calendar.DAY_OF_YEAR);
        int yearNow = calForNow.get(Calendar.YEAR);
        deletePastMsg(dayNow, yearNow, AdminHandler.adviceMsg);
        deletePastMsg(dayNow, yearNow, AdminHandler.broadCastMsg);

    }

    /**
     * 该方法用于处理不处于活跃状态的群
     */
    public void handleUnUseGroup() {

        GroupHandler.sessionPools.forEach((key, value) -> {
            if (value.size() == 0)
                GroupHandler.sessionPools.remove(key);
        });
        System.out.println(GroupHandler.sessionPools);
    }

    private void deletePastMsg(int dayNow, int yearNow, List<AdminMsg> adminMsgList) {
        for (AdminMsg adminMsg : adminMsgList) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = null;
            date = adminMsg.getDate();//将消息的时间字符串转换成日期格式
            Calendar calForMsg = Calendar.getInstance();
            calForMsg.setTime(date);
            //超过七天后从集合中移除该对象
            if (getTimeDistance(dayNow, yearNow, calForMsg) >= 7)
                AdminHandler.adviceMsg.remove(adminMsg);
        }
    }

    private int getTimeDistance(int dayNow, int yearNow, Calendar calForMsg) {
        int dayForMsg = calForMsg.get(Calendar.DAY_OF_YEAR);
        int yearForMsg = calForMsg.get(Calendar.YEAR);
        if (yearNow != yearForMsg) {//不同年
            int timeDistance = 0;
            for (int i = yearForMsg; i < yearNow; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }
            return timeDistance + (dayNow - dayForMsg);
        } else {//同年
            return dayNow - dayForMsg;
        }
    }

}
