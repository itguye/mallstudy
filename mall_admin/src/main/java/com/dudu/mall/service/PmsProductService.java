package com.dudu.mall.service;

import com.dudu.mall.dto.PmsProductParam;
import com.dudu.mall.dto.PmsProductQueryParam;
import com.dudu.mall.dto.PmsProductResult;
import com.dudu.mall.mbg.model.PmsProduct;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PmsProductService {
    /**
     * 获取商品列表(分页查询)
     * @param productQueryParam
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<PmsProduct> getProductList(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum);

    /**
     * 批量修改商品状态
     * @param ids
     * @param publishStatus
     * @return
     */
    int updatePublishStatus(List<Long> ids, Integer publishStatus);

    /**
     * 批量修改商品推荐状态
     */
    int updateRecommendStatus(List<Long> ids, Integer recommendStatus);

    /**
     * 批量修改新品状态
     */
    int updateNewStatus(List<Long> ids, Integer newStatus);

    /**
     * 批量删除商品
     */
    int updateDeleteStatus(List<Long> ids, Integer deleteStatus);




    /**
     * 根据商品编号获取更新信息
     */
    PmsProductResult getUpdateInfo(Long id);

    /**
     * 更新商品
     */
    @Transactional
    int update(Long id, PmsProductParam productParam);

    /**
     * 创建商品
     * @param productParam
     * @return
     */
    int create(PmsProductParam productParam);

    /**
     * 批量修改审核状态
     * @param ids
     * @param verifyStatus
     * @param detail
     * @return
     */
    int updateVerifyStatus(List<Long> ids, Integer verifyStatus, String detail);

    /**
     * 根据商品名称或货号模糊查询
     * @param keyword
     * @return
     */
    List<PmsProduct> list(String keyword);
}
