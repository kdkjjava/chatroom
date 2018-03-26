package com.kdkj.intelligent.controller;

import com.kdkj.intelligent.entity.Members;
import com.kdkj.intelligent.service.BaseService;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("test")
public class BeginController {

    Logger logger ;
    {
        logger = LoggerFactory.getLogger("chatroom");

    }

    @Autowired
    private BaseService baseService;

    @Autowired
    private UsersService usersService;

    @PostMapping("/begin")
    public String begin(Members members, Map<String,Object> map) {
        members.setBuildTime(new Date());
        Integer affectItem=baseService.add(members);
        if (affectItem==0)
            affectItem=-1;
        map.put("affectItem",affectItem);
        return  "index";
    }
 /*   @ResponseBody
    @PostMapping("/testSend")
    public String testSend(@RequestParam(value = "masterId",required = false)Integer masterId,@RequestParam(value = "message",required = false)String message){
        String str=messageHandlerService.handleMessage(message,masterId);
        if (str==null)
            return "false";
        return str;
    }*/

    @GetMapping(value = "socket")
    public String socket(@RequestParam(value = "groupId",required = false) String groupId, Map<String,Object> map,
                         HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "msgFrom")String msgFrom){
        map.put("groupId",groupId);
        map.put("msgFrom",msgFrom);
        return "webSocket";
    }
    @ResponseBody
    @PostMapping("hasExpire")
    public Result hasExpire(@RequestParam("username")String username){
        if (usersService.hasExpired(username))
            return Result.error("该用户已过期");
        return Result.ok("用户可正常使用");
    }

    @PostConstruct
    public void test(){
        try {
            int i =1/0;
        }catch (ArithmeticException e){
            logger.error("想你想我想你想我想你想我想你想我想你想我想你想我想你想我想你想我想你想我想你想我想你想我想你想我想你想我想你想我想你想我想你想我");
        }
    }


}
