package com.focus.focus.gateway.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * JWT认证管理器
 * 主要作用：对携带过来的token进行校验（如：过期时间、加密方式等）
 * token校验通过，则会交给鉴权管理器进行鉴权
 */
@Component
@Slf4j
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    /* 使用JWT令牌进行令牌解析 */
    @Autowired
    private TokenStore tokenStore;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(a-> a instanceof BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class)
                .map(BearerTokenAuthenticationToken::getToken)
                .flatMap((accessToken)->{
                    OAuth2AccessToken oAuth2AccessToken = this.tokenStore.readAccessToken(accessToken);
                    // 根据accessToken获取不到OAuth2AccessToken
                    if(oAuth2AccessToken == null){
                        return Mono.error(new InvalidTokenException("无效的Token"));
                    }else if(oAuth2AccessToken.isExpired()){
                        return Mono.error(new InvalidTokenException("Token已过期"));
                    }
                    // 通过Authentication去认证token
                    OAuth2Authentication oAuth2Authentication = this.tokenStore.readAuthentication(accessToken);
                    if(oAuth2Authentication == null){
                        return Mono.error(new InvalidTokenException("无效的Token"));
                    }else{
                        return Mono.just(oAuth2Authentication);
                    }
                })
                .cast(Authentication.class);
    }
}
