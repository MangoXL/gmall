package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * sku的库存 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface SkuStockService extends IService<SkuStock> {

    /**
     * 根据商品ID查询当前商品所有sku组合信息
     * @param productId
     * @return
     */
    List<SkuStock> getAllSkuInfoByProductId(Long productId);

    /**
     * 根据商品SkuID查询该信息
     * @param skuId
     * @return
     */
    SkuStock getSkuInfo(Long skuId);

    /**
     * 获取商品价格
     * @param skuId
     * @return
     */
    BigDecimal getPriceById(String skuId);
}
