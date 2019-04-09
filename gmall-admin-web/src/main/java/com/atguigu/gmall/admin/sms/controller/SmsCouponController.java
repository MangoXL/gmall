package com.atguigu.gmall.admin.sms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.sms.entity.Coupon;
import com.atguigu.gmall.sms.service.CouponService;
import com.atguigu.gmall.sms.vo.SmsCouponParam;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@CrossOrigin//跨域
@RestController
@Api(tags = "SmsCouponController", description = "优惠券管理")
@RequestMapping("/coupon")
public class SmsCouponController {
    @Reference
    private CouponService couponService;

    @ApiOperation("添加优惠券")
    @PostMapping("/create")
    public Object create(@RequestBody SmsCouponParam smsCouponParam){
        couponService.create(smsCouponParam);
        return new CommonResult().success(null);
    }

    @ApiOperation("根据优惠券名称和类型分页获取优惠券列表")
    @GetMapping("/list")
    public Object list(
            @ApiParam(name = "name", value = "优惠券名称",required = false)
            @RequestParam(value = "name",required = false) String name,

            @ApiParam(name = "type", value = "优惠卷类型；0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券",required = false)
            @RequestParam(value = "type",required = false) Integer type,

            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize){
        HashMap<String,Object> data = couponService.getList(name,type,pageNum,pageSize);
        return new CommonResult().success(data);
    }

    @ApiOperation("删除优惠券")
    @PostMapping("/delete/{id}")
    public Object delete(@PathVariable("id") Long id){
        Integer data = couponService.deleteCouponById(id);
        return new CommonResult().success(data);
    }

    @ApiOperation("获取单个优惠券的详细信息")
    @GetMapping("/{id}")
    public Object getCouponById(@PathVariable("id") Long id){
        SmsCouponParam data = couponService.getCouponById(id);
        return new CommonResult().success(data);
    }

    @ApiOperation("修改优惠券")
    @PostMapping("/update/{id}")
    public Object update(@PathVariable("id") Long id,@RequestBody SmsCouponParam smsCouponParam){
        couponService.updateCouponById(id,smsCouponParam);
        return new CommonResult().success(null);
    }
}
