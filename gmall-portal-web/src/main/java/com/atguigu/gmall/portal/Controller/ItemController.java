package com.atguigu.gmall.portal.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ProductAllInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Api(description = "商品详情管理")
@RequestMapping("/item")
public class ItemController {

    @Reference(version = "1.0")
    ItemService itemService;

    @ApiOperation("获取商品详情页面数据")
    @GetMapping(value = "/{skuId}.html",produces = "application/json")
    public ProductAllInfo getProductInfo(@PathVariable("skuId") Long skuId){
        ProductAllInfo productAllInfo = itemService.getProductInfo(skuId);
        return productAllInfo;
    }
}
