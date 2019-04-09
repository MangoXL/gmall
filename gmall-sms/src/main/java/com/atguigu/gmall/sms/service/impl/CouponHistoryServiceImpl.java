package com.atguigu.gmall.sms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.sms.entity.CouponHistory;
import com.atguigu.gmall.sms.mapper.CouponHistoryMapper;
import com.atguigu.gmall.sms.service.CouponHistoryService;
import com.atguigu.gmall.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * <p>
 * 优惠券使用、领取历史表 服务实现类
 * </p>
 *
 * @author XiaoLe
 * @since 2019-03-19
 */
@Service(version = "1.0")
@Component
public class CouponHistoryServiceImpl extends ServiceImpl<CouponHistoryMapper, CouponHistory> implements CouponHistoryService {

    @Override
    public HashMap<String, Object> getCouponHistoryInfo(Long couponId, Integer useStatus, String orderSn, Integer pageNum, Integer pageSize) {
        QueryWrapper<CouponHistory> queryWrapper = new QueryWrapper<>();
        if(couponId != null){
            //根据优惠券ID
            queryWrapper.eq("coupon_id",couponId);
        }
        if(useStatus != null){
            //根据优惠券使用状态
            queryWrapper.eq("use_status",useStatus);
        }
        if(!StringUtils.isEmpty(orderSn)){
            //根据订单编号
            queryWrapper.like("order_id",orderSn);
        }
        Page<CouponHistory> page = new Page<>(pageNum,pageSize);
        getBaseMapper().selectPage(page,queryWrapper);
        return PageUtils.getPage(page);
    }
}
