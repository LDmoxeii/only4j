package com.only4.config;

import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.extra.spring.SpringUtil;
import com.only4.config.properties.SecurityProperties;
import com.only4.handler.AllUrlHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 权限安全配置
 *
 * @author LD_moxeii
 */

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final SecurityProperties securityProperties;

    /**
     * 注册sa-token的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaInterceptor(handler -> {
                AllUrlHandler allUrlHandler = SpringUtil.getBean(AllUrlHandler.class);
                // 登录验证 -- 排除多个路径
                    // 检查是否登录 是否有token
                    // 有效率影响 用于临时测试
                    // if (log.isDebugEnabled()) {
                    //     log.info("剩余有效时间: {}", StpUtil.getTokenTimeout());
                    //     log.info("临时有效时间: {}", StpUtil.getTokenActivityTimeout());
                    // }
                    SaRouter
                    // 获取所有的
                    .match(allUrlHandler.getUrls())
                    // 对未排除的路径进行检查
                    .check(StpUtil::checkLogin);
            })).addPathPatterns("/**")
            // 排除不需要拦截的路径
            .excludePathPatterns(securityProperties.getExcludes());
    }

    /**
     * 对 actuator 健康检查接口 做账号密码鉴权
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        String username = SpringUtil.getProperty("spring.boot.admin.client.username");
        String password = SpringUtil.getProperty("spring.boot.admin.client.password");
        return new SaServletFilter()
            .addInclude("/actuator", "/actuator/**")
            .setAuth(obj -> {
                SaHttpBasicUtil.check(username + ":" + password);
            })
            .setError(e -> SaResult.error(e.getMessage()).setCode(HttpStatus.UNAUTHORIZED.value()));
    }

}
