package com.dudu.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.dudu.mall.dao.PmsSkuStockDao;
import com.dudu.mall.mbg.mapper.PmsSkuStockMapper;
import com.dudu.mall.mbg.model.PmsSkuStock;
import com.dudu.mall.mbg.model.PmsSkuStockExample;
import com.dudu.mall.service.PmsSkuStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品SKU库存管理Service实现类
 */
@Service
public class PmsSkuStockServiceImpl implements PmsSkuStockService {
    @Resource
    private PmsSkuStockMapper skuStockMapper;
    @Resource
    private PmsSkuStockDao skuStockDao;

    @Override
    public List<PmsSkuStock> getList(Long pid, String keyword) {
        PmsSkuStockExample example = new PmsSkuStockExample();
        PmsSkuStockExample.Criteria criteria = example.createCriteria().andProductIdEqualTo(pid);
        if (!StrUtil.isEmpty(keyword)) {
            criteria.andSkuCodeLike("%" + keyword + "%");
        }
        return skuStockMapper.selectByExample(example);
    }

    @Override
    public int update(Long pid, List<PmsSkuStock> skuStockList) {
        return skuStockDao.replaceList(skuStockList);
    }
}
