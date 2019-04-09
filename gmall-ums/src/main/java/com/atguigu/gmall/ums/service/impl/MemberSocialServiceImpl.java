package com.atguigu.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cas.vo.AccessTokenVo;
import com.atguigu.gmall.cas.vo.WeiboAccessTokenVo;
import com.atguigu.gmall.cas.vo.WeiboUserVo;
import com.atguigu.gmall.constant.SocialTypeConstant;
import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.entity.MemberSocial;
import com.atguigu.gmall.ums.mapper.MemberMapper;
import com.atguigu.gmall.ums.mapper.MemberSocialMapper;
import com.atguigu.gmall.ums.service.MemberSocialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author XiaoLe
 * @since 2019-04-01
 *
 * 分布式锁和数据库层Transaction提供的锁定机制都可以；
 * SELECT FOR UPDATE
 *  如果FOR  UPDATE 的字段没有建立索引，会导致全表扫描，导致变成表锁；导致表数据插入等出现问题；
 */
@Service(version = "1.0")
@Component
public class MemberSocialServiceImpl extends ServiceImpl<MemberSocialMapper, MemberSocial> implements MemberSocialService {

    @Autowired
    MemberMapper memberMapper;

    @Transactional //开启事务
    @Override
    public Member getMemberInfo(AccessTokenVo tokenVo) {
        Member member = null;
        if(tokenVo instanceof WeiboAccessTokenVo){
            WeiboAccessTokenVo accessTokenVo = (WeiboAccessTokenVo)tokenVo;
            //查询数据库中是否存在该用户
            member = getBaseMapper().getMemberInfo(accessTokenVo.getUid());
            if(member == null){
                //第一次登录，获取社交用户信息，并注册数据库
                CloseableHttpClient client = HttpClients.createDefault();
                //发送get请求到微博资源服务器
                HttpGet get = new HttpGet("https://api.weibo.com/2/users/show.json?access_token=" + accessTokenVo.getAccess_token() + "&uid=" + accessTokenVo.getUid());
                try {
                    //获取返回数据
                    CloseableHttpResponse execute = client.execute(get);
                    HttpEntity entity = execute.getEntity();
                    //解析数据
                    String s = EntityUtils.toString(entity);
                    WeiboUserVo weiboUserVo = JSON.parseObject(s, WeiboUserVo.class);
                    
                    //将微博用户的数据注册到数据库
                    Member registMember = new Member();
                    registMember.setIcon(weiboUserVo.getProfile_image_url());//保存用户头像
                    registMember.setNickname(weiboUserVo.getName());//保存用户昵称

                    //因为dubbo的超时重试等一些问题，所以要将代码设计的具有幂等性
                    //查询数据库中是否已经存在这个用户的accessToken
                    List<MemberSocial> list = getBaseMapper().selectAccessTokenForUpdate(tokenVo.getAccess_token());
                    if(list != null && list.size() > 0){
                        //已经存在数据，查询该用户信息直接返回
                        MemberSocial social = list.get(0);
                        member = memberMapper.selectById(social.getUserId());
                    }else{
                        //创建一个新用户
                        memberMapper.insert(registMember);
                        //保存用户与微博平台的社交联系
                        MemberSocial memberSocial = new MemberSocial();
                        memberSocial.setUserId(registMember.getId());
                        memberSocial.setUid(weiboUserVo.getId().toString());
                        memberSocial.setType(SocialTypeConstant.WEIBO.getType());
                        memberSocial.setAccessToken(tokenVo.getAccess_token());
                        getBaseMapper().insert(memberSocial);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                return member;
            }
        }
        return member;
    }
}
