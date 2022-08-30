package com.dudu.mall.service.impl;

import com.dudu.mall.mbg.mapper.UmsResourceCategoryMapper;
import com.dudu.mall.mbg.model.UmsResourceCategory;
import com.dudu.mall.mbg.model.UmsResourceCategoryExample;
import com.dudu.mall.service.UmsResourceCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UmsResourceCategoryServiceImpl implements UmsResourceCategoryService {
    @Resource
    private UmsResourceCategoryMapper umsResourceCategoryMapper;
    @Override
    public List<UmsResourceCategory> getListAll() {
        return umsResourceCategoryMapper.selectByExample(new UmsResourceCategoryExample());
    }

    @Override
    public int create(UmsResourceCategory umsResourceCategory) {
        umsResourceCategory.setCreateTime(new Date());
        return umsResourceCategoryMapper.insert(umsResourceCategory);
    }


    @Override
    public int update(Long id, UmsResourceCategory umsResourceCategory) {
        umsResourceCategory.setId(id);
        return umsResourceCategoryMapper.updateByPrimaryKeySelective(umsResourceCategory);
    }

    @Override
    public int delete(Long id) {
        return umsResourceCategoryMapper.deleteByPrimaryKey(id);
    }
}
