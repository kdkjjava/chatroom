package com.kdkj.intelligent.websocket;

import com.kdkj.intelligent.interceptor.ChatRoomInterceptor;
import com.kdkj.intelligent.websocket.MyWebSocketHandler;
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
    private MyWebSocketHandler handler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(handler, "/ws").addInterceptors(new ChatRoomInterceptor());

        //添加websocket处理器，添加握手拦截器
        webSocketHandlerRegistry.addHandler(handler, "/ws/sockjs").addInterceptors(new ChatRoomInterceptor()).withSockJS();
    }
}
