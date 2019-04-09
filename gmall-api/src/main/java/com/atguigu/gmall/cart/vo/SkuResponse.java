package com.atguigu.gmall.cart.vo;

import com.atguigu.gmall.cart.bean.CartItem;
import lombok.Data;

import java.io.Serializable;

@Data
public class SkuResponse implements Serializable{
    private CartItem item;
    private String cartKey;
}
