package com.atguigu.gmall.to.es;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SearchParamVo implements Serializable{
    //属性ID
    private Long productAttributeId;
    //当前属性的所有属性值
    private List<String> value = new ArrayList<>();//3G
    //属性名称
    private String name;//网络制式
}
