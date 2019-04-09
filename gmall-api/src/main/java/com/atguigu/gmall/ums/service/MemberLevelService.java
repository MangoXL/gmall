package com.atguigu.gmall.ums.service;

import com.atguigu.gmall.ums.entity.MemberLevel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 会员等级表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface MemberLevelService extends IService<MemberLevel> {

    /**
     * 查询所有会员等级
     * @return
     */
    List<MemberLevel> getMemberLevel(Integer defaultStatus);
}
