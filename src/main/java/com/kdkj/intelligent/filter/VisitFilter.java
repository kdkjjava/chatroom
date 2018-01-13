package com.kdkj.intelligent.filter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Result;

/**
 * Servlet Filter implementation class VisitFilter
 */
@WebFilter("/VisitFilter")
public class VisitFilter implements Filter {

	@Autowired
	UsersService usersService;

	/**
	 * Default constructor.
	 */
	public VisitFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		String s = JSON.toJSONString((Result.error("用户尚未登录或者登录已过期，请重新登录！")));
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse rep = (HttpServletResponse) response;
		rep.setCharacterEncoding("UTF-8");
		HttpSession session = req.getSession();
		String path = req.getServletPath();
		String path1 = "/login";
		String path2 = "/user/addUser";
		String path3 = "/tokenLogin";
		
		if (path1.equals(path) || path2.equals(path) || path3.equals(path)) {
			chain.doFilter(request, response);
			return;
		}
		if (session.getAttribute("user") == null) {
			if (req.getAttribute("token") != null) {
				String token = (String) req.getAttribute("token");
				Users user = new Users();
				user.setToken(token);
				List<Users> list = usersService.selectListByUser(user);
				if (list != null && list.size() > 0) {
					Users olduser = list.get(0);
					Date date = olduser.getLastLoginTime();
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.add(2, 3); // 加三个月后看时间有无过期
					boolean bl = cal.after(new Date());
					if (bl) {
						olduser.setLastLoginTime(new Date());
						usersService.updateByPrimaryKey(olduser);
						chain.doFilter(request, response);
						return;
					} else {
						rep.getWriter().write(s);
						return;
					}

				} else {
					rep.getWriter().write(s);
					return;
				}
			}
			rep.getWriter().write(s);
			return;
		}
		chain.doFilter(request, response);
		// pass the request along the filter chain
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
