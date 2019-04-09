package com.atguigu.gmall.cas.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class SSOController {

    @Reference(version = "1.0")
    MemberService memberService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @PostMapping("/logintosys")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("url") String url,
                        HttpServletRequest request){
        Member member = memberService.login(username,password);
        if(member != null){
            //将用户信息保存在redis
            String memberInfo = JSON.toJSONString(member);
            String token = UUID.randomUUID().toString().replace("-","");
            redisTemplate.opsForValue().set(RedisCacheConstant.USER_INFO_CACHE_KEY + token,memberInfo,RedisCacheConstant.USER_INFO_TIMEOUT, TimeUnit.DAYS);
            return "redirect:" + url + "?token=" + token;
        }else{
            //获取来登录前的页面地址
            String referer = request.getHeader("Referer");
            //登录失败，返回登录前页面
            return "redirect:" + referer + "?token=" + "";
        }
    }
}
