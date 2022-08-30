package com.dudu.mall.service.impl;

import com.dudu.mall.mbg.mapper.UmsMemberLevelMapper;
import com.dudu.mall.mbg.model.UmsMemberLevel;
import com.dudu.mall.mbg.model.UmsMemberLevelExample;
import com.dudu.mall.service.UmsMemberLevelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 会员等级管理Service实现类
 */
@Service
public class UmsMemberLevelServiceImpl implements UmsMemberLevelService {
    @Resource
    private UmsMemberLevelMapper umsMemberLevelMapper;
    @Override
    public List<UmsMemberLevel> list(Integer defaultStatus) {
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        example.createCriteria().andDefaultStatusEqualTo(defaultStatus);
        return umsMemberLevelMapper.selectByExample(example);
    }
}
