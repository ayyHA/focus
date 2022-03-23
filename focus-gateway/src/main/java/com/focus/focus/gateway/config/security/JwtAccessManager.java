package com.focus.focus.gateway.config.security;

import cn.hutool.core.convert.Convert;
import com.focus.auth.common.model.SysConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

/**
 *  鉴权管理器
 *  认证成功后对用户进行鉴权
 *  从redis中获取对应的uri的权限(role)，与当前用户的token的携带的权限(role)进行对比，如果包含则鉴权成功
 */
@Slf4j
@Component
public class JwtAccessManager implements ReactiveAuthorizationManager<AuthorizationContext>{
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        // 从Redis中获取当前路径可访问的角色列表
        URI uri = authorizationContext.getExchange().getRequest().getURI();
        Object hashValue = redisTemplate.opsForHash().get(SysConstant.OAUTH_URLS, uri.getPath());
        List<String> authorities = Convert.toList(String.class, hashValue);
        // 认证成功且角色匹配则可访问当前路径
        return mono
                // 是否认证成功
                .filter(Authentication::isAuthenticated)
                // 获取认证后的全部权限（其实就是角色）
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
