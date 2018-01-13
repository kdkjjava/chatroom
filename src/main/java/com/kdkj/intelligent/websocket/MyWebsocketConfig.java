package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.interceptor.ChatRoomInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Date: 2017-12-26
 * @Time: 11:13
 */
@Component
@EnableWebSocket
public class MyWebsocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer{

    @Resource
    private GroupWebSocketHandler grouphandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(grouphandler, "/groupWs").addInterceptors(new ChatRoomInterceptor()).setAllowedOrigins("*");

        //添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(grouphandler, "/groupWs/sockjs").addInterceptors(new ChatRoomInterceptor()).withSockJS();
    }
}
