package com.focus.focus.auth.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.web.AuthenticationEntryPoint;

public class OAuthServerClientCredentialsTokenEndpointFilter extends ClientCredentialsTokenEndpointFilter {
    private final AuthorizationServerSecurityConfigurer configurer;

    private AuthenticationEntryPoint authenticationEntryPoint;

    public OAuthServerClientCredentialsTokenEndpointFilter(AuthorizationServerSecurityConfigurer configurer, AuthenticationEntryPoint authenticationEntryPoint) {
        this.configurer = configurer;
        this.authenticationEntryPoint=authenticationEntryPoint;
    }

    @Override
    public void setAuthenticationEntryPoint(AuthenticationEntryPoint entryPoint){
        this.authenticationEntryPoint = entryPoint;
    }

    @Override
    protected AuthenticationManager getAuthenticationManager(){
        return configurer.and().getSharedObject(AuthenticationManager.class);
    }

    @Override
    public void afterPropertiesSet(){
        setAuthenticationFailureHandler((request,response,exception)->{
            if(exception instanceof BadCredentialsException){
                exception = new BadCredentialsException(exception.getMessage(),new BadClientCredentialsException());
            }
            authenticationEntryPoint.commence(request,response,exception);
        });
        setAuthenticationSuccessHandler((request,response,authentication)->{});
    }
}
