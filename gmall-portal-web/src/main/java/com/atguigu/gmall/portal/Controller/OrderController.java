package com.atguigu.gmall.portal.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.atguigu.gmall.cart.bean.CartItem;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.order.service.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.OrderResponseVo;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import com.atguigu.gmall.to.CommonResult;
import com.atguigu.gmall.ums.entity.MemberReceiveAddress;
import com.atguigu.gmall.ums.service.MemberReceiveAddressService;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "OrderController",description = "订单模块")
@Controller
@RequestMapping("/order")
@CrossOrigin
public class OrderController {

    @Reference(version = "1.0")
    CartService cartService;

    @Reference(version = "1.0")
    MemberReceiveAddressService memberReceiveAddressService;

    @Reference(version = "1.0")
    OrderService orderService;

    @ResponseBody
    @PostMapping("/confirm")
    public CommonResult orderConfirm(@RequestParam("token") String token){
        //确认结算，去结算确认页，返回确认结算页数据
        List<CartItem> cartItemList = cartService.cartItemConfirm(token);
        //根据商品的购物项查询对应的优惠券...（未做）
        //用户可选的收货地址
        List<MemberReceiveAddress> addresses = memberReceiveAddressService.getUserAddress(token);
        //颁发交易令牌，防止用户重复提交
        RpcContext.getContext().setAttachment("usertoken",token);
        String tradeToken = orderService.getTradeToken();
        return new CommonResult().success(new OrderConfirmVo(cartItemList,addresses,tradeToken));
    }

    /**
     * 下订单并跳转到支付页面
     */
    @ResponseBody
    @PostMapping("/submit")
    public OrderResponseVo payOrder(OrderSubmitVo orderSubmitVo){
        //创建订单
        OrderResponseVo orderResponseVo = orderService.createOrder(orderSubmitVo);
        //颁发交易令牌
        //给前端返回订单号等消息，展示到确认支付页面，选择支付方式进行支付
        return orderResponseVo;
    }
}
