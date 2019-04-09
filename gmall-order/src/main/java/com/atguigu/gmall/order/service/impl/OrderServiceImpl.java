package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.bean.Cart;
import com.atguigu.gmall.cart.bean.CartItem;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.oms.entity.Order;
import com.atguigu.gmall.oms.entity.OrderItem;
import com.atguigu.gmall.order.mapper.OrderItemMapper;
import com.atguigu.gmall.order.mapper.OrderMapper;
import com.atguigu.gmall.order.service.service.OrderService;
import com.atguigu.gmall.order.to.OrderMQTo;
import com.atguigu.gmall.order.vo.OrderResponseVo;
import com.atguigu.gmall.order.vo.OrderStatusEnume;
import com.atguigu.gmall.order.vo.OrderSubmitVo;
import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.entity.MemberReceiveAddress;
import com.atguigu.gmall.ums.service.MemberReceiveAddressService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service(version = "1.0")
@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    JedisPool jedisPool;

    @Reference(version = "1.0")
    MemberReceiveAddressService memberReceiveAddressService;

    @Reference(version = "1.0")
    CartService cartService;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public String getTradeToken() {
        String token = RpcContext.getContext().getAttachment("usertoken");
        String tradeToken = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(RedisCacheConstant.TRADE_TOKEN + token,
                tradeToken,RedisCacheConstant.TRADE_TOKEN_TIMEOUT, TimeUnit.MINUTES);
        return tradeToken;
    }

    @Override
    public OrderResponseVo createOrder(OrderSubmitVo orderSubmitVo) {
        String s = redisTemplate.opsForValue().get(RedisCacheConstant.TRADE_TOKEN + orderSubmitVo.getToken());
        String s1 = orderSubmitVo.getTradeToke();
        //使用脚本查询redis数据库存储的交易令牌,与用户提交的交易令牌进行比较是否一致，防止重复提交，但是一定要保持整个一系列的操作的一致性
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Jedis jedis = jedisPool.getResource();
        Long result = (Long)jedis.eval(script,
                Collections.singletonList(s),
                Collections.singletonList(s1));
        if(result == 1){
            //检验成功,获取用户信息
            String memberInfo = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + orderSubmitVo.getToken());
            Member member = JSON.parseObject(memberInfo, Member.class);
            Order order = new Order();

            //查出用户信息并封装
            order.setMemberId(member.getId());
            order.setMemberUsername(member.getNickname());

            //查处收货地址信息并封装
            MemberReceiveAddress address = memberReceiveAddressService.getAddressById(orderSubmitVo.getAddressId());
            order.setReceiverCity(address.getCity());
            order.setReceiverDetailAddress(address.getDetailAddress());
            order.setReceiverName(address.getName());
            order.setReceiverPhone(address.getPhoneNumber());
            order.setReceiverProvince(address.getProvince());
            order.setReceiverRegion(address.getRegion());

            //获取结算页所有商品信息并计算订单总额信息
            List<CartItem> cartItems = cartService.cartItemConfirm(orderSubmitVo.getToken());
            Cart cart = new Cart();
            cartItems.forEach(item -> cart.getCartItems().add(item));
            BigDecimal totalPirce = cart.getTotalPirce();
            order.setTotalAmount(totalPirce);

            //初始化订单状态
            order.setStatus(OrderStatusEnume.UNPAY.getCode());

            //设置订单号
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String date = dateFormat.format(new Date());
            Long count = redisTemplate.opsForValue().increment("orderCountId");
            // 0 代表前面补充0
            // 4 代表长度为4
            // d 代表参数为正数型
            String num = String.format("%09d", count);
            String orderSn = date + num;
            order.setOrderSn(orderSn);

            //保存该订单
            orderMapper.insert(order);

            ArrayList<OrderItem> orderItems = new ArrayList<>();
            //保存订单中每个订单项
            cartItems.forEach(item -> {
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(item,orderItem);
                orderItem.setProductQuantity(item.getNum());//每个订单项购买数量
                orderItem.setRealAmount(item.getNewPrice());//每个订单项总金额
                orderItem.setOrderSn(orderSn);//为每个订单项设置该订单的订单号
                orderItem.setOrderId(order.getId());//为每个订单项设置该订单的订单ID
                //保存每个订单项
                orderItemMapper.insert(orderItem);
                orderItems.add(orderItem);
            });

            //将订单创建完成消息发送到RabbitMQ
            OrderMQTo orderMQTo = new OrderMQTo(order,orderItems);
            rabbitTemplate.convertAndSend("orderFanoutExchange","",orderMQTo);

            //给前端返回数据
            OrderResponseVo orderResponseVo = new OrderResponseVo("订单创建成功！",
                    0, orderSn,
                    order.getTotalAmount().toString(),
                    "订单提交成功，请尽快付款!订单号：" + orderSn,
                    orderItems.get(0).getProductName());
            return orderResponseVo;
        }else{
            //检验失败，抛出异常
            throw new RuntimeException("超出结算时间，请重新结算！");
        }
    }
}
