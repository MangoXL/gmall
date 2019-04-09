package com.atguigu.gmall.order.service.service;

import com.atguigu.gmall.order.vo.OrderResponseVo;
import com.atguigu.gmall.order.vo.OrderSubmitVo;

public interface OrderService {
    /**
     * 颁发临时令牌，防止用户重复提交
     * @return
     */
    String getTradeToken();

    /**
     * 创建订单
     * @param orderSubmitVo
     * @return
     */
    OrderResponseVo createOrder(OrderSubmitVo orderSubmitVo);
}
