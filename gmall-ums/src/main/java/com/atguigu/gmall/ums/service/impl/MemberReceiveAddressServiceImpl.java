package com.atguigu.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.entity.MemberReceiveAddress;
import com.atguigu.gmall.ums.mapper.MemberReceiveAddressMapper;
import com.atguigu.gmall.ums.service.MemberReceiveAddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 会员收货地址表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service(version = "1.0")
@Component
public class MemberReceiveAddressServiceImpl extends ServiceImpl<MemberReceiveAddressMapper, MemberReceiveAddress> implements MemberReceiveAddressService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public List<MemberReceiveAddress> getUserAddress(String token) {
        String memberInfo = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberInfo, Member.class);
        if(member != null){
            return getBaseMapper().selectList(new QueryWrapper<MemberReceiveAddress>().eq("member_id",member.getId()));
        }
        return null;
    }

    @Override
    public MemberReceiveAddress getAddressById(Long addressId) {
        return getBaseMapper().selectById(addressId);
    }
}
