package com.atguigu.gmall.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.HashMap;

public class PageUtils {
    public static HashMap<String,Object> getPage(Page page){
        //创建页面需要的数据集合
        HashMap<String, Object> map = new HashMap<>();
        //填充数据到数据集合
        map.put("pageSize",page.getSize()); //每页记录数
        map.put("totalPage",page.getPages()); //总页数
        map.put("total",page.getTotal()); //总记录数
        map.put("pageNum",page.getCurrent()); //当前页
        map.put("list",page.getRecords());//数据列表
        return map;
    }
}
