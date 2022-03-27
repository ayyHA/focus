package com.focus.focus.auth.exception;

import com.focus.auth.common.model.ResultCode;
import com.focus.auth.common.model.ResultMsg;
import com.focus.focus.auth.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用于处理客户端认证出错，如：client_id,client_secret
 */
@Slf4j
@Component
public class OAuthServerAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        log.info("OAuthServerAuthenticationEntryPoint");
        ResponseUtils.result(httpServletResponse,new ResultMsg(ResultCode.CLIENT_AUTHENTICATION_FAILED.getCode(),ResultCode.CLIENT_AUTHENTICATION_FAILED.getMsg(),null));
    }
}
