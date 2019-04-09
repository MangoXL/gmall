package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Brand;
import com.atguigu.gmall.pms.vo.PmsBrandParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface BrandService extends IService<Brand> {

    /**
     * 根据编号查询品牌信息
     * @param id
     * @return
     */
    PmsBrandParam getItem(Long id);

    /**
     * 根据品牌名称分页获取品牌列表
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    HashMap<String,Object> getPageList(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 添加品牌
     * @param pmsBrand
     * @return
     */
    Integer create(PmsBrandParam pmsBrand);

    /**
     * 更新品牌
     * @param pmsBrandParam
     * @return
     */
    Integer updateBrand(Long id,PmsBrandParam pmsBrandParam);

    /**
     * 删除品牌
     * @param id
     * @return
     */
    Integer deleteBrand(Long id);

    /**
     * 批量更新显示状态
     * @param ids
     * @param showStatus
     * @return
     */
    Integer updateShowStatus(List<Long> ids, Integer showStatus);

    /**
     * 批量删除品牌
     * @param ids
     * @return
     */
    Integer deleteBatch(List<Long> ids);
}
