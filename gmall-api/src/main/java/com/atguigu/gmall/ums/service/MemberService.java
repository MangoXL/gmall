package com.atguigu.gmall.ums.service;

import com.atguigu.gmall.ums.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface MemberService extends IService<Member> {

    /**
     * 根据用户名和密码查询该用户是否存在
     * @param username
     * @param password
     * @return
     */
    Member login(String username, String password);
}
