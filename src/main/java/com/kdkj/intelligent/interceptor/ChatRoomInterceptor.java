package com.kdkj.intelligent.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Date: 2017-12-26
 * @Time: 11:03
 */

@Component
public class ChatRoomInterceptor implements HandshakeInterceptor{

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
            HttpServletResponse response =((ServletServerHttpResponse)serverHttpResponse).getServletResponse();
            if (request.getParameter("msgFrom")==null)
                response.getWriter().write("{\"errorCode\":\"请求参数不完整\"}");
            else
                map.put("msgFrom",request.getParameter("msgFrom"));
            if (request.getParameter("groupId")!=null)
                map.put("groupId",request.getParameter("groupId"));
            if (request.getParameter("msgTo")!=null)
                map.put("msgTo",request.getParameter("msgTo"));
        }


        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        //该方法用握手成功后
    }
}
