package com.atguigu.gmall.item.service;

import com.atguigu.gmall.item.vo.ProductAllInfo;

public interface ItemService {
    /**
     * 获取商品详情页面数据
     * @param skuId
     * @return
     */
    ProductAllInfo getProductInfo(Long skuId);
}
