package com.dudu.mall.service;

import com.dudu.mall.dto.UmsAdminParam;
import com.dudu.mall.dto.UpdateAdminPasswordParam;
import com.dudu.mall.mbg.model.UmsAdmin;
import com.dudu.mall.mbg.model.UmsResource;
import com.dudu.mall.mbg.model.UmsRole;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UmsAdminService {
    /**
     * 用户注册
     * @param umsAdminParam
     * @return
     */
    UmsAdmin register(UmsAdminParam umsAdminParam);

    /**
     * 根据用户信息进行登入
     * @param username
     * @param password
     * @return
     */
    String login(String username, String password);

    /**
     * 根据用户id获取用户
     * @param id
     * @return
     */
    UmsAdmin getItem(Long id);

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    UmsAdmin getAdminByUserName(String username);

    /**
     * 根据umsAdmin获取用户角色列表
     * @param umsAdmin
     * @return
     */
    List<UmsRole> getRoleList(Long umsAdmin);

    /**
     * 获取分页列表
     * @param keyword
     * @param pageSize
     * @param pageNum
     * @return
     */
    List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 修改用户信息
     * @param id
     * @param admin
     * @return
     */
    int update(Long id, UmsAdmin admin);

    /**
     * 修改用户密码
     * @param updatePasswordParam
     * @return
     */
    int updatePassword(UpdateAdminPasswordParam updatePasswordParam);

    /**
     * 删除指定用户
     * @param id
     * @return
     */
    int delete(Long id);

    /**
     * 修改角色
     * @param adminId
     * @param roleIds
     * @return
     */
    int updateRole(Long adminId, List<Long> roleIds);

    /**
     * 刷新toke
     * @param token
     * @return
     */
    String refreshToken(String token);


    /**
     * 获取用户信息
     * @param username
     * @return
     */
    UserDetails loadUserByUsername(String username);

    /**
     * 获取缓存服务
     * @return
     */
    UmsAdminCacheService getCacheService();

    /**
     * 获取指定用户的可访问资源
     * @param adminId
     * @return
     */
    List<UmsResource> getResourceList(Long adminId);
}
