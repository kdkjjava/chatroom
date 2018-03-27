package com.kdkj.intelligent.interceptor;

import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Variables;
import org.springframework.beans.factory.annotation.Autowired;
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
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/25 17:58
 * @Description:
 **/
@Component
public class ProxyInterceptor implements HandshakeInterceptor {

    @Autowired
    private UsersService usersService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
            HttpServletResponse response = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();
            String msgFrom = request.getHeader(Variables.MSGFROM);
            if (msgFrom != null) {
                if (usersService.hasExpired(msgFrom)) {
                    response.getWriter().write("{\"code\":\"500\",\"msg\":\"您的用户已过期，请联系管理员\"}");
                    return false;
                }
                map.put(Variables.MSGFROM, msgFrom);
            } else {
                response.getWriter().write("{\"code\":\"500\",\"msg\":\"请求参数不完整\"}");
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        //握手后执行的方法
    }
}
