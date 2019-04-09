package com.atguigu.gmall.item.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ProductAllInfo;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Service(version = "1.0")
@Component
public class ItemServiceImpl implements ItemService {

    @Reference(version = "1.0")
    ProductService productService;

    @Reference(version = "1.0")
    SkuStockService skuStockService;

    @Override
    public ProductAllInfo getProductInfo(Long skuId) {
        //创建返回页面的VO数据
        ProductAllInfo productAllInfo = new ProductAllInfo();
        //查询当前商品sku信息并封装
        SkuStock skuStock = skuStockService.getById(skuId);
        productAllInfo.setSkuStock(skuStock);

        //查询当前商品详情信息并封装
        Product product = productService.getProductByIdFromCache(skuStock.getProductId());
        productAllInfo.setProduct(product);

        //查询当前商品的所有sku组合
        List<SkuStock> skuStocks = skuStockService.getAllSkuInfoByProductId(skuStock.getProductId());
        productAllInfo.setSkuStocks(skuStocks);

        //查询当前商品所有基本属性
        List<EsProductAttributeValue> saleInfo = productService.getProductBaseAttr(skuStock.getProductId());
        productAllInfo.setSaleInfo(saleInfo);
        //查询当前商品所有销售属性
        List<EsProductAttributeValue> baseInfo = productService.getProductSaleAttr(skuStock.getProductId());
        productAllInfo.setBaseInfo(baseInfo);

        return productAllInfo;
    }
}
