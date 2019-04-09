package com.atguigu.gmall.pms.mapper;

import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品信息 Mapper 接口
 * </p>
 *
 * @author XiaoLe
 * @since 2019-03-19
 */
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 根据商品ID查询当前商品所有基本属性
     * @param productId
     * @return
     */
    List<EsProductAttributeValue> getProductBaseAttr(Long productId);

    /**
     * 根据商品ID查询当前商品所有销售属性
     * @param productId
     * @return
     */
    List<EsProductAttributeValue> getProductSaleAttr(Long productId);

    /**
     * 根据商品ID批量修改删除状态
     * @param ids
     * @param deleteStatus
     */
    void updateDeleteStatus(@Param("ids") List<Long> ids, @Param("deleteStatus") Integer deleteStatus);
}
