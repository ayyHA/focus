package com.focus.focus.gateway.filter;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.focus.auth.common.model.ResultCode;
import com.focus.auth.common.model.ResultMsg;
import com.focus.auth.common.model.TokenConstant;
import com.focus.focus.gateway.model.SysParameterConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 全局过滤器 order为0，作用先于DefaultFilter和路由过滤器
 * 用于：1、放行白名单；2、检查token；3、为下游服务暴露出用户信息
 */
@Component
@Slf4j
public class GlobalAuthenticationFilter implements GlobalFilter, Ordered {
    // 令牌存储策略
    @Autowired
    private TokenStore tokenStore;
    // 系统白名单
    @Autowired
    private SysParameterConfig sysParameterConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().value();
        // 白名单放行，如：授权服务、静态资源
        if(checkUrls(sysParameterConfig.getIgnoreUrls(),requestUrl)){
            log.info("URL:{}被放行了",requestUrl);
            return chain.filter(exchange);
        }

        // 检查token是否存在
        String token = getToken(exchange);
        if(StringUtils.isBlank(token)){
            log.info("TOKEN不存在");
            return invalidTokenMono(exchange);
        }

        // 判断是否有效的token
        OAuth2AccessToken oAuth2AccessToken;
        try {
            // 使用tokenStore解析token
            oAuth2AccessToken = tokenStore.readAccessToken(token);
            Map<String, Object> additionalInfo = oAuth2AccessToken.getAdditionalInformation();
            // 取出用户身份信息
            String username = additionalInfo.get("user_name").toString();
            // 获取用户权限
            List<String> authorities = (List<String>) additionalInfo.get("authorities");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(TokenConstant.PRINCIPAL_NAME, username);
            jsonObject.put(TokenConstant.AUTHORITIES_NAME, authorities);
            // 将用户信息通过base64加密后放入请求头中，便于业务服务获取用户信息
            String userInfo = Base64.encode(jsonObject.toJSONString());
            ServerHttpRequest userInfoRequest = exchange.getRequest().mutate().header(TokenConstant.TOKEN_NAME, userInfo).build();
            ServerWebExchange build = exchange.mutate().request(userInfoRequest).build();
            return chain.filter(build);
        } catch (InvalidTokenException e){
            log.info("TOKEN解析出错");
            return invalidTokenMono(exchange);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 对url校验匹配，释放白名单
     */
    private boolean checkUrls(List<String> urls,String path){
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String url:urls){
            if(pathMatcher.match(url,path))
                return true;
        }
        return false;
    }

    /**
     * 从请求头中获取token
     */
    private String getToken(ServerWebExchange exchange){
        String tokenStr = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(StringUtils.isBlank(tokenStr)){
            return null;
        }
        // token: "bearer token..."
        String token = tokenStr.split(" ")[1];
        if(StringUtils.isBlank(token)){
            return null;
        }
        return token;
    }

    /**
     * 无效的token
     */
    private Mono<Void> invalidTokenMono(ServerWebExchange exchange){
        return buildReturnMono(ResultMsg.builder()
                .code(ResultCode.INVALID_TOKEN.getCode())
                .msg(ResultCode.INVALID_TOKEN.getMsg())
                .build(),
                exchange);
    }

    private Mono<Void> buildReturnMono(ResultMsg resultMsg,ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        byte[] bytes = JSON.toJSONString(resultMsg).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type","application/json;charset:utf-8");
        return response.writeWith(Mono.just(buffer));
    }
}
