package com.focus.focus.auth.config;

import com.focus.focus.auth.exception.OAuthServerAuthenticationEntryPoint;
import com.focus.focus.auth.exception.OAuthServerWebResponseExceptionTranslator;
import com.focus.focus.auth.filter.OAuthServerClientCredentialsTokenEndpointFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    /* 令牌存储策略 */
    @Autowired
    private TokenStore tokenStore;

    /* 客户端存储策略 */
    @Autowired
    private ClientDetailsService clientDetailsService;

    /* Security的认证管理器 */
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private OAuthServerAuthenticationEntryPoint authenticationEntryPoint;

    /* 令牌管理服务的配置 */
    @Bean
    public AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices services = new DefaultTokenServices();
        // 客户端配置策略
        services.setClientDetailsService(clientDetailsService);
        // 支持令牌刷新
        services.setSupportRefreshToken(true);
        // 令牌存储策略
        services.setTokenStore(tokenStore);
        // accessToken过期时间
        services.setAccessTokenValiditySeconds(60*60*24);
        // refreshToken过期时间
        services.setRefreshTokenValiditySeconds(60*60*24*3);
        // 设置令牌增强
        services.setTokenEnhancer(jwtAccessTokenConverter);
        return services;
    }

    /* 配置令牌访问的端点 */
    @Override
    @SuppressWarnings("ALL")
    public void configure(AuthorizationServerEndpointsConfigurer endpoints){
        endpoints
                // 设置此异常翻译器，用于处理用户名密码错误或授权类型错误的异常
                .exceptionTranslator(new OAuthServerWebResponseExceptionTranslator())
                /* 授权码模式所需 */
                .authorizationCodeServices(authorizationCodeServices())
                // 密码模式所需
                .authenticationManager(authenticationManager)
                // 令牌管理服务
                .tokenServices(tokenServices())
                // 只允许POST获取令牌
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    /* 配置令牌访问的安全约束 */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security){
        OAuthServerClientCredentialsTokenEndpointFilter endpointFilter = new OAuthServerClientCredentialsTokenEndpointFilter(security, authenticationEntryPoint);
        endpointFilter.afterPropertiesSet();
        security.addTokenEndpointAuthenticationFilter(endpointFilter);
        security
                .authenticationEntryPoint(authenticationEntryPoint)
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()");
    }

    /**
     * 配置客户端详情，并不是所有的客户端都能接入授权服务
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 暂定内存模式，后续可以存储在数据库中，更加方便
        clients.inMemory()
                //客户端id
                .withClient("myjszl")
                //客户端秘钥
                .secret(new BCryptPasswordEncoder().encode("123"))
                //资源id，唯一，比如订单服务作为一个资源,可以设置多个
                //授权模式，总共四种，1. authorization_code（授权码模式）、password（密码模式）、client_credentials（客户端模式）、implicit（简化模式）
                //refresh_token并不是授权模式，
                .authorizedGrantTypes("authorization_code","password","client_credentials","implicit","refresh_token")
                //允许的授权范围，客户端的权限，这里的all只是一种标识，可以自定义，为了后续的资源服务进行权限控制
                .scopes("all")
                //false 则跳转到授权页面
                .autoApprove(false)
                //授权码模式的回调地址
                .redirectUris("http://www.baidu.com");
    }

    /**
     * 授权码模式的service，使用授权码模式authorization_code必须注入
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        // 授权码暂时存在内存中，后续可以存储在数据库中
        return new InMemoryAuthorizationCodeServices();
    }


}
