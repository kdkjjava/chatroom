package com.kdkj.intelligent.util;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

public class MySessionContext {  
    private static MySessionContext instance;  
    private HashMap<String,HttpSession> sessionMap;  
  
    private MySessionContext() {  
        sessionMap = new HashMap<String,HttpSession>();  
    }  
  
    public static MySessionContext getInstance() {  
        if (instance == null) {  
            instance = new MySessionContext();  
        }  
        return instance;  
    }  
  
    public synchronized void addSession(String username,HttpSession session) {  
        if (session != null) {  
            sessionMap.put(username, session);  
        }  
    }  
  
    public synchronized void delSession(String username) {  
    	if (username == null) {  
    		HttpSession session=sessionMap.get(username);
    		sessionMap.remove(username);
    		session.invalidate();
        } 
    }  
  
    public synchronized HttpSession getSession(String username) {  
        if (username == null) {  
            return null;  
        }  
        return sessionMap.get(username);  
    }  
  
}