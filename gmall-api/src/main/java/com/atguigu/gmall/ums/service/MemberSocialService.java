package com.atguigu.gmall.ums.service;

import com.atguigu.gmall.cas.vo.AccessTokenVo;
import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.entity.MemberSocial;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author XiaoLe
 * @since 2019-04-01
 */
public interface MemberSocialService extends IService<MemberSocial> {

    /**
     * 获取用户信息：如果没有登录过，就注册到系统中并返回用户信息，如果登陆过，就直接将用户信息直接返回
     * @param tokenVo
     * @return
     */
    Member getMemberInfo(AccessTokenVo tokenVo);
}
