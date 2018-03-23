package com.kdkj.intelligent.filter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.alibaba.fastjson.JSON;
import com.kdkj.intelligent.entity.Users;
import com.kdkj.intelligent.service.UsersService;
import com.kdkj.intelligent.util.Result;

/**
 * Servlet Filter implementation class VisitFilter
 */
@WebFilter("/VisitFilter")
public class VisitFilter implements Filter {
	
	//@Autowired
	private UsersService usersService;
	
	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		 ServletContext sc = fConfig.getServletContext(); 
	        XmlWebApplicationContext cxt = (XmlWebApplicationContext)WebApplicationContextUtils.getWebApplicationContext(sc);
	        if(cxt != null && cxt.getBean("usersServiceImpl") != null && usersService == null)
	        	usersService=(UsersService)cxt.getBean("usersServiceImpl");   
	}

	
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
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse rep = (HttpServletResponse) response;
		
		rep.setHeader("Access-Control-Allow-Credentials", "true");
		rep.setHeader("Access-Control-Allow-Headers", "X-Requested-With, accept, content-type, xxxx,token");
		rep.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");
		rep.setHeader("Access-Control-Allow-Origin", "http://localhost:8083");

		rep.setCharacterEncoding("UTF-8");
		String s = JSON.toJSONString((Result.error("用户尚未登录或者登录已过期，请重新登录！")));
		HttpSession session = req.getSession();
		String path = req.getServletPath();
		String path1 = "/login";
		String path2 = "/user/addUser";
		String path3 = "/tokenLogin";
		String path5 ="/phoneifexist";
		String path6 ="/validate";
		if (path1.equals(path) || path2.equals(path) || path3.equals(path)|| path6.equals(path)|| path.contains(path5)) {
			chain.doFilter(request, response);
			return;
		}
		if (session.getAttribute("user") == null) {
			if (req.getHeader("token") != null) {
				String token = req.getHeader("token");
				Users user = new Users();
				user.setToken(token);
				List<Users> list = usersService.selectListByUser(user);
				if (list != null && list.size() > 0) {
					Users olduser = list.get(0);
					Date date = olduser.getLastLoginTime();
					Calendar cal = Calendar.getInstance();
					Calendar cal2 = Calendar.getInstance();
					cal2.setTime(new Date());
					cal.setTime(date);
					cal.add(2, 3); // 加三个月后看时间有无过期
					boolean bl = cal.after(cal2);
					if (bl) {
						olduser.setLastLoginTime(new Date());
						usersService.updateByPrimaryKey(olduser);
						req.getSession().setAttribute("user", olduser);
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
		return;
		// pass the request along the filter chain
	}


}
