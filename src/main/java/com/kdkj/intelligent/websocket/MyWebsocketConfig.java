package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.interceptor.ChatRoomInterceptor;
import com.kdkj.intelligent.interceptor.FriendInterceptor;
import com.kdkj.intelligent.interceptor.ProxyInterceptor;
import com.kdkj.intelligent.interceptor.TotalInterceptor;
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
    private ProxyInterceptor proxyInterceptor;
    @Resource
    private ChatRoomInterceptor chatRoomInterceptor;
    @Resource
    private TotalInterceptor totalInterceptor;
    @Resource
    private FriendInterceptor friendInterceptor;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //给群聊添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(grouphandler, "/groupWs").addInterceptors(chatRoomInterceptor).setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(grouphandler, "/groupWs/sockjs").addInterceptors(chatRoomInterceptor).setAllowedOrigins("*").withSockJS();

        //给好友聊天添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(friendHandler, "/friendWs").addInterceptors(friendInterceptor).setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(friendHandler, "/friendWs/sockjs").addInterceptors(friendInterceptor).setAllowedOrigins("*").withSockJS();

        //给proxy聊天添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(proxyHandler, "/proxyWs").addInterceptors(proxyInterceptor).setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(proxyHandler, "/proxyWs/sockjs").addInterceptors(proxyInterceptor).setAllowedOrigins("*").withSockJS();

        //给总的会话添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(totalHandler, "/totalWs").addInterceptors(totalInterceptor).setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(totalHandler, "/totalWs/sockjs").addInterceptors(totalInterceptor).setAllowedOrigins("*").withSockJS();

    }
}
