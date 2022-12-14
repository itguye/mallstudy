package com.dudu.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dudu.mall.dao.*;
import com.dudu.mall.dto.PmsProductParam;
import com.dudu.mall.dto.PmsProductQueryParam;
import com.dudu.mall.dto.PmsProductResult;
import com.dudu.mall.mbg.mapper.*;
import com.dudu.mall.mbg.model.*;
import com.dudu.mall.service.PmsProductService;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PmsProductServiceImpl implements PmsProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsProductServiceImpl.class);
    @Resource
    private PmsProductMapper pmsProductMapper;
    @Resource
    private PmsMemberPriceMapper pmsMemberPriceMapper;
    @Resource
    private PmsProductLadderMapper pmsProductLadderMapper;
    @Resource
    private PmsProductFullReductionMapper pmsProductFullReductionMapper;
    @Resource
    private PmsProductAttributeValueMapper pmsProductAttributeValueMapper;
    @Resource
    private CmsSubjectProductRelationMapper cmsSubjectProductRelationMapper;
    @Resource
    private CmsPrefrenceAreaProductRelationMapper cmsPrefrenceAreaProductRelationMapper;
    @Resource
    private PmsSkuStockMapper pmsSkuStockMapper;


    @Resource
    private PmsProductDao productDao;
    @Resource
    private PmsSkuStockDao skuStockDao;
    @Resource
    private PmsProductAttributeValueDao productAttributeValueDao;
    @Resource
    private PmsMemberPriceDao memberPriceDao;
    @Resource
    private PmsProductLadderDao productLadderDao;
    @Resource
    private PmsProductFullReductionDao productFullReductionDao;
    @Resource
    private CmsSubjectProductRelationDao subjectProductRelationDao;
    @Resource
    private CmsPrefrenceAreaProductRelationDao prefrenceAreaProductRelationDao;

    @Resource
    private PmsProductVertifyRecordDao productVertifyRecordDao;


    @Override
    public List<PmsProduct> getProductList(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum) {
        // ????????????
        PageHelper.startPage(pageNum, pageSize);
        PmsProductExample pmsProductExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = pmsProductExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        if (productQueryParam.getPublishStatus() != null) {
            criteria.andPublishStatusEqualTo(productQueryParam.getPublishStatus());
        }
        if (productQueryParam.getVerifyStatus() != null) {
            criteria.andVerifyStatusEqualTo(productQueryParam.getVerifyStatus());
        }
        // ????????????
        if (!StrUtil.isEmpty(productQueryParam.getKeyword())) {
            criteria.andNameLike("%" + productQueryParam.getKeyword() + "%");
        }
        if (!StrUtil.isEmpty(productQueryParam.getProductSn())) {
            criteria.andProductSnEqualTo(productQueryParam.getProductSn());
        }
        if (productQueryParam.getBrandId() != null) {
            criteria.andBrandIdEqualTo(productQueryParam.getBrandId());
        }
        if (productQueryParam.getProductCategoryId() != null) {
            criteria.andProductCategoryIdEqualTo(productQueryParam.getProductCategoryId());
        }

        return pmsProductMapper.selectByExample(pmsProductExample);
    }

    @Override
    public int updatePublishStatus(List<Long> ids, Integer publishStatus) {
        // ????????????????????????
        PmsProduct pmsProduct = new PmsProduct();
        pmsProduct.setPublishStatus(publishStatus);

        PmsProductExample pmsProductExample = new PmsProductExample();
        pmsProductExample.createCriteria().andIdIn(ids);
        return pmsProductMapper.updateByExampleSelective(pmsProduct,pmsProductExample);
    }


    @Override
    public int updateRecommendStatus(List<Long> ids, Integer recommendStatus) {
        PmsProduct record = new PmsProduct();
        record.setRecommandStatus(recommendStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return pmsProductMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateNewStatus(List<Long> ids, Integer newStatus) {
        PmsProduct record = new PmsProduct();
        record.setNewStatus(newStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return pmsProductMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateDeleteStatus(List<Long> ids, Integer deleteStatus) {
        PmsProduct record = new PmsProduct();
        record.setDeleteStatus(deleteStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);
        return pmsProductMapper.updateByExampleSelective(record, example);
    }



    @Override
    public PmsProductResult getUpdateInfo(Long id) {
        return productDao.getUpdateInfo(id);
    }


    @Override
    public int update(Long id, PmsProductParam productParam) {
        int count;
        //??????????????????
        PmsProduct product = productParam;
        product.setId(id);
        pmsProductMapper.updateByPrimaryKeySelective(product);
        //????????????
        PmsMemberPriceExample pmsMemberPriceExample = new PmsMemberPriceExample();
        pmsMemberPriceExample.createCriteria().andProductIdEqualTo(id);
        pmsMemberPriceMapper.deleteByExample(pmsMemberPriceExample);
        relateAndInsertList(memberPriceDao, productParam.getMemberPriceList(), id);
        //????????????
        PmsProductLadderExample ladderExample = new PmsProductLadderExample();
        ladderExample.createCriteria().andProductIdEqualTo(id);
        pmsProductLadderMapper.deleteByExample(ladderExample);
        relateAndInsertList(productLadderDao, productParam.getProductLadderList(), id);
        //????????????
        PmsProductFullReductionExample fullReductionExample = new PmsProductFullReductionExample();
        fullReductionExample.createCriteria().andProductIdEqualTo(id);
        pmsProductFullReductionMapper.deleteByExample(fullReductionExample);
        relateAndInsertList(productFullReductionDao, productParam.getProductFullReductionList(), id);
        //??????sku????????????
        handleUpdateSkuStockList(id, productParam);
        //??????????????????,???????????????????????????
        PmsProductAttributeValueExample productAttributeValueExample = new PmsProductAttributeValueExample();
        productAttributeValueExample.createCriteria().andProductIdEqualTo(id);
        pmsProductAttributeValueMapper.deleteByExample(productAttributeValueExample);
        relateAndInsertList(productAttributeValueDao, productParam.getProductAttributeValueList(), id);
        //????????????
        CmsSubjectProductRelationExample subjectProductRelationExample = new CmsSubjectProductRelationExample();
        subjectProductRelationExample.createCriteria().andProductIdEqualTo(id);
        cmsSubjectProductRelationMapper.deleteByExample(subjectProductRelationExample);
        relateAndInsertList(subjectProductRelationDao, productParam.getSubjectProductRelationList(), id);
        //????????????
        CmsPrefrenceAreaProductRelationExample prefrenceAreaExample = new CmsPrefrenceAreaProductRelationExample();
        prefrenceAreaExample.createCriteria().andProductIdEqualTo(id);
        cmsPrefrenceAreaProductRelationMapper.deleteByExample(prefrenceAreaExample);
        relateAndInsertList(prefrenceAreaProductRelationDao, productParam.getPrefrenceAreaProductRelationList(), id);
        count = 1;
        return count;
    }

    @Override
    public int create(PmsProductParam productParam) {
        int count;
        //????????????
        PmsProduct product = productParam;
        product.setId(null);
        pmsProductMapper.insertSelective(product);
        //???????????????????????????????????????????????????????????????????????????
        Long productId = product.getId();
        //????????????
        relateAndInsertList(memberPriceDao, productParam.getMemberPriceList(), productId);
        //????????????
        relateAndInsertList(productLadderDao, productParam.getProductLadderList(), productId);
        //????????????
        relateAndInsertList(productFullReductionDao, productParam.getProductFullReductionList(), productId);
        //??????sku?????????
        handleSkuStockCode(productParam.getSkuStockList(),productId);
        //??????sku????????????
        relateAndInsertList(skuStockDao, productParam.getSkuStockList(), productId);
        //??????????????????,???????????????????????????
        relateAndInsertList(productAttributeValueDao, productParam.getProductAttributeValueList(), productId);
        //????????????
        relateAndInsertList(subjectProductRelationDao, productParam.getSubjectProductRelationList(), productId);
        //????????????
        relateAndInsertList(prefrenceAreaProductRelationDao, productParam.getPrefrenceAreaProductRelationList(), productId);
        count = 1;
        return count;
    }


    /**
     * ??????????????????????????????
     *
     * @param dao       ???????????????dao
     * @param dataList  ??????????????????
     * @param productId ???????????????id
     */
    private void relateAndInsertList(Object dao, List dataList, Long productId) {
        try {
            if (CollectionUtils.isEmpty(dataList)) return;
            for (Object item : dataList) {
                Method setId = item.getClass().getMethod("setId", Long.class);
                setId.invoke(item, (Long) null);
                Method setProductId = item.getClass().getMethod("setProductId", Long.class);
                setProductId.invoke(item, productId);
            }
            // ????????????????????????insertList
            Method insertList = dao.getClass().getMethod("insertList", List.class);
            insertList.invoke(dao, dataList);
        } catch (Exception e) {
            LOGGER.warn("??????????????????:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleUpdateSkuStockList(Long id, PmsProductParam productParam) {
        //?????????sku??????
        List<PmsSkuStock> currSkuList = productParam.getSkuStockList();
        //????????????sku????????????
        if(CollUtil.isEmpty(currSkuList)){
            PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
            skuStockExample.createCriteria().andProductIdEqualTo(id);
            pmsSkuStockMapper.deleteByExample(skuStockExample);
            return;
        }
        //????????????sku??????
        PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
        skuStockExample.createCriteria().andProductIdEqualTo(id);
        List<PmsSkuStock> oriStuList = pmsSkuStockMapper.selectByExample(skuStockExample);
        //????????????sku??????
        List<PmsSkuStock> insertSkuList = currSkuList.stream().filter(item->item.getId()==null).collect(Collectors.toList());
        //?????????????????????sku??????
        List<PmsSkuStock> updateSkuList = currSkuList.stream().filter(item->item.getId()!=null).collect(Collectors.toList());
        List<Long> updateSkuIds = updateSkuList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
        //?????????????????????sku??????
        List<PmsSkuStock> removeSkuList = oriStuList.stream().filter(item-> !updateSkuIds.contains(item.getId())).collect(Collectors.toList());
        handleSkuStockCode(insertSkuList,id);
        handleSkuStockCode(updateSkuList,id);
        //??????sku
        if(CollUtil.isNotEmpty(insertSkuList)){
            relateAndInsertList(skuStockDao, insertSkuList, id);
        }
        //??????sku
        if(CollUtil.isNotEmpty(removeSkuList)){
            List<Long> removeSkuIds = removeSkuList.stream().map(PmsSkuStock::getId).collect(Collectors.toList());
            PmsSkuStockExample removeExample = new PmsSkuStockExample();
            removeExample.createCriteria().andIdIn(removeSkuIds);
            pmsSkuStockMapper.deleteByExample(removeExample);
        }
        //??????sku
        if(CollUtil.isNotEmpty(updateSkuList)){
            for (PmsSkuStock pmsSkuStock : updateSkuList) {
                pmsSkuStockMapper.updateByPrimaryKeySelective(pmsSkuStock);
            }
        }

    }


    private void handleSkuStockCode(List<PmsSkuStock> skuStockList, Long productId) {
        if(CollectionUtils.isEmpty(skuStockList))return;
        for(int i=0;i<skuStockList.size();i++){
            PmsSkuStock skuStock = skuStockList.get(i);
            if(StrUtil.isEmpty(skuStock.getSkuCode())){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                StringBuilder sb = new StringBuilder();
                //??????
                sb.append(sdf.format(new Date()));
                //????????????id
                sb.append(String.format("%04d", productId));
                //????????????id
                sb.append(String.format("%03d", i+1));
                skuStock.setSkuCode(sb.toString());
            }
        }
    }


    @Override
    public int updateVerifyStatus(List<Long> ids, Integer verifyStatus, String detail) {
        // ????????????????????????
        PmsProduct product = new PmsProduct();
        product.setVerifyStatus(verifyStatus);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andIdIn(ids);

        List<PmsProductVertifyRecord> list = new ArrayList<>();
        int count = pmsProductMapper.updateByExampleSelective(product, example);
        //??????????????????????????????????????????
        for (Long id : ids) {
            PmsProductVertifyRecord record = new PmsProductVertifyRecord();
            record.setProductId(id);
            record.setCreateTime(new Date());
            record.setDetail(detail);
            record.setStatus(verifyStatus);
            record.setVertifyMan("test");
            list.add(record);
        }
        productVertifyRecordDao.insertList(list);
        return count;
    }

    @Override
    public List<PmsProduct> list(String keyword) {
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        if(!StrUtil.isEmpty(keyword)){
            criteria.andNameLike("%" + keyword + "%");
            productExample.or().andDeleteStatusEqualTo(0).andProductSnLike("%" + keyword + "%");
        }
        return pmsProductMapper.selectByExample(productExample);
    }
}
