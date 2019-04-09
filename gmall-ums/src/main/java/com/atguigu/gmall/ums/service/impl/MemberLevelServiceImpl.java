package com.atguigu.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.ums.entity.MemberLevel;
import com.atguigu.gmall.ums.mapper.MemberLevelMapper;
import com.atguigu.gmall.ums.service.MemberLevelService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 会员等级表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelMapper, MemberLevel> implements MemberLevelService {

    @Override
    public List<MemberLevel> getMemberLevel(Integer defaultStatus) {
        //根据是否为普通会员查询所有会员信息
        QueryWrapper<MemberLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("default_status",defaultStatus);
        return getBaseMapper().selectList(queryWrapper);
    }
}
