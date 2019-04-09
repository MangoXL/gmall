package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.vo.PmsProductAttributeParam;
import com.atguigu.gmall.util.PageUtils;
import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.mapper.ProductAttributeMapper;
import com.atguigu.gmall.pms.service.ProductAttributeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * 商品属性参数表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductAttributeServiceImpl extends ServiceImpl<ProductAttributeMapper, ProductAttribute> implements ProductAttributeService {

    @Autowired
    ProductAttributeMapper productAttributeMapper;

    @Override
    public HashMap<String, Object> getPageList(Long cid, Integer type, Integer pageSize, Integer pageNum) {
        //创建分页对象
        Page<ProductAttribute> page = new Page<>(pageNum,pageSize);
        QueryWrapper<ProductAttribute> queryWrapper = new QueryWrapper<>();
        //组装条件，根据商品ID和属性类型（销售参数或者规格参数）查询
        queryWrapper.eq("product_attribute_category_id",cid)
                .eq("type",type);
        getBaseMapper().selectPage(page, queryWrapper);
        //组装页面数据并返回
        HashMap<String, Object> pageInfo = PageUtils.getPage(page);
        return pageInfo;
    }

    @Override
    public PmsProductAttributeParam getItem(Long id) {
        ProductAttribute productAttribute = getBaseMapper().selectById(id);
        PmsProductAttributeParam pmsProductAttributeParam = new PmsProductAttributeParam();

        BeanUtils.copyProperties(productAttribute,pmsProductAttributeParam);
        return pmsProductAttributeParam;
    }

    @Override
    public Integer updateProductAttribute(Long id, PmsProductAttributeParam productAttributeParam) {
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setId(id);
        BeanUtils.copyProperties(productAttributeParam,productAttribute);
        return getBaseMapper().updateById(productAttribute);
    }

    @Override
    public Integer create(PmsProductAttributeParam productAttributeParam) {
        ProductAttribute productAttribute = new ProductAttribute();
        BeanUtils.copyProperties(productAttributeParam,productAttribute);
        return getBaseMapper().insert(productAttribute);
    }

    @Override
    public Integer deleteBatch(List<Long> ids) {
        QueryWrapper<ProductAttribute> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",ids);
        return getBaseMapper().delete(queryWrapper);
    }

    @Override
    public List<PmsProductAttributeParam> getAttrInfo(Long productCategoryId) {
        ArrayList<PmsProductAttributeParam> list = new ArrayList<>();
        List<ProductAttribute> ProductAttributes = productAttributeMapper.selectList(new QueryWrapper<ProductAttribute>().eq("product_attribute_category_id", productCategoryId));
        ProductAttributes.forEach(productAttribute -> {
            PmsProductAttributeParam pmsProductAttributeParam = new PmsProductAttributeParam();
            BeanUtils.copyProperties(productAttribute,pmsProductAttributeParam);
            list.add(pmsProductAttributeParam);
        });
        return list;
    }
}
