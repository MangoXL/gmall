package com.atguigu.gmall.sms.vo;

import com.atguigu.gmall.sms.entity.Coupon;
import com.atguigu.gmall.sms.entity.CouponProductCategoryRelation;
import com.atguigu.gmall.sms.entity.CouponProductRelation;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SmsCouponParam extends Coupon implements Serializable{
    List<CouponProductCategoryRelation> productCategoryRelationList;
    List<CouponProductRelation> productRelationList;
}
