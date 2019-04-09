package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductAllInfo implements Serializable{
    private Product product;//当前商品详情信息
    private SkuStock skuStock;//当前商品sku信息
    private List<SkuStock> skuStocks;//当前商品所有sku组合以及库存等信息
    private List<EsProductAttributeValue> baseInfo;//当前商品基本属性
    private List<EsProductAttributeValue> saleInfo;//当前商品销售属性
}
