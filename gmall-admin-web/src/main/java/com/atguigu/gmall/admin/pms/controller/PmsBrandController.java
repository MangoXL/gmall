package com.atguigu.gmall.admin.pms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.pms.service.BrandService;
import com.atguigu.gmall.pms.vo.PmsBrandParam;
import com.atguigu.gmall.to.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 品牌功能Controller
 */
@CrossOrigin
@RestController
@Api(tags = "PmsBrandController",description = "商品品牌管理")
@RequestMapping("/brand")
public class PmsBrandController {
    @Reference
    private BrandService brandService;

    @ApiOperation(value = "获取全部品牌列表")
    @GetMapping(value = "/listAll")
    public Object getList() {

        //TODO 获取全部品牌列表  brandService.listAll()
        return new CommonResult().success(null);
    }

    @ApiOperation(value = "添加品牌")
    @PostMapping(value = "/create")
    public Object create(@Validated @RequestBody PmsBrandParam pmsBrand, BindingResult result) {
        //添加品牌
        Integer data = brandService.create(pmsBrand);
        return new CommonResult().success(data);
    }

    @ApiOperation(value = "更新品牌")
    @PostMapping(value = "/update/{id}")
    public Object update(@PathVariable("id") Long id,
                              @Validated @RequestBody PmsBrandParam pmsBrandParam,
                              BindingResult result) {

        //更新品牌
        Integer data = brandService.updateBrand(id,pmsBrandParam);
        return new CommonResult().success(data);
    }

    @ApiOperation(value = "删除品牌")
    @GetMapping(value = "/delete/{id}")
    public Object delete(@PathVariable("id") Long id) {

        //删除品牌
        Integer data = brandService.deleteBrand(id);
        return new CommonResult().success(data);
    }

    @ApiOperation(value = "根据品牌名称分页获取品牌列表")
    @GetMapping(value = "/list")
    public Object getList(@RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        CommonResult commonResult = new CommonResult();

        //根据品牌名称分页获取品牌列表
        HashMap<String,Object> pageInfo = brandService.getPageList(keyword,pageNum,pageSize);

        return commonResult.success(pageInfo);
    }

    @ApiOperation(value = "根据编号查询品牌信息")
    @GetMapping(value = "/{id}")
    public Object getItem(@PathVariable("id") Long id) {
        CommonResult commonResult = new CommonResult();
        //根据编号查询品牌信息
        PmsBrandParam data = brandService.getItem(id);
        return commonResult.success(data);
    }

    @ApiOperation(value = "批量删除品牌")
    @PostMapping(value = "/delete/batch")
    public Object deleteBatch(@RequestParam("ids") List<Long> ids) {
        //批量删除品牌
        Integer data = brandService.deleteBatch(ids);
        return new CommonResult().success(data);
    }

    @ApiOperation(value = "批量更新显示状态")
    @PostMapping(value = "/update/showStatus")
    public Object updateShowStatus(@RequestParam("ids") List<Long> ids,
                                   @ApiParam(name = "showStatus", value = "0：隐藏品牌，1：显示品牌", required = true)
                                   @RequestParam("showStatus") Integer showStatus) {
        //TODO 批量更新显示状态
        Integer data = brandService.updateShowStatus(ids,showStatus);
        return new CommonResult().success(data);
    }

    @ApiOperation(value = "批量更新厂家制造商状态")
    @PostMapping(value = "/update/factoryStatus")
    public Object updateFactoryStatus(@RequestParam("ids") List<Long> ids,
                                      @RequestParam("factoryStatus") Integer factoryStatus) {
        CommonResult commonResult = new CommonResult();
        //TODO 批量更新厂家制造商状态


        return commonResult;
    }
}
