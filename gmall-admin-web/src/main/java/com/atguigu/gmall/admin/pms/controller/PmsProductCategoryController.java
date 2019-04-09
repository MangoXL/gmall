package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.atguigu.gmall.pms.vo.PmsProductCategoryParam;
import com.atguigu.gmall.pms.vo.PmsProductCategoryWithChildrenItem;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

/**
 * 商品分类模块Controller
 */
@CrossOrigin
@RestController
@Api(tags = "PmsProductCategoryController", description = "商品分类管理")
@RequestMapping("/productCategory")
public class PmsProductCategoryController {
    @Reference
    private ProductCategoryService productCategoryService;

    @ApiOperation("添加产品分类")
    @PostMapping(value = "/create")
    public Object create(@Validated @RequestBody PmsProductCategoryParam productCategoryParam,
                         BindingResult result) {
        //添加产品分类
        Integer data = productCategoryService.create(productCategoryParam);
        return new CommonResult().success(data);
    }

    @ApiOperation("修改商品分类")
    @PostMapping(value = "/update/{id}")
    public Object update(@PathVariable Long id,
                         @Valid
                         @RequestBody PmsProductCategoryParam productCategoryParam,
                         BindingResult result) {
        //TODO 修改商品分类
        Integer data = productCategoryService.updateProductCategoryById(id,productCategoryParam);
        return new CommonResult().success(data);
    }

    @ApiOperation("分页查询商品分类")
    @GetMapping(value = "/list/{parentId}")
    public Object getList(@PathVariable Long parentId,
                          @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        // 分页查询商品分类
        HashMap<String,Object> data = productCategoryService.getPageList(parentId,pageNum,pageSize);
        return new CommonResult().success(data);
    }

    @ApiOperation("根据id获取商品分类")
    @GetMapping(value = "/{id}")
    public Object getItem(@PathVariable Long id) {
        //根据id获取商品分类
        ProductCategory data = productCategoryService.getItem(id);
        return new CommonResult().success(data);
    }

    @ApiOperation("删除商品分类")
    @PostMapping(value = "/delete/{id}")
    public Object delete(@PathVariable Long id) {
        //删除商品分类
        Integer data = productCategoryService.deleteById(id);
        return new CommonResult().success(data);
    }

    @ApiOperation("修改导航栏显示状态")
    @PostMapping(value = "/update/navStatus")
    public Object updateNavStatus(@RequestParam("ids") List<Long> ids, @RequestParam("navStatus") Integer navStatus) {
        //TODO 修改导航栏显示状态
        return new CommonResult().success(null);
    }

    @ApiOperation("修改显示状态")
    @PostMapping(value = "/update/showStatus")
    public Object updateShowStatus(@RequestParam("ids") List<Long> ids, @RequestParam("showStatus") Integer showStatus) {
        //TODO 修改显示状态
        return new CommonResult().success(null);
    }

    @ApiOperation("查询所有一级分类及子分类[有难度]")
    @GetMapping(value = "/list/withChildren")
    public Object listWithChildren() {
        //查询所有一级分类及子分类
        List<PmsProductCategoryWithChildrenItem> data = productCategoryService.getListWithChildren();
        return new CommonResult().success(data);
    }
}
