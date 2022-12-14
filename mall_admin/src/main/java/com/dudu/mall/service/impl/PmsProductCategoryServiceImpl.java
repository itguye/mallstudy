package com.dudu.mall.service.impl;

import com.dudu.mall.dao.PmsProductCategoryAttributeRelationDao;
import com.dudu.mall.dao.PmsProductCategoryDao;
import com.dudu.mall.dto.PmsProductCategoryParam;
import com.dudu.mall.dto.PmsProductCategoryWithChildrenItem;
import com.dudu.mall.mbg.mapper.PmsProductCategoryAttributeRelationMapper;
import com.dudu.mall.mbg.mapper.PmsProductCategoryMapper;
import com.dudu.mall.mbg.mapper.PmsProductMapper;
import com.dudu.mall.mbg.model.*;
import com.dudu.mall.service.PmsProductCategoryService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PmsProductCategoryServiceImpl implements PmsProductCategoryService {
    @Resource
    private PmsProductCategoryMapper pmsProductCategoryMapper;
    @Resource
    private PmsProductCategoryDao pmsProductCategoryDao;
    @Resource
    private PmsProductCategoryAttributeRelationDao productCategoryAttributeRelationDao;
    @Resource
    private PmsProductMapper pmsProductMapper;
    @Resource
    private PmsProductCategoryAttributeRelationMapper pmsProductCategoryAttributeRelationMapper;
    @Override
    public List<PmsProductCategoryWithChildrenItem> listWithChildren() {
        return pmsProductCategoryDao.listWithChildren();
    }

    @Override
    public List<PmsProductCategory> getList(Long parentId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.setOrderByClause("sort desc");
        example.createCriteria().andParentIdEqualTo(parentId);
        return pmsProductCategoryMapper.selectByExample(example);
    }


    @Override
    public int create(PmsProductCategoryParam pmsProductCategoryParam) {
        PmsProductCategory productCategory = new PmsProductCategory();
        productCategory.setProductCount(0);
        BeanUtils.copyProperties(pmsProductCategoryParam, productCategory);
        //?????????????????????????????????
        setCategoryLevel(productCategory);
        int count = pmsProductCategoryMapper.insertSelective(productCategory);
        //????????????????????????
        List<Long> productAttributeIdList = pmsProductCategoryParam.getProductAttributeIdList();
        if(!CollectionUtils.isEmpty(productAttributeIdList)){
            insertRelationList(productCategory.getId(), productAttributeIdList);
        }
        return count;
    }

    /**
     * ???????????????parentId???????????????level
     */
    private void setCategoryLevel(PmsProductCategory productCategory) {
        //?????????????????????????????????
        if (productCategory.getParentId() == 0) {
            productCategory.setLevel(0);
        } else {
            //????????????????????????????????????level??????
            PmsProductCategory parentCategory = pmsProductCategoryMapper.selectByPrimaryKey(productCategory.getParentId());
            if (parentCategory != null) {
                productCategory.setLevel(parentCategory.getLevel() + 1);
            } else {
                productCategory.setLevel(0);
            }
        }
    }


    /**
     * ????????????????????????????????????????????????
     * @param productCategoryId ????????????id
     * @param productAttributeIdList ????????????????????????id??????
     */
    private void insertRelationList(Long productCategoryId, List<Long> productAttributeIdList) {
        List<PmsProductCategoryAttributeRelation> relationList = new ArrayList<>();
        for (Long productAttrId : productAttributeIdList) {
            PmsProductCategoryAttributeRelation relation = new PmsProductCategoryAttributeRelation();
            relation.setProductAttributeId(productAttrId);
            relation.setProductCategoryId(productCategoryId);
            relationList.add(relation);
        }
        productCategoryAttributeRelationDao.insertList(relationList);
    }


    @Override
    public int updateNavStatus(List<Long> ids, Integer navStatus) {
        PmsProductCategory productCategory = new PmsProductCategory();
        productCategory.setNavStatus(navStatus);
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.createCriteria().andIdIn(ids);
        return pmsProductCategoryMapper.updateByExampleSelective(productCategory, example);
    }

    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        PmsProductCategory productCategory = new PmsProductCategory();
        productCategory.setShowStatus(showStatus);
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.createCriteria().andIdIn(ids);
        return pmsProductCategoryMapper.updateByExampleSelective(productCategory, example);
    }


    @Override
    public PmsProductCategory getItem(Long id) {
        return pmsProductCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Long id, PmsProductCategoryParam pmsProductCategoryParam) {
        // ????????????????????????
        PmsProductCategory productCategory = new PmsProductCategory();
        productCategory.setId(id);
        BeanUtils.copyProperties(pmsProductCategoryParam, productCategory);
        setCategoryLevel(productCategory);

        //????????????????????????????????????????????????
        // ????????????
        PmsProduct product = new PmsProduct();
        product.setProductCategoryName(productCategory.getName());
        // ??????
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andProductCategoryIdEqualTo(id);
        // ??????
        pmsProductMapper.updateByExampleSelective(product,example);

        //?????????????????????????????????
        if(!CollectionUtils.isEmpty(pmsProductCategoryParam.getProductAttributeIdList())){
            PmsProductCategoryAttributeRelationExample relationExample = new PmsProductCategoryAttributeRelationExample();
            relationExample.createCriteria().andProductCategoryIdEqualTo(id);
            pmsProductCategoryAttributeRelationMapper.deleteByExample(relationExample);// ?????????????????????????????????????????????
            insertRelationList(id,pmsProductCategoryParam.getProductAttributeIdList());// ??????????????????
        }else{
            // ???????????????????????????????????????,??????????????????
            PmsProductCategoryAttributeRelationExample relationExample = new PmsProductCategoryAttributeRelationExample();
            relationExample.createCriteria().andProductCategoryIdEqualTo(id);
            pmsProductCategoryAttributeRelationMapper.deleteByExample(relationExample);
        }
        return pmsProductCategoryMapper.updateByPrimaryKeySelective(productCategory);// ????????????????????????
    }


    @Override
    public int delete(Long id) {
        return pmsProductCategoryMapper.deleteByPrimaryKey(id);
    }
}
