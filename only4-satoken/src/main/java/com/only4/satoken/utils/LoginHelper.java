package com.only4.satoken.utils;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.only4.common.entity.LoginUser;
import com.only4.common.entity.Permission;
import com.only4.common.entity.Role;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * @author LD_moxeii
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

    public static final String ADMIN_USER_KEY = "adminUser";

    public static final String USER_PERMISSIONS = "userPermissions";

    public static final String USER_ROLES = "userRoles";

    public static final String ADMIN_USER_ID = "adminUserId";

    public static void login(LoginUser loginUser) {
        login(loginUser, null);
    }

    public static void login(LoginUser loginUser, SaLoginModel model) {

        model = ObjectUtil.defaultIfNull(model, new SaLoginModel());
        StpUtil.login(loginUser.getId(),
                model.setExtra(ADMIN_USER_ID, loginUser.getId())
                        .setExtra(USER_ROLES, loginUser.getRoles())
                        .setExtra(USER_PERMISSIONS, loginUser.getPermissions())
        );
        StpUtil.getSession().set(ADMIN_USER_KEY, loginUser);
    }

    /**
     * 获取用户基于session
     */
    public static LoginUser getLoginUser() {
        SaSession session = StpUtil.getTokenSession();
        if (ObjectUtil.isNull(session)) {
            return null;
        }

        return (LoginUser)StpUtil.getSession().get(ADMIN_USER_KEY);
    }


    /**
     * 获取用户基于token
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static LoginUser getLoginUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (ObjectUtil.isNull(session)) {
            return null;
        }

        return (LoginUser)session.get(ADMIN_USER_KEY);
    }

    /**
     * 获取用户权限
     */
    public static List<Permission> getPermissions() {
        return Convert.toList(Permission.class,
                getExtra(USER_PERMISSIONS));
    }

    /**
     * 获取用户角色
     */
    public static List<Role> getRoles() {
        return Convert.toList(Role.class,
                getExtra(USER_ROLES));
    }

    /**
     * 获取用户id
     */
    public static Long getUserId() {
        return Convert.toLong(getExtra(ADMIN_USER_ID));
    }

    /**
     * 获取当前 Token 的扩展信息
     *
     * @param key 键值
     * @return 对应的扩展数据
     */
    private static Object getExtra(String key) {
        try {
            return StpUtil.getExtra(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return 结果
     */
    public static <T> boolean isLogin(Class<T> clazz) {
        try {
            return getUserId() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
