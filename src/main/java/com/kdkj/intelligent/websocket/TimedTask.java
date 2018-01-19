package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.entity.AdminMsg;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/16 17:21
 * @Description:
 **/
public class TimedTask {

    public void handlePastMsg() {

        Calendar calForNow = Calendar.getInstance();
        calForNow.setTime(new Date());
        int dayNow = calForNow.get(Calendar.DAY_OF_YEAR);
        int yearNow = calForNow.get(Calendar.YEAR);
        deletePastMsg(dayNow,yearNow,AdminHandler.adviceMsg);
        deletePastMsg(dayNow,yearNow,AdminHandler.broadCastMsg);
    }

    private void deletePastMsg(int dayNow,int yearNow,List<AdminMsg> adminMsgList){
        for (AdminMsg adminMsg : adminMsgList) {
            Long date = Long.parseLong(adminMsg.getDate());
            Calendar calForMsg = Calendar.getInstance();
            calForMsg.setTimeInMillis(date);
            //超过七天后从集合中移除该对象
            if (getTimeDistance(dayNow,yearNow,calForMsg)>=7)
                AdminHandler.adviceMsg.remove(adminMsg);
        }
    }

    private int getTimeDistance(int dayNow,int yearNow,Calendar calForMsg){
        int dayForMsg = calForMsg.get(Calendar.DAY_OF_YEAR);
        int yearForMsg = calForMsg.get(Calendar.YEAR);
        System.out.println("时间差距");
        if (yearNow != yearForMsg){//不同年
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
            System.out.println("判断day2 - day1 : " + (dayNow - dayForMsg));
            return dayNow - dayForMsg;
        }
    }

}
