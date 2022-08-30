package com.dudu.mall.service;

import com.dudu.mall.dto.*;
import com.dudu.mall.mbg.model.OmsOrder;

import java.util.List;

public interface OmsOderService {
    /**
     * 获取订单列表
     * @param queryParam
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<OmsOrder> getOrderList(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum);

    /**
     * 根据订单id获取订单详情信息
     * @param id
     * @return
     */
    OmsOrderDetail detail(Long id);

    /**
     * 批量发货
     * @param deliveryParamList
     * @return
     */
    int delivery(List<OmsOrderDeliveryParam> deliveryParamList);

    /**
     * 批量关闭订单
     * @param ids
     * @param note
     * @return
     */
    int close(List<Long> ids, String note);

    /**
     * 批量删除订单
     * @param ids
     * @return
     */
    int delete(List<Long> ids);

    /**
     * 修改收货人信息
     * @param receiverInfoParam
     * @return
     */
    int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam);

    /**
     * 修改订单费用
     * @param moneyInfoParam
     * @return
     */
    int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam);

    /**
     * 备注订单信息
     * @param id
     * @param note
     * @param status
     * @return
     */
    int updateNote(Long id, String note, Integer status);
}
