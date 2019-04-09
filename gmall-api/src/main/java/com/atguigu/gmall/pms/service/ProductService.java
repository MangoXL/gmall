package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.atguigu.gmall.pms.vo.PmsProductQueryParam;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品列表
     * @param pageSize
     * @param pageNum
     * @return
     */
    HashMap<String,Object> getPageList(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum);


    /**
     * 创建商品
     * @param productParam
     * @return
     */
    void create(PmsProductParam productParam);

    /**
     * 批量上下架
     * @param ids
     * @param publishStatus
     */
    void updatePublishStatus(List<Long> ids, Integer publishStatus);
    /**
     * 根据商品ID查询当前商品所有基本属性
     * @param productId
     * @return
     */
    List<EsProductAttributeValue> getProductBaseAttr(Long productId);

    /**
     * 根据商品ID查询当前商品所有销售属性
     * @param productId
     * @return
     */
    List<EsProductAttributeValue> getProductSaleAttr(Long productId);

    /**
     * 根据商品名称或货号模糊查询
     * @param keyword
     * @return
     */
    List<Product> getListByKeyword(String keyword);

    /**
     * 批量修改删除状态
     * @param ids
     * @param deleteStatus
     * @return
     */
    void updateDeleteStatus(List<Long> ids, Integer deleteStatus);

    /**
     * 根据商品ID上查询商品详情
     * @param productId
     * @return
     */
    Product getProductByIdFromCache(Long productId);
}
