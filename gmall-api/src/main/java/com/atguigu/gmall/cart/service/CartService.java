package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.bean.Cart;
import com.atguigu.gmall.cart.bean.CartItem;
import com.atguigu.gmall.cart.vo.SkuResponse;

import java.util.List;

public interface CartService {
    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @param cartKey
     * @return
     */
    SkuResponse addToCart(Long skuId, Integer num, String cartKey);

    /**
     * 修改购物车商品数量
     * @param skuId
     * @param num
     * @param cartKey
     * @return
     */
    boolean updateToCart(Long skuId, Integer num, String cartKey);

    /**
     * 删除购物车商品
     * @param skuId
     * @param cartKey
     * @return
     */
    boolean deleteToCart(Long skuId, String cartKey);

    /**
     * 修改购物车商品选中状态
     * @param skuId
     * @param flag
     * @param cartKey
     * @return
     */
    boolean checkToCart(Long skuId, Integer flag, String cartKey);

    /**
     * 获取购物车列表
     * @param cartKey
     * @return
     */
    Cart list(String cartKey);

    /**
     * 将购物车中已选中的购物商品项去往结算确认页面
     * @param token
     * @return
     */
    List<CartItem> cartItemConfirm(String token);
}
