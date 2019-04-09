package com.atguigu.gmall.admin.sms.vo;

import com.atguigu.gmall.sms.entity.Coupon;
import com.atguigu.gmall.sms.entity.CouponProductCategoryRelation;
import com.atguigu.gmall.sms.entity.CouponProductRelation;

import java.util.List;

public class SmsCouponParam extends Coupon {
    List<CouponProductCategoryRelation> productCategoryRelationList;
    List<CouponProductRelation> productRelationList;
}
