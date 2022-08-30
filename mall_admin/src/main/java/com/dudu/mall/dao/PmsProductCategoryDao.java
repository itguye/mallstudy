package com.dudu.mall.dao;

import com.dudu.mall.dto.PmsProductCategoryWithChildrenItem;

import java.util.List;

/**
 * 自定义商品分类dao
 */
public interface PmsProductCategoryDao {
    /**
     * 获取商品分类及其子分类
     */
    List<PmsProductCategoryWithChildrenItem> listWithChildren();
}
