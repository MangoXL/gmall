package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.vo.PmsProductCategoryParam;
import com.atguigu.gmall.pms.vo.PmsProductCategoryWithChildrenItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 产品分类 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 查询所有一级分类及子分类
     * @return
     */
    List<PmsProductCategoryWithChildrenItem> getListWithChildren();

    /**
     * 根据id获取商品分类
     * @param id
     * @return
     */
    ProductCategory getItem(Long id);

    /**
     * 根据分类ID修改该分类数据
     * @param id
     * @param productCategoryParam
     * @return
     */
    Integer updateProductCategoryById(Long id, PmsProductCategoryParam productCategoryParam);

    /**
     * 根据分类ID删除该分类
     * @param id
     * @return
     */
    Integer deleteById(Long id);

    /**
     * 分页查询商品分类
     * @param parentId
     * @param pageNum
     * @param pageSize
     * @return
     */
    HashMap<String,Object> getPageList(Long parentId, Integer pageNum, Integer pageSize);

    /**
     * 添加产品分类
     * @param productCategoryParam
     * @return
     */
    Integer create(PmsProductCategoryParam productCategoryParam);
}
