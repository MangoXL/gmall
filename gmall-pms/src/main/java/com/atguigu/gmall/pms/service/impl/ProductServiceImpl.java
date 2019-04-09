package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cms.entity.SubjectProductRelation;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.mapper.*;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.atguigu.gmall.pms.vo.PmsProductQueryParam;
import com.atguigu.gmall.service.SearchService;
import com.atguigu.gmall.to.es.ESProduct;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.atguigu.gmall.util.PageUtils;
import com.atguigu.gmall.pms.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service(version = "1.0")
@Component
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    JedisPool jedisPool;

    @Reference(version = "1.0")
    SearchService searchService;

    ThreadLocal<Product> productThreadLocal = new ThreadLocal<>();

    @Autowired
    SkuStockMapper skuStockMapper;

    @Autowired
    ProductLadderMapper productLadderMapper;

    @Autowired
    ProductFullReductionMapper productFullReductionMapper;

    @Autowired
    MemberPriceMapper memberPriceMapper;

    @Autowired
    ProductAttributeValueMapper productAttributeValueMapper;

    @Autowired
    ProductCategoryMapper ProductCategoryMapper;

    @Autowired
    ProductAttributeMapper ProductAttributeMapper;

    @Override
    public HashMap<String, Object> getPageList(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_status",0);
        if (!StringUtils.isEmpty(productQueryParam)) {
            //商品名称模糊查询
            if (!StringUtils.isEmpty(productQueryParam.getKeyword())) {
                queryWrapper.like("name", productQueryParam.getKeyword());
            }
            //商品货号模糊查询
            if (!StringUtils.isEmpty(productQueryParam.getProductSn())) {
                queryWrapper.like("product_sn", productQueryParam.getProductSn());
            }
            //商品分类编号查询
            if (!StringUtils.isEmpty(productQueryParam.getProductCategoryId())) {
                queryWrapper.eq("product_category_id", productQueryParam.getProductCategoryId());
            }
            //商品品牌编号查询
            if (!StringUtils.isEmpty(productQueryParam.getBrandId())) {
                queryWrapper.eq("brand_id", productQueryParam.getBrandId());
            }
            //商品上架状态查询
            if (!StringUtils.isEmpty(productQueryParam.getPublishStatus())) {
                queryWrapper.eq("publish_status", productQueryParam.getPublishStatus());
            }
            //商品审核状态查询
            if (!StringUtils.isEmpty(productQueryParam.getVerifyStatus())) {
                queryWrapper.eq("verify_status", productQueryParam.getVerifyStatus());
            }
        }
        //创建分页对象并查询商品
        Page<Product> page = new Page<>(pageNum, pageSize);
        getBaseMapper().selectPage(page, queryWrapper);
        HashMap<String, Object> pageInfo = PageUtils.getPage(page);
        //返回数据
        return pageInfo;
    }

    /**
     * 1、保存商品基本信息：Product【REQUIRED】
     * 2、保存商品阶梯价格：ProductLadder【REQUIRED_NEW】
     * 3、保存商品满减价格：ProductFullReduction【REQUIRED_NEW】
     * 4、保存商品会员价格：MemberPrice【REQUIRED_NEW】
     * 5、保存商品的sku库存信息：SkuStock【REQUIRED】
     * 6、保存商品参数及自定义规格属性：ProductAttributeValue【REQUIRED_NEW】
     * 7、专题和商品关系：SubjectProductRelation【REQUIRED_NEW】
     * 8、优选专区和商品的关系：PrefrenceAreaProductRelation【REQUIRED_NEW】
     * 9、修改分类count：product_category【REQUIRED_NEW】
     * @param productParam
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void create(PmsProductParam productParam) {
        //获取当前类的代理对象
        ProductServiceImpl psProxy = (ProductServiceImpl)AopContext.currentProxy();
        //保存各项数据存入数据库
        psProxy.saveBaseProductInfo(productParam);
        psProxy.saveProductLadder(productParam.getProductLadderList());
        psProxy.saveProductFullReduction(productParam.getProductFullReductionList());
        psProxy.saveMemberPrice(productParam.getMemberPriceList());
        psProxy.saveProductAttributeValue(productParam.getProductAttributeValueList());
        psProxy.updateProductCategoryCount();
    }

    @Override
    public void updatePublishStatus(List<Long> ids, Integer publishStatus) {
        //判断是商品上架还是下架
        if(publishStatus == 1){
            //商品上架
            productPublish(ids);
        }else{
            //商品下架
        }
    }

    @Override
    public List<EsProductAttributeValue> getProductBaseAttr(Long productId) {
        return getBaseMapper().getProductBaseAttr(productId);
    }

    @Override
    public List<EsProductAttributeValue> getProductSaleAttr(Long productId) {
        return getBaseMapper().getProductSaleAttr(productId);
    }

    @Override
    public List<Product> getListByKeyword(String keyword) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name",keyword).or().eq("product_sn",keyword);
        return getBaseMapper().selectList(queryWrapper);
    }

    @Override
    public void updateDeleteStatus(List<Long> ids, Integer deleteStatus) {
        getBaseMapper().updateDeleteStatus(ids,deleteStatus);
    }

    @Override
    public Product getProductByIdFromCache(Long productId) {
        Product product = null;
        //先去缓存中查询数据
        Jedis jedis = jedisPool.getResource();
        String productJson = jedis.get(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + productId);
        if(!StringUtils.isEmpty(productJson)){
            //缓存命中
            product = JSON.parseObject(productJson, Product.class);
            return product;
        }else{
            //缓存未命中,先到数据库中查询，然后存入缓存并返回数据
            // 先去redis获取分布式锁，如果获取到锁，先设置超时时间，防止突发情况导致锁不能释放，再查询数据库
            //设置一个随机数作为锁的value，防止key已过期，但是业务没有执行完毕，当业务执行完后删除的锁是别人以后的锁
            String token = UUID.randomUUID().toString();
            String lock = jedis.set("lock", token, SetParams.setParams().nx().ex(5));
            if(!StringUtils.isEmpty(lock) && "ok".equalsIgnoreCase(lock)){
                try {
                    product = getBaseMapper().selectById(productId);
                    //不管数据库中有没有查询到数据，都要放入redis缓存中，防止缓存穿透，但是一定要为Key设置过期时间
                    if(product == null){
                        //设置一个随机数，增加到过期时间中，防止缓存雪崩
                        int num = new Random().nextInt(2000);
                        //没有查询到数据，放入缓存中，设置过期时间短一点
                        jedis.setex(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + productId, 60 + num,JSON.toJSONString(product));
                    }else{
                        //设置一个随机数，增加到过期时间中，防止缓存雪崩
                        int num = new Random().nextInt(2000);
                        //查询到数据，放入缓存中，设置过期时间久一点
                        jedis.setex(RedisCacheConstant.PRODUCT_INFO_CACHE_KEY + productId, 60 * 60 * 24 * 3 + num,JSON.toJSONString(product));
                    }
                }finally {
                    //最后一定要释放锁,释放锁的时候获取value判断，是否是获取是存入的值
                     /* 但是有可能刚获取锁的值，判断也正确，但是还没有执行删除，这个key过期了，
                     刚好别的线程又获取了锁，然后在执行这个删除锁的命令，还是会出现误删别的线程锁的情况，
                     所以使用lua脚本删除，这样才可以保证原子性 */

                     //脚本
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    //发送脚本给redis执行
                    jedis.eval(script, Collections.singletonList("lock"), Collections.singletonList(token));
                }
            }else {
                //没有获取到锁，睡眠一秒，然后自己调用自己，到缓存中继续查看
                try {
                    Thread.sleep(1000);
                    getProductByIdFromCache(productId);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            jedis.close();
            return product;
        }
    }

    //商品上架
    private void productPublish(List<Long> ids) {
        ids.forEach(id -> {
            //查询商品SPU参数
            Product product = getBaseMapper().selectById(id);
            //查询商品SKU参数
            List<SkuStock> skuStocks = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id",id));
            //查询商品筛选属性
            List<EsProductAttributeValue> list = productAttributeValueMapper.selectProductAttrValues(id);
            //统计上架状态是否全部成功上传到es
            AtomicReference<Integer> count = new AtomicReference<>(0);

            //遍历SKU参数并填充页面VO数据
            skuStocks.forEach(skuStock -> {
                //设置SPU参数
                ESProduct esProduct = new ESProduct();
                BeanUtils.copyProperties(product,esProduct);
                //设置SKU参数
                //改写id，使用sku的id
                esProduct.setId(skuStock.getId());
                //改写商品的标题，加上sku的销售属性
                esProduct.setName(product.getName() + " " +skuStock.getSp1() +" "+ skuStock.getSp2() + " " +skuStock.getSp3());
                esProduct.setPrice(skuStock.getPrice());//价格
                esProduct.setStock(skuStock.getStock());//库存
                esProduct.setSale(skuStock.getSale());//销量
                esProduct.setAttrValueList(list);//商品的筛选属性集合

                //保存到es
                boolean result = searchService.saveProductInfoToES(esProduct);
                //增加成功上传统计数
                if(result){
                    count.set(count.get() + 1);
                }
            });
            //统计成功数是否与上传次数相等
            if(count.get() == skuStocks.size()){
                //修改数据库状态
                Product pro = new Product();
                pro.setId(id);
                pro.setPublishStatus(1);
                getBaseMapper().updateById(pro);
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBaseProductInfo(PmsProductParam productParam){
        ProductServiceImpl psProxy = (ProductServiceImpl)AopContext.currentProxy();
        psProxy.saveProduct(productParam);
        psProxy.saveSkuStock(productParam.getSkuStockList());
    }

    //修改分类count

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProductCategoryCount() {
        Long productCategoryId = productThreadLocal.get().getProductCategoryId();
        ProductCategoryMapper.updateCountById(productCategoryId);
    }

    //保存商品参数及自定义规格属性
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductAttributeValue(List<ProductAttributeValue> productAttributeValueList) {
        productAttributeValueList.forEach(attributeValue -> {
            attributeValue.setProductId(productThreadLocal.get().getId());
            productAttributeValueMapper.insert(attributeValue);
        });
    }

    //保存商品会员价格
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveMemberPrice(List<MemberPrice> memberPriceList) {
        memberPriceList.forEach(memberPrice -> {
            memberPrice.setProductId(productThreadLocal.get().getId());
            memberPriceMapper.insert(memberPrice);
        });
    }

    //保存商品满减价格
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductFullReduction(List<ProductFullReduction> productFullReductionList) {
        productFullReductionList.forEach(fullReduction -> {
            fullReduction.setProductId(productThreadLocal.get().getId());
            productFullReductionMapper.insert(fullReduction);
        });
    }

    //保存商品阶梯价格
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveProductLadder(List<ProductLadder> productLadderList) {
        productLadderList.forEach(ladder -> {
            ladder.setProductId(productThreadLocal.get().getId());
            productLadderMapper.insert(ladder);
        });
    }

    //保存商品的sku库存信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSkuStock(List<SkuStock> skuStockList) {
        AtomicReference<Integer> i = new AtomicReference<>(0);
        skuStockList.forEach(stock -> {
            stock.setProductId(productThreadLocal.get().getId());
            // 0 代表前面补充0
            // 4 代表长度为4
            // d 代表参数为正数型
            String str = String.format("%04d", i.get());
            String code = "k_" + stock.getId() + "_" + str;
            stock.setSkuCode(code);
            i.set(i.get() + 1);
            skuStockMapper.insert(stock);
        });
    }

    //保存商品基本信息
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveProduct(PmsProductParam productParam){
        Product product = new Product();
        BeanUtils.copyProperties(productParam,product);
        getBaseMapper().insert(product);
        productThreadLocal.set(product);
    }
}
