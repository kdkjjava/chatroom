package com.kdkj.intelligent.interceptor;

import com.kdkj.intelligent.util.Variables;
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
public class ChatRoomInterceptor implements HandshakeInterceptor {


    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
            HttpServletResponse response = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();
            String msgFrom = request.getParameter(Variables.MSGFROM);
            String groupId = request.getParameter(Variables.GROUPID);
            if (msgFrom == null || groupId == null) {
                response.getWriter().write("{\"code\":\"500\",\"msg\":\"请求参数不完整\"}");
                return false;
            } else {
                map.put(Variables.MSGFROM, request.getParameter(Variables.MSGFROM));
                map.put(Variables.GROUPID, request.getParameter(Variables.GROUPID));
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
        //该方法用握手成功后
    }
}
