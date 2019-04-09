package com.atguigu.gmall.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseVo implements Serializable{
    private String tips;//提示信息
    private Integer code;//状态码
    private String out_trade_no;//订单号
    private String total_amount;//付款金额
    private String subject;//订单名称
    private String body;//订单描述
}
