package com.atguigu.gmall.cart.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 购物车数据
 */
@Setter
public class Cart implements Serializable{
    @Getter
    private List<CartItem> cartItems = new ArrayList<>();//所有商品购物项
    private Integer total;//商品总数
    private BigDecimal totalPirce;//商品总价

    public Integer getTotal() {
        if(cartItems.size() > 0){
            AtomicReference<Integer> total = new AtomicReference<>(0);
            cartItems.forEach(item -> {
                total.set(total.get() + item.getNum());
            });
            return total.get();
        }else{
            return total;
        }
    }

    public BigDecimal getTotalPirce() {
        if(cartItems.size() > 0){
            AtomicReference<BigDecimal> talPirce = new AtomicReference<>(new BigDecimal(0.0));
            cartItems.forEach(item -> {
                BigDecimal price = item.getPrice();
                BigDecimal multiply = price.multiply(new BigDecimal(item.getNum().toString()));
                talPirce.set(multiply.add(talPirce.get()));
            });
            return talPirce.get();
        }else{
            return totalPirce;
        }
    }
}
