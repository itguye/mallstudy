package com.dudu.mall.dao;

import com.dudu.mall.dto.OmsOrderDeliveryParam;
import com.dudu.mall.dto.OmsOrderDetail;
import com.dudu.mall.dto.OmsOrderQueryParam;
import com.dudu.mall.mbg.model.OmsOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单查询自定义Dao
 */
public interface OmsOrderDao {
    /**
     * 条件查询订单
     */
    List<OmsOrder> getList(@Param("queryParam") OmsOrderQueryParam queryParam);

    /**
     * 批量发货
     */
    int delivery(@Param("list") List<OmsOrderDeliveryParam> deliveryParamList);

    /**
     * 获取订单详情
     */
    OmsOrderDetail getDetail(@Param("id") Long id);
}
