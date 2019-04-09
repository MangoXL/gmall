package com.atguigu.gmall.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderSubmitVo implements Serializable{
    private String token;//用户令牌
    private String tradeToke;//交易令牌
    private BigDecimal price;//商品价格
    private Long addressId;//用户选择的收货地址ID
    private String message;//备注信息
}
