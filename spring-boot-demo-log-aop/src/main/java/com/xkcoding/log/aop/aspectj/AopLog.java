package com.xkcoding.log.aop.aspectj;

import cn.hutool.json.JSONUtil;
import com.xkcoding.log.aop.domain.User;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 使用 aop 切面记录请求日志信息
 * </p>
 *
 * @package: com.xkcoding.log.aop.aspectj
 * @description: 使用 aop 切面记录请求日志信息
 * @author: yangkai.shen
 * @date: Created in 2018/10/1 10:05 PM
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Aspect
@Component
@Slf4j
public class AopLog {
    private static final String START_TIME = "request-start";

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 切入点
     */
//	@Pointcut("execution(public * com.xkcoding.log.aop.controller.*Controller.*(..))")
//	public void log() {
//
//	}
    @Pointcut("@annotation(com.xkcoding.log.aop.annotation.Log)")
    public void log() {
    }

    /**
     * 前置操作
     *
     * @param point 切入点
     */
    @Before("log()")
    public void beforeLog(JoinPoint point) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

        log.info("【请求 URL】：{}", request.getRequestURL());
        log.info("【请求 IP】：{}", request.getRemoteAddr());
        log.info("【请求类名】：{}，【请求方法名】：{}", point.getSignature().getDeclaringTypeName(), point.getSignature().getName());

        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("【请求参数】：{}，", JSONUtil.toJsonStr(parameterMap));

        Object[] args = point.getArgs();
        Class<?>[] classes = new Class[args.length];
        for (int k = 0; k < args.length; k++) {
            // 对于接受参数中含有MultipartFile，ServletRequest，ServletResponse类型的特殊处理，我这里是直接返回了null。（如果不对这三种类型判断，会报异常）
            if (args[k] instanceof User) {
                log.info("【请求参数user】：{}，", JSONUtil.toJsonStr(args[k]));

            }
            if (!args[k].getClass().isPrimitive()) {
                // 当方法参数是基础类型，但是获取到的是封装类型的就需要转化成基础类型
//                String result = args[k].getClass().getName();
//                Class s = map.get(result);

                // 当方法参数是封装类型
                Class s = args[k].getClass();

                classes[k] = s == null ? args[k].getClass() : s;
            }

            Long start = System.currentTimeMillis();
            request.setAttribute(START_TIME, start);
        }
    }

    /**
     * 环绕操作
     *
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around("log()")
    public Object aroundLog(ProceedingJoinPoint joinPoint) throws Throwable {


        Method method = getMethod(joinPoint);

        com.xkcoding.log.aop.annotation.Log logger =   method.getAnnotation(com.xkcoding.log.aop.annotation.Log.class);

        Object[] parameterValues = joinPoint.getArgs();
        // SpEL表达式的上下文
        EvaluationContext context = new StandardEvaluationContext();
        // Parameter[] parameters = method.getParameters();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameters = signature.getParameterNames();
        for (int i = 0; i < parameters.length; i++) {
            // 方法的入参全部set进SpEL的上下文
            // String name = parameters[i].getName();
            String name = parameters[i];
            Object value = parameterValues[i];
            context.setVariable(name, value);
        }

        String userId = "000";
        Object value = parser.parseExpression(logger.value()).getValue(context);
        if (value != null) {
            userId = value.toString();
            // 这里即可获取方法入参的用户Id
            System.out.println(userId);
        }


        log.info("【****************id*********************】：{}", userId);
        Object result = joinPoint.proceed();
        log.info("【返回值】：{}", JSONUtil.toJsonStr(result));
        return result;
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(), method.getParameterTypes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return method;
    }


//    作者：0慕念0
//    链接：https://www.jianshu.com/p/7ae84db164c9
//    来源：简书
//    著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

    /**
     * 后置操作
     */
    @AfterReturning("log()")
    public void afterReturning() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

        Long start = (Long) request.getAttribute(START_TIME);
        Long end = System.currentTimeMillis();
        log.info("【请求耗时】：{}毫秒", end - start);

        String header = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(header);
        log.info("【浏览器类型】：{}，【操作系统】：{}，【原始User-Agent】：{}", userAgent.getBrowser().toString(), userAgent.getOperatingSystem().toString(), header);
    }
}
