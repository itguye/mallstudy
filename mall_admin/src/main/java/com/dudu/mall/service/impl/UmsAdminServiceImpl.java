package com.dudu.mall.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.dudu.mall.bo.AdminUserDetails;
import com.dudu.mall.common.utils.RequestUtil;
import com.dudu.mall.dao.UmsAdminRoleRelationDao;
import com.dudu.mall.dto.UmsAdminParam;
import com.dudu.mall.dto.UpdateAdminPasswordParam;
import com.dudu.mall.mbg.mapper.UmsAdminLoginLogMapper;
import com.dudu.mall.mbg.mapper.UmsAdminMapper;
import com.dudu.mall.mbg.mapper.UmsAdminRoleRelationMapper;
import com.dudu.mall.mbg.model.*;
import com.dudu.mall.service.UmsAdminCacheService;
import com.dudu.mall.service.UmsAdminService;
import com.dudu.mall.service.UmsRoleService;
import com.dudu.mall.common.exception.*;
import com.dudu.mall.utils.JwtTokenUtil;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class UmsAdminServiceImpl implements UmsAdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsAdminServiceImpl.class);
    @Resource
    private JwtTokenUtil tokenUtil;
    @Resource
    private UmsAdminMapper umsAdminMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UmsAdminRoleRelationDao adminRoleRelationDao;
    @Resource
    private UmsAdminLoginLogMapper umsAdminLoginLogMapper;
    @Resource
    private UmsAdminRoleRelationMapper umsAdminRoleRelationMapper;

    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        // ?????????UmsAdmin??????
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam, umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);

        // ??????UmsAdmin??????
        //???????????????????????????????????????
        UmsAdminExample example = new UmsAdminExample();// ??????
        example.createCriteria().andUsernameEqualTo(umsAdmin.getUsername());
        List<UmsAdmin> umsAdmins = umsAdminMapper.selectByExample(example);
        if (umsAdmins.size() > 0) {
            return null;// ???????????????????????????????????????
        }

        // ???????????????,???????????????????????????????????????
        String encodePassword = passwordEncoder.encode(umsAdmin.getPassword());
        umsAdmin.setPassword(encodePassword);
        umsAdminMapper.insert(umsAdmin);
        return umsAdmin;
    }
    @Override
    public String login(String username, String password) {
        // Token??????
        String token = null;
        try {
            UserDetails userDetails = loadUserByUsername(username);
            // ??????????????????
            if (passwordEncoder.matches(passwordEncoder.encode(password),userDetails.getPassword())) {
                Asserts.fail("???????????????");
            }

            // ???????????????????????????
            if (!userDetails.isEnabled()) {
                Asserts.fail("??????????????????");
            }

            // ?????????????????????UsernamePasswordAuthenticationToken
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            // ??????Toke
            token = tokenUtil.generateToken(userDetails);
            // ??????????????????
            insertLoginLog(username);
            return token;
        } catch (AuthenticationException e) {
            LOGGER.warn("????????????:{}", e.getMessage());
        }

        return null;
    }
    @Override
    public UmsAdmin getItem(Long id) {
        return umsAdminMapper.selectByPrimaryKey(id);
    }
    @Override
    public UmsAdmin getAdminByUserName(String username) {
        // ??????????????????
        UmsAdmin umsAdmin = getCacheService().getAdmin(username);
        if (umsAdmin != null) {
            return umsAdmin;
        }

        // ?????????????????????
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsAdmin> umsAdmins = umsAdminMapper.selectByExample(example);
        if (umsAdmins != null && umsAdmins.size() > 0) {
            umsAdmin = umsAdmins.get(0);
            getCacheService().setAdmin(umsAdmin);// ???????????????????????????
            return umsAdmin;
        }
        return null;
    }
    @Override
    public List<UmsRole> getRoleList(Long adminId) {
        return adminRoleRelationDao.getRoleList(adminId);
    }
    @Override
    public List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsAdminExample example = new UmsAdminExample();
        UmsAdminExample.Criteria criteria = example.createCriteria();
        if (!StrUtil.isEmpty(keyword)) {
            criteria.andUsernameLike("%" + keyword + "%");
            example.or(example.createCriteria().andNickNameLike("%" + keyword + "%"));
        }
        return umsAdminMapper.selectByExample(example);
    }
    @Override
    public int update(Long id, UmsAdmin admin) {
        admin.setId(id);
        UmsAdmin rawAdmin = umsAdminMapper.selectByPrimaryKey(id);
        if(rawAdmin.getPassword().equals(admin.getPassword())){
            //??????????????????????????????????????????
            admin.setPassword(null);
        }else{
            //?????????????????????????????????????????????
            if(StrUtil.isEmpty(admin.getPassword())){
                admin.setPassword(null);
            }else{
                admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            }
        }
        int count = umsAdminMapper.updateByPrimaryKeySelective(admin);
        getCacheService().delAdmin(id);
        return count;
    }
    @Override
    public int updatePassword(UpdateAdminPasswordParam param) {
        if(StrUtil.isEmpty(param.getUsername())
                ||StrUtil.isEmpty(param.getOldPassword())
                ||StrUtil.isEmpty(param.getNewPassword())){
            return -1;
        }
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(param.getUsername());
        List<UmsAdmin> adminList = umsAdminMapper.selectByExample(example);
        if(CollUtil.isEmpty(adminList)){
            return -2;
        }
        UmsAdmin umsAdmin = adminList.get(0);
        if(!passwordEncoder.matches(param.getOldPassword(),umsAdmin.getPassword())){
            return -3;
        }
        // ??????
        umsAdmin.setPassword(passwordEncoder.encode(param.getNewPassword()));
        umsAdminMapper.updateByPrimaryKey(umsAdmin);
        getCacheService().delAdmin(umsAdmin.getId());
        return 1;
    }
    @Override
    public int delete(Long id) {
        getCacheService().delAdmin(id);
        int count = umsAdminMapper.deleteByPrimaryKey(id);
        getCacheService().delResourceList(id);
        return count;
    }
    @Override
    public int updateRole(Long adminId, List<Long> roleIds) {
        int count = roleIds == null ? 0 : roleIds.size();
        //????????????????????????
        UmsAdminRoleRelationExample adminRoleRelationExample = new UmsAdminRoleRelationExample();
        adminRoleRelationExample.createCriteria().andAdminIdEqualTo(adminId);
        umsAdminRoleRelationMapper.deleteByExample(adminRoleRelationExample);
        //???????????????
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<UmsAdminRoleRelation> list = new ArrayList<>();
            for (Long roleId : roleIds) {
                UmsAdminRoleRelation roleRelation = new UmsAdminRoleRelation();
                roleRelation.setAdminId(adminId);
                roleRelation.setRoleId(roleId);
                list.add(roleRelation);
            }
            adminRoleRelationDao.insertList(list);
        }
        getCacheService().delResourceList(adminId);
        return count;
    }
    @Override
    public String refreshToken(String oldToken) {
        return tokenUtil.refreshHeadToken(oldToken);
    }
    // ?????????????????????????????????
    @Override
    public UserDetails loadUserByUsername(String username) {
        // ??????????????????
        UmsAdmin umsAdmin = getAdminByUserName(username);
        if (umsAdmin != null) {
            // ??????????????????
            List<UmsResource> umsRoleServices = getResourceList(umsAdmin.getId());
            return new AdminUserDetails(umsAdmin, umsRoleServices);
        }
        throw new UsernameNotFoundException("????????????????????????");
    }
    // ????????????Id????????????????????????????????????
    @Override
    public List<UmsResource> getResourceList(Long id) {
        List<UmsResource> umsResourceList = getCacheService().getResourceList(id);// ??????????????????
        if (CollUtil.isNotEmpty(umsResourceList)) {
            return umsResourceList;
        }

        // ?????????????????????
        umsResourceList = adminRoleRelationDao.getResourceList(id);
        if (CollUtil.isNotEmpty(umsResourceList)) {
            // ??????????????????????????????
            getCacheService().setResourceList(id,umsResourceList);
        }
        return umsResourceList;
    }
    // ????????????
    @Override
    public UmsAdminCacheService getCacheService() {
        return SpringUtil.getBean(UmsAdminCacheService.class);
    }
    // ??????????????????????????????
    private void insertLoginLog(String username) {
        // ????????????????????????????????????
        UmsAdmin umsAdmin = getAdminByUserName(username);
        if (umsAdmin == null) {
            return;
        }

        // ????????????,????????????????????????
        UmsAdminLoginLog loginLog = new UmsAdminLoginLog();
        loginLog.setAdminId(umsAdmin.getId());
        loginLog.setCreateTime(new Date());
        // ??????IP????????????
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        loginLog.setIp(RequestUtil.getRequestIp(request));

        // ??????????????????????????????
        umsAdminLoginLogMapper.insert(loginLog);
    }
}
