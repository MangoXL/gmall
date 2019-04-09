package com.atguigu.gmall.portal.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.service.SearchService;
import com.atguigu.gmall.to.es.SearchParam;
import com.atguigu.gmall.to.es.SearchResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@CrossOrigin
@Api(tags = "SearchController",description = "商品检索管理")
public class SearchController {

    @Reference(version = "1.0")
    SearchService searchService;

    @ApiOperation("检索商品")
    @GetMapping("/search")
    public SearchResponse search(SearchParam searchParam) throws IOException {
        SearchResponse response = searchService.search(searchParam);
        return response;
    }
}
