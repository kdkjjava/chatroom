package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.entity.AdminMsg;

import java.util.Calendar;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/16 17:21
 * @Description:
 **/
public class TimedTask {

    public void handlePastMsg(){

        for (AdminMsg adminMsg:AdminHandler.adviceMsg){
            Long date =Long.parseLong(adminMsg.getDate());

            Calendar calendar =Calendar.getInstance();
        }

    }
}
