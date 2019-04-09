package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.ProductAttributeValue;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 存储产品参数信息的表 Mapper 接口
 * </p>
 *
 * @author XiaoLe
 * @since 2019-03-19
 */
public interface ProductAttributeValueMapper extends BaseMapper<ProductAttributeValue> {

    /**
     * 根据商品ID查询该商品所有筛选属性及所属分类
     * @param id
     */
    List<EsProductAttributeValue> selectProductAttrValues(Long id);
}
