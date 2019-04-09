package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.vo.PmsProductAttributeParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 商品属性参数表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductAttributeService extends IService<ProductAttribute> {

    /**
     * 根据分类查询属性列表或参数列表
     * @param cid
     * @param type
     * @param pageSize
     * @param pageNum
     * @return
     */
    HashMap<String,Object> getPageList(Long cid, Integer type, Integer pageSize, Integer pageNum);

    /**
     * 查询单个商品属性
     * @param id
     * @return
     */
    PmsProductAttributeParam getItem(Long id);

    /**
     * 修改商品属性信息
     * @param id
     * @param productAttributeParam
     * @return
     */
    Integer updateProductAttribute(Long id, PmsProductAttributeParam productAttributeParam);

    /**
     * 添加商品属性信息
     * @param productAttributeParam
     * @return
     */
    Integer create(PmsProductAttributeParam productAttributeParam);

    /**
     * 批量删除商品属性
     * @param ids
     * @return
     */
    Integer deleteBatch(List<Long> ids);

    /**
     * 根据商品分类的id获取商品属性及属性分类
     * @return
     */
    List<PmsProductAttributeParam> getAttrInfo(Long productCategoryId);
}
