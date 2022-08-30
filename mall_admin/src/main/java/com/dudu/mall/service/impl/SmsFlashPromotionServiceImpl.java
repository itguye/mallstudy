package com.dudu.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.dudu.mall.mbg.mapper.SmsFlashPromotionMapper;
import com.dudu.mall.mbg.model.SmsFlashPromotion;
import com.dudu.mall.mbg.model.SmsFlashPromotionExample;
import com.dudu.mall.service.SmsFlashPromotionService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 限时购活动管理Service实现类
 */
@Service
public class SmsFlashPromotionServiceImpl implements SmsFlashPromotionService {
    @Resource
    private SmsFlashPromotionMapper smsFlashPromotionMapper;

    @Override
    public int create(SmsFlashPromotion flashPromotion) {
        flashPromotion.setCreateTime(new Date());
        return smsFlashPromotionMapper.insert(flashPromotion);
    }

    @Override
    public int update(Long id, SmsFlashPromotion flashPromotion) {
        flashPromotion.setId(id);
        return smsFlashPromotionMapper.updateByPrimaryKey(flashPromotion);
    }

    @Override
    public int delete(Long id) {
        return smsFlashPromotionMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        SmsFlashPromotion flashPromotion = new SmsFlashPromotion();
        flashPromotion.setId(id);
        flashPromotion.setStatus(status);
        return smsFlashPromotionMapper.updateByPrimaryKeySelective(flashPromotion);
    }

    @Override
    public SmsFlashPromotion getItem(Long id) {
        return smsFlashPromotionMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<SmsFlashPromotion> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        SmsFlashPromotionExample example = new SmsFlashPromotionExample();
        if (!StrUtil.isEmpty(keyword)) {
            example.createCriteria().andTitleLike("%" + keyword + "%");
        }
        return smsFlashPromotionMapper.selectByExample(example);
    }
}
