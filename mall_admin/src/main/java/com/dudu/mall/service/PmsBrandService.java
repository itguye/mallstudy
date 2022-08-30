package com.dudu.mall.service;

import com.dudu.mall.dto.PmsBrandParam;
import com.dudu.mall.mbg.model.PmsBrand;

import java.util.List;

public interface PmsBrandService {
    /**
     * 获取商品品牌信息,分页查询
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<PmsBrand> getBrandList(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 批量修改品牌显示
     * @param ids
     * @param showStatus
     * @return
     */
    int updateShowStatus(List<Long> ids, Integer showStatus);

    /**
     * 批量修改品牌商状态
     * @param ids
     * @param factoryStatus
     * @return
     */
    int updateFactoryStatus(List<Long> ids, Integer factoryStatus);

    /**
     * 根据id删除商品品牌
     * @param id
     * @return
     */
    int deleteBrand(Long id);

    /**
     * 批量删除品牌
     */
    int deleteBrand(List<Long> ids);

    /**
     * 根据编号查询品牌信息
     * @param id
     * @return
     */
    PmsBrand getBrand(Long id);

    /**
     * 修改品牌信息
     * @param id
     * @param pmsBrandParam
     * @return
     */
    int updateBrand(Long id, PmsBrandParam pmsBrandParam);

    /**
     * 添加商品品牌
     * @param pmsBrand
     * @return
     */
    int createBrand(PmsBrandParam pmsBrand);

    /**
     * 获取所有商品品牌
     * @return
     */
    List<PmsBrand> listAllBrand();
}
