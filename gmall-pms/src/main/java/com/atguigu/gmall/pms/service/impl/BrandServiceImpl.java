package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Brand;
import com.atguigu.gmall.pms.mapper.BrandMapper;
import com.atguigu.gmall.pms.service.BrandService;
import com.atguigu.gmall.pms.vo.PmsBrandParam;
import com.atguigu.gmall.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {


    @Override
    public PmsBrandParam getItem(Long id) {
        Brand brand = getBaseMapper().selectById(id);
        PmsBrandParam brandParam = new PmsBrandParam();
        BeanUtils.copyProperties(brand,brandParam);
        return brandParam;
    }

    @Override
    public HashMap<String, Object> getPageList(String keyword, Integer pageNum, Integer pageSize) {
        Page<Brand> page = new Page<>(pageNum,pageSize);
        QueryWrapper<Brand> queryWrapper = null;
        if(!StringUtils.isEmpty(keyword)){
            queryWrapper = new QueryWrapper<>();
            queryWrapper.like("name",keyword);
        }
        getBaseMapper().selectPage(page, queryWrapper);
        HashMap<String, Object> pageInfo = PageUtils.getPage(page);
        return pageInfo;
    }

    @Override
    public Integer create(PmsBrandParam pmsBrand) {
        Brand brand = new Brand();
        BeanUtils.copyProperties(pmsBrand,brand);
        return getBaseMapper().insert(brand);
    }

    @Override
    public Integer updateBrand(Long id,PmsBrandParam pmsBrandParam) {
        Brand brand = new Brand();
        brand.setId(id);
        BeanUtils.copyProperties(pmsBrandParam,brand);
        return getBaseMapper().updateById(brand);
    }

    @Override
    public Integer deleteBrand(Long id) {
        return getBaseMapper().deleteById(id);
    }

    @Override
    public Integer updateShowStatus(List<Long> ids, Integer showStatus) {
        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",ids);
        Brand brand = new Brand();
        brand.setShowStatus(showStatus);
        return getBaseMapper().update(brand,queryWrapper);
    }

    @Override
    public Integer deleteBatch(List<Long> ids) {
        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",ids);
        return getBaseMapper().delete(queryWrapper);
    }
}
