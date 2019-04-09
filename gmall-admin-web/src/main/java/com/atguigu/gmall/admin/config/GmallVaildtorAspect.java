package com.atguigu.gmall.admin.config;

import com.atguigu.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Slf4j
@Aspect //说明这是一个切面
@Component
public class GmallVaildtorAspect {

    //表达式：任意修饰符和返回值，com.atguigu.gmall下多层目录下任意controller包下任意多层目录下任意类下任意方法下任意参数
    @Around("execution(* com.atguigu.gmall.admin..controller..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("切面开始进行校验！");
        //获取参数
        Object[] args = joinPoint.getArgs();
            //前置校验
            for (Object arg : args) {
                //遍历参数，只获取BindingResult类型 参数
                if(arg instanceof BindingResult){
                    //获取校验错误数
                    int count = ((BindingResult) arg).getErrorCount();
                    //判断是否存在错误
                    if(count > 0){
                        log.error("校验发生错误！");
                        return new CommonResult().validateFailed((BindingResult) arg);
                    }
                }
            }
            //方法放行
            Object proceed = joinPoint.proceed(args);

        return proceed;
    }
}
