package com.dudu.mall.service.impl;


import com.dudu.mall.mbg.mapper.OmsCompanyAddressMapper;
import com.dudu.mall.mbg.model.OmsCompanyAddress;
import com.dudu.mall.mbg.model.OmsCompanyAddressExample;
import com.dudu.mall.service.OmsCompanyAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收货地址管理Service实现类
 */
@Service
public class OmsCompanyAddressServiceImpl implements OmsCompanyAddressService {
    @Resource
    private OmsCompanyAddressMapper companyAddressMapper;
    @Override
    public List<OmsCompanyAddress> list() {
        return companyAddressMapper.selectByExample(new OmsCompanyAddressExample());
    }
}
