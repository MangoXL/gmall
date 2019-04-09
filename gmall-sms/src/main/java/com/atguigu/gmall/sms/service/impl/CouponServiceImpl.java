package com.atguigu.gmall.sms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.sms.entity.Coupon;
import com.atguigu.gmall.sms.entity.CouponProductCategoryRelation;
import com.atguigu.gmall.sms.entity.CouponProductRelation;
import com.atguigu.gmall.sms.mapper.CouponMapper;
import com.atguigu.gmall.sms.mapper.CouponProductCategoryRelationMapper;
import com.atguigu.gmall.sms.mapper.CouponProductRelationMapper;
import com.atguigu.gmall.sms.service.CouponService;
import com.atguigu.gmall.sms.vo.SmsCouponParam;
import com.atguigu.gmall.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 优惠卷表 服务实现类
 * </p>
 *
 * @author XiaoLe
 * @since 2019-03-19
 */
@Service
@Component
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    ThreadLocal<Coupon> couponThreadLocal= new ThreadLocal<Coupon>();

    @Autowired
    CouponProductCategoryRelationMapper couponProductCategoryRelationMapper;

    @Autowired
    CouponProductRelationMapper couponProductRelationMapper;

    @Override
    public void create(SmsCouponParam smsCouponParam) {
        //添加优惠券
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(smsCouponParam,coupon);
        getBaseMapper().insert(coupon);
        couponThreadLocal.set(coupon);

        //添加优惠券商品类别关系
        List<CouponProductCategoryRelation> productCategoryRelationList = smsCouponParam.getProductCategoryRelationList();
        productCategoryRelationList.forEach(productCategoryRelation -> {
            productCategoryRelation.setCouponId(couponThreadLocal.get().getId());
            couponProductCategoryRelationMapper.insert(productCategoryRelation);
        });

        //添加优惠券商品关系
        List<CouponProductRelation> productRelationList = smsCouponParam.getProductRelationList();
        productRelationList.forEach(productRelation -> {
            productRelation.setCouponId(couponThreadLocal.get().getId());
            couponProductRelationMapper.insert(productRelation);
        });
    }

    @Override
    public HashMap<String, Object> getList(String name, Integer type, Integer pageNum, Integer pageSize) {
        QueryWrapper<Coupon> queryWrapper = null;
        if(!StringUtils.isEmpty(name) || !StringUtils.isEmpty(type)){
            queryWrapper = new QueryWrapper<>();
            if(!StringUtils.isEmpty(name)){
                queryWrapper.like("name",name);
            }
            if(!StringUtils.isEmpty(type)){
                queryWrapper.eq("type",type);
            }
        }
        Page<Coupon> page = new Page<>(pageNum,pageSize);
        getBaseMapper().selectPage(page,queryWrapper);
        return PageUtils.getPage(page);
    }

    @Override
    public Integer deleteCouponById(Long id) {
        QueryWrapper<CouponProductCategoryRelation> productCategoryRelationQueryWrapper = new QueryWrapper<>();
        productCategoryRelationQueryWrapper.eq("coupon_id",id);
        CouponProductCategoryRelation couponProductCategoryRelation = couponProductCategoryRelationMapper.selectOne(productCategoryRelationQueryWrapper);
        if(!StringUtils.isEmpty(couponProductCategoryRelation)){
            couponProductCategoryRelationMapper.delete(productCategoryRelationQueryWrapper);
        }

        QueryWrapper<CouponProductRelation> productRelationQueryWrapper = new QueryWrapper<>();
        productRelationQueryWrapper.eq("coupon_id",id);
        CouponProductRelation couponProductRelation = couponProductRelationMapper.selectOne(productRelationQueryWrapper);
        if(!StringUtils.isEmpty(couponProductRelationMapper)){
            couponProductRelationMapper.delete(productRelationQueryWrapper);
        }

        return getBaseMapper().deleteById(id);
    }

    @Override
    public void updateCouponById(Long id, SmsCouponParam smsCouponParam) {
        //修改优惠券信息
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(smsCouponParam,coupon);
        coupon.setId(id);
        getBaseMapper().updateById(coupon);

        //修改优惠券与商品分类关联信息
        ArrayList<Long> productCategoryIds = new ArrayList<>();
        if(smsCouponParam.getProductCategoryRelationList() != null && smsCouponParam.getProductCategoryRelationList().size() > 0){
            smsCouponParam.getProductCategoryRelationList().forEach(couponProductCategoryRelation -> {
                //查询数据库中是否已存在该分类记录
                CouponProductCategoryRelation productCategoryRelation = couponProductCategoryRelationMapper.selectOne(
                        new QueryWrapper<CouponProductCategoryRelation>()
                                .eq("product_category_id", couponProductCategoryRelation.getProductCategoryId())
                                .eq("coupon_id",id));
                if(productCategoryRelation != null){
                    //该分类信息已存在，记录分类ID
                    productCategoryIds.add(productCategoryRelation.getProductCategoryId());
                }else{
                    //该分类信息不存在，插入该分类记录并记录该分类ID
                    couponProductCategoryRelation.setCouponId(id);
                    couponProductCategoryRelationMapper.insert(couponProductCategoryRelation);
                    productCategoryIds.add(couponProductCategoryRelation.getProductCategoryId());
                }
            });
            if(productCategoryIds.size() != 0){
                //删除数据库中已移除的数据
                couponProductCategoryRelationMapper.delete(
                        new QueryWrapper<CouponProductCategoryRelation>()
                                .eq("coupon_id",id)
                                .notIn("product_category_id",productCategoryIds));
            }
        }

        if(smsCouponParam.getProductRelationList() != null && smsCouponParam.getProductRelationList().size() > 0){
            //修改优惠券与商品关联信息
            ArrayList<Long> productIds = new ArrayList<>();
            smsCouponParam.getProductRelationList().forEach(couponProductRelation -> {
                //查询数据库中是否已存在该商品记录
                CouponProductRelation productRelation = couponProductRelationMapper.selectOne(
                        new QueryWrapper<CouponProductRelation>()
                                .eq("product_id", couponProductRelation.getProductId())
                                .eq("coupon_id", id));
                if(productRelation != null){
                    //该商品信息已存在，记录商品ID
                    productIds.add(productRelation.getProductId());
                }else{
                    //该商品信息不存在，插入该商品记录并记录该商品ID
                    couponProductRelation.setCouponId(id);
                    couponProductRelationMapper.insert(couponProductRelation);
                    productIds.add(couponProductRelation.getProductId());
                }
            });

            if(productIds.size() != 0){
                //删除数据库中已移除的数据
                couponProductRelationMapper.delete(
                        new QueryWrapper<CouponProductRelation>()
                                .eq("coupon_id",id)
                                .notIn("product_id",productIds));
            }
        }
    }

    @Override
    public SmsCouponParam getCouponById(Long id) {
        SmsCouponParam result = new SmsCouponParam();
        //获取优惠券信息
        Coupon coupon = getBaseMapper().selectById(id);
        BeanUtils.copyProperties(coupon,result);
        //获取优惠券与商品分类关联信息
        List<CouponProductCategoryRelation> couponProductCategoryRelationList = couponProductCategoryRelationMapper.selectList(new QueryWrapper<CouponProductCategoryRelation>().eq("coupon_id", id));
        result.setProductCategoryRelationList(couponProductCategoryRelationList);
        //获取优惠券与商品关联信息
        List<CouponProductRelation> couponProductRelationList = couponProductRelationMapper.selectList(new QueryWrapper<CouponProductRelation>().eq("coupon_id", id));
        result.setProductRelationList(couponProductRelationList);

        return result;
    }
}
