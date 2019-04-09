package com.atguigu.gmall.constant;

public class RedisCacheConstant {
    public static final String PRODUCT_CATEGORY_CACHE_KEY = "gmall:product:category:cache";

    public static final String PRODUCT_INFO_CACHE_KEY = "gulishop:product:info:";
    public static final String USER_INFO_CACHE_KEY = "gulishop:user:info:";
    public static final Long USER_INFO_TIMEOUT = 3L;
    public static final String CART_TEMP = "gmall:cart:temp:";
    public static final String USER_CART = "gmall:user:cart:";
    public static final String TRADE_TOKEN = "gmall:trade:temptoken:";
    public static final Long TRADE_TOKEN_TIMEOUT = 5L;
}
