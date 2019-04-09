package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.cart.bean.CartItem;
import com.atguigu.gmall.ums.entity.MemberReceiveAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderConfirmVo implements Serializable {
    private List<CartItem> cartItems;
    private List<MemberReceiveAddress> addresses;
    private String tradeToken;
}
