package com.atguigu.gmall.to.es;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchParam implements Serializable{
    //三级分类
    public String[] category3;
    //品牌
    public String[] brand;
    //关键字
    public String keyword;
    //排序规则：order=排序类型对应的ID：asc/desc
    public String order;//综合：1、销量:2、价格：3

    public Integer from;//价格区间查询的起始值
    public Integer to;//价格区间查询的结束值

    //页码
    public Integer pageNum;
    //每页显示几条数据
    public Integer pageSize = 12;
    //筛选属性：properties=属性ID:属性值，  如果前端想传入很多值： properties=属性ID:属性值-属性值-属性值
    public String[] properties;
}
