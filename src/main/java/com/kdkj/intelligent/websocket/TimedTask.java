package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.entity.AdminMsg;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        Runnable task = new Runnable(){
            @Override
            public void run() {
                Calendar calForNow = Calendar.getInstance();
                calForNow.setTime(new Date());
                int dayNow = calForNow.get(Calendar.DAY_OF_YEAR);
                int yearNow = calForNow.get(Calendar.YEAR);
                deletePastMsg(dayNow,yearNow,AdminHandler.adviceMsg);
                deletePastMsg(dayNow,yearNow,AdminHandler.broadCastMsg);
            }
        };
        new Thread(task).start();

    }

    private void deletePastMsg(int dayNow,int yearNow,List<AdminMsg> adminMsgList){
        for (AdminMsg adminMsg : adminMsgList) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = null;
            try {
                date = sdf.parse(adminMsg.getDate());//将消息的时间字符串转换成日期格式
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calForMsg = Calendar.getInstance();
            calForMsg.setTime(date);
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
            //System.out.println("判断day2 - day1 : " + (dayNow - dayForMsg));
            return dayNow - dayForMsg;
        }
    }


}
