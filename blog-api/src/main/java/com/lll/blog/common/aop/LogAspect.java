package com.lll.blog.common.aop;

import com.alibaba.fastjson.JSON;
import com.lll.blog.utils.HttpContextUtils;
import com.lll.blog.utils.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 日志切面
 * 切面定义了通知与切点之间的关系
 * 切点  连接点  增强  注入
 */
@Aspect   //标志此类是一个切面
@Component
@Slf4j
public class LogAspect {

    //切点
    @Pointcut("@annotation(com.lll.blog.common.aop.LogAnnotation)")
    public void logPointCut() {
    }

    //环绕注入 对方法增强  在logPointCut()方法执行之前后执行下面的东西
    @Around("logPointCut()")  //填入切入点 ===》logPointCut()
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        recordLog(point, time);
        return result;
    }

    /**
     * 记录日志方法
     * 1、记录切点的方法名
     * 2、记录切点的执行时间
     * 3、记录注解标记的切点（模块名+操作名字）
     * 3、记录方法请求的IP地址
     *
     * @param joinPoint
     * @param time
     */
    private void recordLog(ProceedingJoinPoint joinPoint, long time) {
        //获取签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法
        Method method = signature.getMethod();

        // 获取注解  目的获取其参数
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        log.info("=====================log start================================");
        log.info("module:{}",logAnnotation.module());
        log.info("operation:{}",logAnnotation.operator());


        String className = joinPoint.getTarget().getClass().getName(); //请求的方法所在类名
        String methodName = signature.getName();  //请求的方法名
        log.info("request method:{}",className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        String params = JSON.toJSONString(args[0]);
        log.info("params:{}",params);

        //获取request 设置IP地址
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.info("ip:{}", IPUtils.getIpAddr(request));


        log.info("excute time : {} ms",time);
        log.info("=====================log end================================");
    }

}