package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gmall.cart.bean.Cart;
import com.atguigu.gmall.cart.bean.CartItem;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.vo.SkuResponse;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.atguigu.gmall.ums.entity.Member;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

@Service(version = "1.0")
@Component
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient client;

    @Reference(version = "1.0")
    ProductService productService;

    @Reference(version = "1.0")
    SkuStockService skuStockService;


    @Override
    public SkuResponse addToCart(Long skuId, Integer num, String cartKey) {
        //返回页面的数据
        SkuResponse skuResponse = new SkuResponse();
        //获取远程传输的token
        String token = RpcContext.getContext().getAttachment("usertoken");
        //获取用户数据
        String memberInfo = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberInfo, Member.class);
        Long memberId = member == null?0L:member.getId();
        String memberName = member == null?"":member.getNickname();
        //查询这个商品的sku信息
        SkuStock skuStock = skuStockService.getSkuInfo(skuId);
        //查询这个商品的spu信息
        Product product = productService.getProductByIdFromCache(skuStock.getProductId());
        CartItem item = new CartItem(product.getId(),
                skuStock.getId(),
                memberId,
                num,
                skuStock.getPrice(),
                skuStock.getPrice(),
                num,
                skuStock.getSp1(), skuStock.getSp2(), skuStock.getSp3(),
                product.getPic(),
                product.getName(),
                memberName,
                product.getProductCategoryId(),
                product.getBrandName(),
                false,
                "满199减90"
        );
        if(StringUtils.isEmpty(memberInfo)){
            //用户未登录
            if(!StringUtils.isEmpty(cartKey)){
                skuResponse.setCartKey(cartKey);
                cartKey = RedisCacheConstant.CART_TEMP + cartKey;
                addItemToCart(item,num,cartKey);
            }else{
                String newCartKey = UUID.randomUUID().toString().replace("-", "");
                skuResponse.setCartKey(newCartKey);
                newCartKey = RedisCacheConstant.CART_TEMP + newCartKey;
                addItemToCart(item,num,newCartKey);
            }
        }else{
            //用户已登录:使用在线购物车
            String loginCartKey = RedisCacheConstant.USER_CART + member.getId();
            //合并购物车
            mergeCart(RedisCacheConstant.CART_TEMP + cartKey,loginCartKey);
            //存入购物车
            addItemToCart(item,num,loginCartKey);
        }
        skuResponse.setItem(item);
        return skuResponse;
    }

    @Override
    public boolean updateToCart(Long skuId, Integer num, String cartKey) {
        //获取远程传输的token
        String token = RpcContext.getContext().getAttachment("usertoken");
        //redis查询用户信息
        String memberInfo = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberInfo, Member.class);
        RMap<String, String> map = null;
        if(member == null){
            //用户未登录
            map = client.getMap(RedisCacheConstant.CART_TEMP + cartKey);
        }else{
            //用户已登录
            map = client.getMap(RedisCacheConstant.USER_CART + member.getId());
        }
        String item = map.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(item, CartItem.class);
        cartItem.setNum(num);
        String jsonString = JSON.toJSONString(cartItem);
        map.put(skuId.toString(),jsonString);
        return true;
    }

    @Override
    public boolean deleteToCart(Long skuId, String cartKey) {
        //获取远程传输的token
        String token = RpcContext.getContext().getAttachment("usertoken");
        //redis查询用户信息
        String memberInfo = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberInfo, Member.class);
        RMap<String, String> map = null;
        if(member == null){
            //用户未登录
            map = client.getMap(RedisCacheConstant.CART_TEMP + cartKey);
        }else{
            //用户已登录
            map = client.getMap(RedisCacheConstant.USER_CART + member.getId());
        }
        map.remove(skuId.toString());
        return true;
    }

    @Override
    public boolean checkToCart(Long skuId, Integer flag, String cartKey) {
        //获取远程传输的token
        String token = RpcContext.getContext().getAttachment("usertoken");
        //redis查询用户信息
        String memberInfo = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberInfo, Member.class);
        RMap<String, String> map = null;
        if(member == null){
            //用户未登录
            map = client.getMap(RedisCacheConstant.CART_TEMP + cartKey);
        }else{
            //用户已登录
            map = client.getMap(RedisCacheConstant.USER_CART + member.getId());
        }
        String item = map.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(item, CartItem.class);
        cartItem.setChecked(flag == 0?false:true);
        String jsonString = JSON.toJSONString(cartItem);
        map.put(skuId.toString(),jsonString);

        //获取所有勾选中的购物项set
        String checked = map.get("checked");
        if(StringUtils.isEmpty(checked)){
            HashSet<String> set = new HashSet<>();
            if(flag == 0){
                set.remove(skuId.toString());
            }else{
                set.add(skuId.toString());
            }
            String setJson = JSON.toJSONString(set);
            map.put("checked",setJson);
        }else{
            Set<String> set = JSON.parseObject(checked, new TypeReference<Set<String>>() {});
            if(flag == 0){
                set.remove(skuId.toString());
            }else{
                set.add(skuId.toString());
            }
            String setJson = JSON.toJSONString(set);
            map.put("checked",setJson);
        }
        return true;
    }

    @Override
    public Cart list(String cartKey) {
        //获取远程传输的token
        String token = RpcContext.getContext().getAttachment("usertoken");
        //redis查询用户信息
        String memberInfo = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberInfo, Member.class);
        RMap<String, String> map = null;
        if(member == null){
            //用户未登录
            map = client.getMap(RedisCacheConstant.CART_TEMP + cartKey);
        }else{
            //用户已登录
            //先尝试合并购物车
            mergeCart(RedisCacheConstant.CART_TEMP + cartKey,RedisCacheConstant.USER_CART + member.getId());
            //然后查出用户登录后的购物车数据
            map = client.getMap(RedisCacheConstant.USER_CART + member.getId());
        }
        Cart cart = new Cart();
        map.entrySet().forEach(entry -> {
            if(!entry.getKey().equals("checked")){
                CartItem cartItem = JSON.parseObject(entry.getValue(), CartItem.class);
                cart.getCartItems().add(cartItem);
            }
        });
        return cart;
    }

    @Override
    public List<CartItem> cartItemConfirm(String token) {
        //返回的数据集合
        ArrayList<CartItem> cartItems = new ArrayList<>();
        //获取用户数据
        String memberInfo = redisTemplate.opsForValue().get(RedisCacheConstant.USER_INFO_CACHE_KEY + token);
        Member member = JSON.parseObject(memberInfo, Member.class);
        //获取用户的购物车
        RMap<String, String> map = client.getMap(RedisCacheConstant.USER_CART + member.getId());
        //获取用户购物车中已选中的商品skuId集合
        String checked = map.get("checked");
        Set<String> set = JSON.parseObject(checked, new TypeReference<Set<String>>() {});
        if(set != null && !set.isEmpty()){
            set.forEach(skuId -> {
                //获取选中的商品项信息
                String cartItemJson = map.get(skuId);
                CartItem cartItem = JSON.parseObject(cartItemJson, CartItem.class);
                //查询购物车中的商品价格是否是最新价格，如果不是，就实时更新结算页商品价格信息
                BigDecimal price = skuStockService.getPriceById(skuId);
                if(cartItem.getNewPrice() != null){
                    //将原来购物车中商品的新价格赋值给老价格
                    cartItem.setPrice(cartItem.getNewPrice());
                }
                cartItem.setNewPrice(price);
                cartItems.add(cartItem);
            });
        }
        return cartItems;
    }

    /**
     * 合并购物车数据
     * @param cartKey
     * @param loginCartKey
     */
    private void mergeCart(String cartKey, String loginCartKey) {
        //获取临时购物车数据
        RMap<String, String> map = client.getMap(cartKey);
        if(map != null && map.entrySet() != null){
            map.entrySet().forEach(entry -> {
                if(!entry.getKey().equals("checked")){
                    //遍历获取临时购物车每个商品项
                    CartItem cartItem = JSON.parseObject(entry.getValue(), CartItem.class);
                    //将临时购物车里的每条购物项数据添加到登录后用户的购物车里
                    addItemToCart(cartItem,cartItem.getNum(),loginCartKey);
                    //删除临时购物车的商品
                    map.remove(entry.getKey());
                }
            });
        }
    }

    private void addItemToCart(CartItem item, Integer num, String cartKey) {
        //获取购物车
        RMap<String, String> map = client.getMap(cartKey);
        //查询购物车中是否有这个商品
        boolean result = map.containsKey(item.getProductSkuId().toString());
        if(result){
            //该商品已存在,获取该商品信息
            String itemInfo = map.get(item.getProductSkuId().toString());
            CartItem cartItem = JSON.parseObject(itemInfo, CartItem.class);
            //增加数量
            cartItem.setNum(cartItem.getNum() + num);
            String jsonString = JSON.toJSONString(cartItem);
            //将新增后的数据添加到数据库
            map.put(item.getProductSkuId().toString(),jsonString);
        }else{
            //该商品不存在,直接添加商品项
            String itemInfo = JSON.toJSONString(item);
            map.put(item.getProductSkuId().toString(),itemInfo);
        }
    }
}
