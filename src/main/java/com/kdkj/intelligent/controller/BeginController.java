package com.kdkj.intelligent.controller;

import com.kdkj.intelligent.entity.Members;
import com.kdkj.intelligent.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Map;

@Controller
public class BeginController {
    @Autowired
    private BaseService baseService;

    @PostMapping("/begin")
    public String begin(Members members, Map<String,Object> map) {
        members.setBuildTime(new Date());
        Integer affectItem=baseService.add(members);
        if (affectItem==0)
            affectItem=-1;
        map.put("affectItem",affectItem);
        return  "index";
    }

    @PostMapping("/testSend")
    public String testSend(@RequestParam(value = "proxyId",required = false)Integer id,@RequestParam(value = "message",required = false)String message){



        return null;
    }

}
