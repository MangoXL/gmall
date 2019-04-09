package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import com.atguigu.gmall.pms.mapper.ProductAttributeCategoryMapper;
import com.atguigu.gmall.pms.mapper.ProductAttributeMapper;
import com.atguigu.gmall.pms.service.ProductAttributeCategoryService;
import com.atguigu.gmall.pms.vo.PmsProductAttributeCategoryItem;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 产品属性分类表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductAttributeCategoryServiceImpl extends ServiceImpl<ProductAttributeCategoryMapper, ProductAttributeCategory> implements ProductAttributeCategoryService {

    @Autowired
    ProductAttributeMapper productAttributeMapper;

    @Override
    public HashMap<String, Object> getPageList(Integer pageNum, Integer pageSize) {
        Page<ProductAttributeCategory> page = new Page<>(pageNum,pageSize);
        IPage<ProductAttributeCategory> pageInfo = getBaseMapper().selectPage(page, null);

        //创建页面显示的数据集合
        HashMap<String, Object> map = new HashMap<>();
        //填充数据
        map.put("total",pageInfo.getTotal());//总记录数
        map.put("totalPage",pageInfo.getPages()); //总页数
        map.put("pageSize",pageSize);//每页显示记录数
        map.put("list",pageInfo.getRecords());//数据列表
        map.put("pageNum",pageNum);//当前页

        //返回数据
        return map;
    }

    @Override
    public Integer updateByIdAndName(Long id, String name) {
        ProductAttributeCategory attributeCategory = new ProductAttributeCategory();
        attributeCategory.setId(id);
        attributeCategory.setName(name);
        return getBaseMapper().updateById(attributeCategory);
    }

    @Override
    public Integer create(String name) {
        ProductAttributeCategory attributeCategory = new ProductAttributeCategory();
        attributeCategory.setName(name);
        return getBaseMapper().insert(attributeCategory);
    }

    @Override
    public Integer deleteSingle(Long id) {
        return getBaseMapper().deleteById(id);
    }

    @Override
    public List<PmsProductAttributeCategoryItem> getListWithAttr() {
        ArrayList<PmsProductAttributeCategoryItem> items = new ArrayList<>();
        List<ProductAttributeCategory> productAttributeCategories = getBaseMapper().selectList(null);
        productAttributeCategories.forEach(productAttributeCategory -> {
            PmsProductAttributeCategoryItem pmsProductAttributeCategoryItem = new PmsProductAttributeCategoryItem();
            BeanUtils.copyProperties(productAttributeCategory,pmsProductAttributeCategoryItem);
            List<ProductAttribute> productAttributeList = productAttributeMapper.selectList(
                    new QueryWrapper<ProductAttribute>()
                            .eq("product_attribute_category_id", productAttributeCategory.getId()));
            productAttributeList.forEach(productAttribute -> {
                pmsProductAttributeCategoryItem.getProductAttributeList().add(productAttribute);
            });
            items.add(pmsProductAttributeCategoryItem);
        });
        return items;
    }
}
