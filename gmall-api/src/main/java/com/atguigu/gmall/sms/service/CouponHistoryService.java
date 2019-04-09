package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.entity.CouponHistory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;

/**
 * <p>
 * 优惠券使用、领取历史表 服务类
 * </p>
 *
 * @author XiaoLe
 * @since 2019-03-19
 */
public interface CouponHistoryService extends IService<CouponHistory> {

    /**
     * 根据优惠券id，使用状态，订单编号分页获取领取记录
     * @param couponId
     * @param useStatus
     * @param orderSn
     * @param pageNum
     * @param pageSize
     * @return
     */
    HashMap<String,Object> getCouponHistoryInfo(Long couponId, Integer useStatus, String orderSn, Integer pageNum, Integer pageSize);
}
