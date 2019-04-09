package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.entity.Coupon;
import com.atguigu.gmall.sms.vo.SmsCouponParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;

/**
 * <p>
 * 优惠卷表 服务类
 * </p>
 *
 * @author XiaoLe
 * @since 2019-03-19
 */
public interface CouponService extends IService<Coupon> {

    /**
     * 添加优惠券
     * @return
     */
    void create(SmsCouponParam smsCouponParam);

    /**
     * 根据优惠券名称和类型分页获取优惠券列表
     * @param name
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    HashMap<String,Object> getList(String name, Integer type, Integer pageNum, Integer pageSize);

    /**
     * 删除优惠券
     * @param id
     * @return
     */
    Integer deleteCouponById(Long id);

    /**
     * 修改优惠券
     * @param id
     * @param smsCouponParam
     * @return
     */
    void updateCouponById(Long id, SmsCouponParam smsCouponParam);

    /**
     * 获取单个优惠券的详细信息
     * @param id
     * @return
     */
    SmsCouponParam getCouponById(Long id);
}
