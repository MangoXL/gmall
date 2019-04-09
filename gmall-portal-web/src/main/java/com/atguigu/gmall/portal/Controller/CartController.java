package com.atguigu.gmall.portal.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.atguigu.gmall.cart.bean.Cart;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.vo.SkuResponse;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "CartController",description = "购物车模块管理")
@CrossOrigin
@RequestMapping("/cart")
public class CartController {

    @Reference(version = "1.0")
    CartService cartService;

    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public CommonResult addToCart(@RequestParam("skuId")Long skuId,
                                  @RequestParam("num")Integer num,
                                  @RequestParam(value = "token",required = false)String token,
                                  @RequestParam(value = "cartKey",required = false)String cartKey){
        //dubbo隐式传参
        RpcContext.getContext().setAttachment("usertoken",token);
        SkuResponse skuResponse = cartService.addToCart(skuId,num,cartKey);
        return new CommonResult().success(skuResponse);
    }

    @ApiOperation("修改购物车商品数量")
    @PostMapping("/update")
    public CommonResult updateToCart(@RequestParam("skuId")Long skuId,
                                     @RequestParam("num")Integer num,
                                     @RequestParam(value = "token",required = false)String token,
                                     @RequestParam(value = "cartKey",required = false)String cartKey){
        RpcContext.getContext().setAttachment("usertoken",token);
        boolean result = cartService.updateToCart(skuId,num,cartKey);
        return new CommonResult().success(result);
    }

    @ApiOperation("删除购物车商品")
    @PostMapping("/delete")
    public CommonResult deleteToCart(@RequestParam("skuId")Long skuId,
                                     @RequestParam(value = "token",required = false)String token,
                                     @RequestParam(value = "cartKey",required = false)String cartKey){
        RpcContext.getContext().setAttachment("usertoken",token);
        boolean result = cartService.deleteToCart(skuId,cartKey);
        return new CommonResult().success(result);
    }

    @ApiOperation("修改购物车商品选中状态")
    @PostMapping("/check")
    public CommonResult checkToCart(@RequestParam("skuId")Long skuId,
                                    @ApiParam(value = "需要选中的商品，0不选中，1选中")
                                     @RequestParam(value = "flag",required = false)Integer flag,
                                     @RequestParam(value = "token",required = false)String token,
                                     @RequestParam(value = "cartKey",required = false)String cartKey){
        RpcContext.getContext().setAttachment("usertoken",token);
        boolean result = cartService.checkToCart(skuId,flag,cartKey);
        return new CommonResult().success(result);
    }

    @ApiOperation("获取购物车列表")
    @GetMapping("/list")
    public CommonResult list( @RequestParam(value = "token",required = false)String token,
                                    @RequestParam(value = "cartKey",required = false)String cartKey){
        RpcContext.getContext().setAttachment("usertoken",token);
        Cart cart = cartService.list(cartKey);
        return new CommonResult().success(cart);
    }
}
