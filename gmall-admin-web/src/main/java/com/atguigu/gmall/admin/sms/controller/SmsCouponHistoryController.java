package com.atguigu.gmall.admin.sms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.sms.entity.CouponHistory;
import com.atguigu.gmall.sms.service.CouponHistoryService;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Api(tags = "SmsCouponHistoryController",description = "优惠券领取管理系统")
@CrossOrigin
@RequestMapping("/couponHistory")
@RestController
public class SmsCouponHistoryController {

    @Reference(version = "1.0")
    CouponHistoryService couponHistoryService;

    @ApiOperation("根据优惠券id，使用状态，订单编号分页获取领取记录")
    @GetMapping("/list")
    public Object list(
            @ApiParam(name = "couponId", value = "优惠券ID",required = false)
            @RequestParam(value = "couponId",required = false)Long couponId,

            @ApiParam(name = "useStatus", value = "使用状态",required = false)
            @RequestParam(value = "useStatus",required = false)Integer useStatus,

            @ApiParam(name = "orderSn", value = "使用状态：0->未使用；1->已使用；2->已过期",required = false)
            @RequestParam(value = "orderSn",required = false)String orderSn,

            @RequestParam(value = "pageSize", defaultValue = "5")Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1")Integer pageNum){
        HashMap<String,Object> data = couponHistoryService.getCouponHistoryInfo(couponId,useStatus,orderSn,pageNum,pageSize);
        return new CommonResult().success(data);
    }
}
