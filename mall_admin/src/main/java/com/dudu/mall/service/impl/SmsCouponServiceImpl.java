package com.dudu.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.dudu.mall.dao.SmsCouponDao;
import com.dudu.mall.dao.SmsCouponProductCategoryRelationDao;
import com.dudu.mall.dao.SmsCouponProductRelationDao;
import com.dudu.mall.dto.SmsCouponParam;
import com.dudu.mall.mbg.mapper.SmsCouponMapper;
import com.dudu.mall.mbg.mapper.SmsCouponProductCategoryRelationMapper;
import com.dudu.mall.mbg.mapper.SmsCouponProductRelationMapper;
import com.dudu.mall.mbg.model.*;
import com.dudu.mall.service.SmsCouponService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 优惠券管理Service实现类
 */
@Service
public class SmsCouponServiceImpl implements SmsCouponService {
    @Resource
    private SmsCouponMapper couponMapper;
    @Resource
    private SmsCouponProductRelationMapper productRelationMapper;
    @Resource
    private SmsCouponProductCategoryRelationMapper productCategoryRelationMapper;
    @Resource
    private SmsCouponProductRelationDao productRelationDao;
    @Resource
    private SmsCouponProductCategoryRelationDao productCategoryRelationDao;
    @Resource
    private SmsCouponDao couponDao;
    @Override
    public int create(SmsCouponParam couponParam) {
        couponParam.setCount(couponParam.getPublishCount());
        couponParam.setUseCount(0);
        couponParam.setReceiveCount(0);
        //插入优惠券表
        int count = couponMapper.insert(couponParam);
        //插入优惠券和商品关系表
        if(couponParam.getUseType().equals(2)){
            for(SmsCouponProductRelation productRelation:couponParam.getProductRelationList()){
                productRelation.setCouponId(couponParam.getId());
            }
            productRelationDao.insertList(couponParam.getProductRelationList());
        }
        //插入优惠券和商品分类关系表
        if(couponParam.getUseType().equals(1)){
            for (SmsCouponProductCategoryRelation couponProductCategoryRelation : couponParam.getProductCategoryRelationList()) {
                couponProductCategoryRelation.setCouponId(couponParam.getId());
            }
            productCategoryRelationDao.insertList(couponParam.getProductCategoryRelationList());
        }
        return count;
    }

    @Override
    public int delete(Long id) {
        //删除优惠券
        int count = couponMapper.deleteByPrimaryKey(id);
        //删除商品关联
        deleteProductRelation(id);
        //删除商品分类关联
        deleteProductCategoryRelation(id);
        return count;
    }

    /**
     * 根据id删除优惠价与商品分类的关系
     * @param id
     */
    private void deleteProductCategoryRelation(Long id) {
        SmsCouponProductCategoryRelationExample productCategoryRelationExample = new SmsCouponProductCategoryRelationExample();
        productCategoryRelationExample.createCriteria().andCouponIdEqualTo(id);
        productCategoryRelationMapper.deleteByExample(productCategoryRelationExample);
    }

    /**
     * 根据Id删除优惠价与商品关系的数据
     * @param id
     */
    private void deleteProductRelation(Long id) {
        SmsCouponProductRelationExample productRelationExample = new SmsCouponProductRelationExample();
        productRelationExample.createCriteria().andCouponIdEqualTo(id);
        productRelationMapper.deleteByExample(productRelationExample);
    }

    @Override
    public int update(Long id, SmsCouponParam couponParam) {
        couponParam.setId(id);
        int count =couponMapper.updateByPrimaryKey(couponParam);
        //删除后插入优惠券和商品关系表
        if(couponParam.getUseType().equals(2)){
            for(SmsCouponProductRelation productRelation:couponParam.getProductRelationList()){
                productRelation.setCouponId(couponParam.getId());
            }
            deleteProductRelation(id);// 先删除优惠价与商品关系表的数据
            productRelationDao.insertList(couponParam.getProductRelationList());// 再插入新的关系
        }
        //删除后插入优惠券和商品分类关系表
        if(couponParam.getUseType().equals(1)){
            for (SmsCouponProductCategoryRelation couponProductCategoryRelation : couponParam.getProductCategoryRelationList()) {
                couponProductCategoryRelation.setCouponId(couponParam.getId());
            }
            deleteProductCategoryRelation(id);// 删除优惠价与商品分类关系表的数据
            productCategoryRelationDao.insertList(couponParam.getProductCategoryRelationList());// 再插入新的关系
        }
        return count;
    }

    @Override
    public List<SmsCoupon> list(String name, Integer type, Integer pageSize, Integer pageNum) {
        SmsCouponExample example = new SmsCouponExample();
        SmsCouponExample.Criteria criteria = example.createCriteria();
        if(!StrUtil.isEmpty(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if(type!=null){
            criteria.andTypeEqualTo(type);
        }
        PageHelper.startPage(pageNum,pageSize);
        return couponMapper.selectByExample(example);
    }

    @Override
    public SmsCouponParam getItem(Long id) {
        return couponDao.getItem(id);
    }
}
