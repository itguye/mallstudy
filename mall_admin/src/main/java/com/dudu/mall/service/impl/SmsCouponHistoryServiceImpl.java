package com.dudu.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.dudu.mall.mbg.mapper.SmsCouponHistoryMapper;
import com.dudu.mall.mbg.model.SmsCouponHistory;
import com.dudu.mall.mbg.model.SmsCouponHistoryExample;
import com.dudu.mall.service.SmsCouponHistoryService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 优惠券领取记录管理Service实现类
 */
@Service
public class SmsCouponHistoryServiceImpl implements SmsCouponHistoryService {
    @Resource
    private SmsCouponHistoryMapper smsHistoryMapper;
    @Override
    public List<SmsCouponHistory> list(Long couponId, Integer useStatus, String orderSn, Integer pageSize, Integer pageNum) {
        // 设置分页查询
        PageHelper.startPage(pageNum,pageSize);
        // 设置查询条件
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        SmsCouponHistoryExample.Criteria criteria = example.createCriteria();
        if(couponId!=null){
            criteria.andCouponIdEqualTo(couponId);
        }
        if(useStatus!=null){
            criteria.andUseStatusEqualTo(useStatus);
        }
        if(!StrUtil.isEmpty(orderSn)){
            criteria.andOrderSnEqualTo(orderSn);
        }
        return smsHistoryMapper.selectByExample(example);
    }
}
