package com.dudu.mall.service.impl;

import com.dudu.mall.mbg.mapper.CmsPrefrenceAreaMapper;
import com.dudu.mall.mbg.model.CmsPrefrenceArea;
import com.dudu.mall.mbg.model.CmsPrefrenceAreaExample;
import com.dudu.mall.service.CmsPrefrenceAreaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品优选管理Service实现类
 * Created by macro on 2018/6/1.
 */
@Service
public class CmsPrefrenceAreaServiceImpl implements CmsPrefrenceAreaService {
    @Resource
    private CmsPrefrenceAreaMapper prefrenceAreaMapper;

    @Override
    public List<CmsPrefrenceArea> listAll() {
        return prefrenceAreaMapper.selectByExample(new CmsPrefrenceAreaExample());
    }
}
