package com.atguigu.gmall.service;

import com.atguigu.gmall.to.es.ESProduct;
import com.atguigu.gmall.to.es.SearchParam;
import com.atguigu.gmall.to.es.SearchResponse;

import java.io.IOException;

public interface SearchService {

    /**
     * 保存商品信息到ES
     * @param esProduct
     * @return
     */
    boolean saveProductInfoToES(ESProduct esProduct);

    /**
     * 检索商品
     * @param searchParam
     * @return
     */
    SearchResponse search(SearchParam searchParam) throws IOException;
}
