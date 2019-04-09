package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.mapper.ProductCategoryMapper;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.atguigu.gmall.pms.vo.PmsProductCategoryParam;
import com.atguigu.gmall.pms.vo.PmsProductCategoryWithChildrenItem;
import com.atguigu.gmall.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Slf4j
@Service
@Component
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public List<PmsProductCategoryWithChildrenItem> getListWithChildren() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        //根据数据key获取该数据
        String cache = ops.get(RedisCacheConstant.PRODUCT_CATEGORY_CACHE_KEY);
        //判断缓存数据库中是否已存在数据
        if(!StringUtils.isEmpty(cache)){
            log.debug("PRODUCT_CATEGORY_CACHE_KEY 缓存命中！");
            //将缓存数据库中的json数据使用fastjson转换为list数据集合返回
            List<PmsProductCategoryWithChildrenItem> list = JSON.parseArray(cache, PmsProductCategoryWithChildrenItem.class);
            return list;
        }

        log.debug("PRODUCT_CATEGORY_CACHE_KEY 缓存未命中！");
        //Redis中没有数据，就去mysql中查询并存入Redis数据库
        List<PmsProductCategoryWithChildrenItem> list = getBaseMapper().selectListWithChildren(0);
        //将数据转换为JSON字符串
        String jsonString = JSON.toJSONString(list);
        //存入Redis，保存三天
        ops.set(RedisCacheConstant.PRODUCT_CATEGORY_CACHE_KEY,jsonString,3, TimeUnit.DAYS);
        return list;
    }

    @Override
    public ProductCategory getItem(Long id) {
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        return getBaseMapper().selectOne(queryWrapper);
    }

    @Override
    public Integer updateProductCategoryById(Long id, PmsProductCategoryParam productCategoryParam) {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(id);
        //设置修改后的数据
        BeanUtils.copyProperties(productCategoryParam,productCategory);
        return getBaseMapper().updateById(productCategory);
    }

    @Override
    public Integer deleteById(Long id) {
        return getBaseMapper().deleteById(id);
    }

    @Override
    public HashMap<String, Object> getPageList(Long parentId, Integer pageNum, Integer pageSize) {
        Page<ProductCategory> page = new Page<>(pageNum,pageSize);
        getBaseMapper().selectPage(page,new QueryWrapper<ProductCategory>().eq("parent_id",parentId));
        HashMap<String, Object> pageInfo = PageUtils.getPage(page);
        return pageInfo;
    }

    @Override
    public Integer create(PmsProductCategoryParam productCategoryParam) {
        ProductCategory productCategory = new ProductCategory();
        BeanUtils.copyProperties(productCategoryParam,productCategory);
        return getBaseMapper().insert(productCategory);
    }
}
