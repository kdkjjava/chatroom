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
    private GroupHandler grouphandler;

    @Resource
    private FriendHandler friendHandler;

    @Resource
    private ProxyHandler proxyHandler;

    @Resource
    private TotalHandler totalHandler;

    @Resource
    private AdminHandler adminHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //给群聊添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(grouphandler, "/groupWs").addInterceptors(new ChatRoomInterceptor()).setAllowedOrigins("*");
        //添加群聊websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(grouphandler, "/groupWs/sockjs").addInterceptors(new ChatRoomInterceptor()).setAllowedOrigins("*").withSockJS();

        //给好友聊天添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(friendHandler, "/friendWs").addInterceptors(new ChatRoomInterceptor()).setAllowedOrigins("*");
        //添加好友聊天websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(friendHandler, "/friendWs/sockjs").addInterceptors(new ChatRoomInterceptor()).setAllowedOrigins("*").withSockJS();

        //给好友聊天添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(proxyHandler, "/proxyWs").addInterceptors(new ChatRoomInterceptor()).setAllowedOrigins("*");
        //添加好友聊天websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(proxyHandler, "/proxyWs/sockjs").addInterceptors(new ChatRoomInterceptor()).setAllowedOrigins("*").withSockJS();

        //给好友聊天添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(totalHandler, "/totalWs").addInterceptors(new ChatRoomInterceptor()).setAllowedOrigins("*");
        //添加好友聊天websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(totalHandler, "/totalWs/sockjs").addInterceptors(new ChatRoomInterceptor()).setAllowedOrigins("*").withSockJS();

        //给好友聊天添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(adminHandler, "/adminWs").addInterceptors(new ChatRoomInterceptor()).setAllowedOrigins("*");
        //添加好友聊天websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(adminHandler, "/adminWs/sockjs").addInterceptors(new ChatRoomInterceptor()).setAllowedOrigins("*").withSockJS();
    }
}
