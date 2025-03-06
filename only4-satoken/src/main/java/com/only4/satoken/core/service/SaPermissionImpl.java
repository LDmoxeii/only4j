package com.only4.satoken.core.service;

import cn.dev33.satoken.stp.StpInterface;
import com.only4.common.entity.Permission;
import com.only4.common.entity.Role;
import com.only4.satoken.utils.LoginHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * sa-token 权限管理实现类
 *
 * @author LD_moxeii
 */
public class SaPermissionImpl implements StpInterface {

    /**
     * 获取菜单权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return LoginHelper.getPermissions().stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 获取角色权限列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return LoginHelper.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
