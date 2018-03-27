package com.kdkj.intelligent.controller;

import com.kdkj.intelligent.util.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;

/**
 *
* @ClassName: exceptionController    [参考:http://blog.csdn.net/github_38659047/article/details/72930369]
* @Description: 处理所有异常本记录日志      [有人会问我，为什么要这么去做？因为我愿意^_^。其实吧！api为了给用户一个很好的体验，即便是出现异常，也要给用于说明怎么回事]
* @author Love丶TG
* @date 2017年11月30日 下午6:37:53
*
 */

@ControllerAdvice
public class ExceptionAll {

	private static final  Logger logger = LogManager.getLogger(ExceptionAll.class);

	/**
	 * 处理文件过大异常
	 * @param request
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = MultipartException.class)
	@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
	@ResponseBody
	public Result multipartExceptionErrorHandler(HttpServletRequest request, Exception e) {
		logger.error(e.getMessage());
		return Result.error("文件过大或类型不匹配");
	}
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public Result processUnauthenticatedException(NativeWebRequest request, Exception e) {
		logger.error(e.getMessage());
		return Result.error("你没有对应权限或内部错误请联系管理员"); //返回一个逻辑视图名
	}
    
}
