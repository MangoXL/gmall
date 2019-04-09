package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import com.atguigu.gmall.pms.vo.PmsProductAttributeCategoryItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 产品属性分类表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductAttributeCategoryService extends IService<ProductAttributeCategory> {

    /**
     * 分页获取所有商品属性分类
     * @param pageNum
     * @param pageSize
     * @return
     */
    HashMap<String,Object> getPageList(Integer pageNum, Integer pageSize);

    /**
     * 修改商品属性分类
     * @param id
     * @param name
     * @return
     */
    Integer updateByIdAndName(Long id, String name);

    /**
     * 添加商品属性分类
     * @param name
     * @return
     */
    Integer create(String name);

    /**
     * 删除单个商品属性分类
     * @param id
     * @return
     */
    Integer deleteSingle(Long id);

    /**
     * 获取所有商品属性分类及其下属性
     * @return
     */
    List<PmsProductAttributeCategoryItem> getListWithAttr();
}
