package com.lll.blog.handler;

import com.alibaba.fastjson.JSON;
import com.lll.blog.dao.pojo.SysUser;
import com.lll.blog.service.LoginService;
import com.lll.blog.utils.UserThreadLocal;
import com.lll.blog.vo.ErrorCode;
import com.lll.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在执行controller方法（handler）之前进行执行
        /**
         * 1、需要判断接口是否是HandlerMehtood(controller方法
         * 2、判断token是否为空，空=未登录
         * 3、不空登录验证logineservice  checktoken
         * 4、认证成功放行
         */
        if (!(handler instanceof HandlerMethod)){ //不是HandlerMehtood(controller方法，放行
            //hander有可能是requestresourcehandler  springboot 程序 访问口静态资源
            return true;
        }

        String token = request.getHeader("Authorization");  //token在Heade
        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        if (token == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");  //让前端知道返回是json类型
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        SysUser sysUser =loginService.checkToken(token);
        if (sysUser == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        //是登录状态，放行
        UserThreadLocal.put(sysUser);
        return true;
    }
    //释放线程
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}