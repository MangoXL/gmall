package com.atguigu.gmall.ums.service;

import com.atguigu.gmall.ums.entity.MemberReceiveAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 会员收货地址表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddress> {

    /**
     * 获取用户可选的收货地址
     * @return
     */
    List<MemberReceiveAddress> getUserAddress(String token);

    /**
     * 根据地址id获取地址详情
     * @param addressId
     * @return
     */
    MemberReceiveAddress getAddressById(Long addressId);
}
