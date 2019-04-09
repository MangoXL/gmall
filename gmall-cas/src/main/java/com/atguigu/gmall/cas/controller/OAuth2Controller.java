package com.atguigu.gmall.cas.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cas.config.WeiboOAuthConfig;
import com.atguigu.gmall.cas.vo.WeiboAccessTokenVo;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.service.MemberSocialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@CrossOrigin
@Controller
@Api(tags = "OAuth2Controller", description = "社交登陆管理")
public class OAuth2Controller {

    @Autowired
    WeiboOAuthConfig config;

    RestTemplate restTemplate = new RestTemplate();

    @Reference(version = "1.0")
    MemberSocialService memberSocialService;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 用户登录授权，如果是第一次就是注册，之后就是登录
     * @param authType
     * @return
     */
    @GetMapping("/register/authorization")
    public String registerAuthorization(
                                @RequestParam("authType") String authType,
                                @RequestParam("url") String url,
                                HttpSession session){
        session.setAttribute("url",url);
        System.out.println(url);
        //判断社交登陆平台类型
        if("weibo".equals(authType)){
            return "redirect:" + config.getAuthPage();
        }
        //重定向到授权页面
        return "redirect:" + config.getAuthPage();
    }

    /**
     * 用户授权后获取一个授权码code
     * @param code
     * @return
     */
    @ApiOperation("用户授权后获取授权码code")
    @GetMapping("/auth/success")
    public String codeGetToken(@RequestParam("code") String code,
                                        HttpSession session){
        System.out.println("获取到授权码：" + code);
        //换取accessToken地址
        String authPage = config.getAccessTokenPage() + code;
        //根据授权码获取accessToken
        WeiboAccessTokenVo tokenVo = restTemplate.postForObject(authPage, null, WeiboAccessTokenVo.class);
        //将accessToken转换为UUID，存入到redis
        //判断该用户之前是否登陆过，如果没有，就就注册到系统中，如果登陆过，就将用户信息直接返回
        //通过accessToken获取的用户信息，作为初始化用户信息存入数据库，以后直接返回
        //数据库也要记录accessToken，如果过期，重新引导授权，如果没有，继续使用
        Member member = memberSocialService.getMemberInfo(tokenVo);
        String token = UUID.randomUUID().toString();
        String memberInfo = JSON.toJSONString(member);
        redisTemplate.opsForValue().set(RedisCacheConstant.USER_INFO_CACHE_KEY + token,memberInfo);
        String url = (String) session.getAttribute("url");
        return "redirect:" + url + "?token=" + token;
    }

    /**
     * 登陆成功的用户以后的任何请求都带上token
     * @param token
     * @return
     */
    @ResponseBody
    @GetMapping("/userinfo")
    public Member getUserInfo(String token){
        String memberInfo = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberInfo, Member.class);
        return member;
    }
}
