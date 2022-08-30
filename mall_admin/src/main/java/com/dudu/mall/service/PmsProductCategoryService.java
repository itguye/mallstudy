package com.dudu.mall.service;

import com.dudu.mall.dto.PmsProductCategoryParam;
import com.dudu.mall.dto.PmsProductCategoryWithChildrenItem;
import com.dudu.mall.mbg.model.PmsProductCategory;

import java.util.List;

public interface PmsProductCategoryService {
    /**
     * 获取一级及子级菜单
     * @return
     */
    List<PmsProductCategoryWithChildrenItem> listWithChildren();

    /**
     * 获取商品分类,分页查询
     * @param parentId
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<PmsProductCategory> getList(Long parentId, Integer pageSize, Integer pageNum);

    /**
     * 添加商品分类
     * @param productCategoryParam
     * @return
     */
    int create(PmsProductCategoryParam productCategoryParam);

    /**
     * 修改导航栏状态
     * @param ids
     * @param navStatus
     * @return
     */
    int updateNavStatus(List<Long> ids, Integer navStatus);

    /**
     * 修改是否显示状态
     * @param ids
     * @param showStatus
     * @return
     */
    int updateShowStatus(List<Long> ids, Integer showStatus);

    /**
     * 根据ID获取商品分类
     * @param id
     * @return
     */
    PmsProductCategory getItem(Long id);

    /**
     * 修改商品信息
     * @param id
     * @param productCategoryParam
     * @return
     */
    int update(Long id, PmsProductCategoryParam productCategoryParam);

    /**
     * 根据ID删除商品分类信息
     * @param id
     * @return
     */
    int delete(Long id);
}
