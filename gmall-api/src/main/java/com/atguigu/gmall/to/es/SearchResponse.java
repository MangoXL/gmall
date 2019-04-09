package com.atguigu.gmall.to.es;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品检索的返回数据
 */
@Data
public class SearchResponse implements Serializable{
    //检索到的商品信息
    public List<ESProduct> esProducts = new ArrayList<>();
    //检索信息：所有商品的筛选属性
    public List<SearchParamVo> searchParamVos = new ArrayList<>();
    //品牌信息
    private SearchParamVo brand;
    //分类信息
    private SearchParamVo catelog;
    //分页信息
    public Long total;
    public Integer pageNum;
    public Integer pageSize;
}
