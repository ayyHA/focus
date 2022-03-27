package com.focus.focus.gateway.config.security;

import cn.hutool.core.util.ArrayUtil;
import com.focus.focus.gateway.exception.RequestAccessDeniedHandler;
import com.focus.focus.gateway.exception.RequestAuthenticationEntryPoint;
import com.focus.focus.gateway.filter.CorsFilter;
import com.focus.focus.gateway.model.SysParameterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

/**
 * 网关的OAuth2资源的配置类
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    /* JWT的鉴权管理器 */
    @Autowired
    private JwtAccessManager accessManager;

    /* token过期的异常处理 */
    @Autowired
    private RequestAuthenticationEntryPoint requestAuthenticationEntryPoint;

    /* 权限不足的异常处理 */
    @Autowired
    private RequestAccessDeniedHandler requestAccessDeniedHandler;

    /* 白名单 */
    @Autowired
    private SysParameterConfig sysConfig;

    /* token校验管理器 */
    @Autowired
    private ReactiveAuthenticationManager tokenAuthenticationManager;

    /* 跨域配置 */
    @Autowired
    private CorsFilter corsFilter;

    @Bean
    SecurityWebFilterChain webFluxSecurityFilterChain(ServerHttpSecurity http)throws Exception{
        // 认证过滤器中放入认证管理器
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(tokenAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());

        http
                .httpBasic().disable()
                .csrf().disable()
                .authorizeExchange()
                // 白名单直接放行
                .pathMatchers(ArrayUtil.toArray(sysConfig.getIgnoreUrls(),String.class)).permitAll()
                // 注册放行
                // 其他的请求必须鉴权
                .anyExchange().access(accessManager)
                // 鉴权异常处理
                .and().exceptionHandling()
                // token失效或未登录就访问别的资源
                .authenticationEntryPoint(requestAuthenticationEntryPoint)
                // 权限不足
                .accessDeniedHandler(requestAccessDeniedHandler)
                // 跨域过滤器
                .and().addFilterAt(corsFilter, SecurityWebFiltersOrder.CORS)
                // token的认证过滤器
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }
}
