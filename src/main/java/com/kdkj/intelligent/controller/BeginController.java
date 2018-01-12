package com.kdkj.intelligent.controller;

import com.kdkj.intelligent.entity.Members;
import com.kdkj.intelligent.service.BaseService;
import com.kdkj.intelligent.service.MessageHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("test")
public class BeginController {
    @Autowired
    private BaseService baseService;
    @Autowired
    private MessageHandlerService messageHandlerService;

    @PostMapping("/begin")
    public String begin(Members members, Map<String,Object> map) {
        members.setBuildTime(new Date());
        Integer affectItem=baseService.add(members);
        if (affectItem==0)
            affectItem=-1;
        map.put("affectItem",affectItem);
        return  "index";
    }
    @ResponseBody
    @PostMapping("/testSend")
    public String testSend(@RequestParam(value = "masterId",required = false)Integer masterId,@RequestParam(value = "message",required = false)String message){
        String str=messageHandlerService.handleMessage(message,masterId);
        if (str==null)
            return "false";
        return str;
    }

    @GetMapping(value = "socket")
    public String socket(@RequestParam(value = "roomNum",required = false) String roomNum, Map<String,Object> map, HttpServletRequest request, HttpServletResponse response){
        map.put("roomNum",roomNum);
        return "webSocket";
    }


}
