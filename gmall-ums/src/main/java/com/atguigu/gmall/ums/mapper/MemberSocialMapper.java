package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.entity.MemberSocial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author XiaoLe
 * @since 2019-04-01
 */
public interface MemberSocialMapper extends BaseMapper<MemberSocial> {

    /**
     * 根据社交用户的ID，查询数据库中是否存在该用户
     * @param uid
     * @return
     */
    Member getMemberInfo(String uid);

    /**
     * 事务模式下阻塞查询
     * @param access_token
     * @return
     */
    List<MemberSocial> selectAccessTokenForUpdate(String access_token);
}
