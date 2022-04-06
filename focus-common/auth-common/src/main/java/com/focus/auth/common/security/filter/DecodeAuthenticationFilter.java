package com.focus.auth.common.security.filter;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.focus.auth.common.model.LoginVal;
import com.focus.auth.common.model.RequestConstant;
import com.focus.auth.common.model.TokenConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 采用Base64解码
 * 此过滤器用于解密网关传递的用户信息，将其放入request中，便于业务模块使用
 */
@Slf4j
@Component
public class DecodeAuthenticationFilter extends OncePerRequestFilter {
    /**
     * 从header获取token,将其内携带的用户信息解析出来（解密->转为JSONObject->获取username和authorities）
     * 而后将其放入LoginVal实例中，放入请求中，便于后续业务模块使用
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = httpServletRequest.getHeader(TokenConstant.TOKEN_NAME);
        if (StrUtil.isNotBlank(token)) {
            String json = Base64.decodeStr(token);
            JSONObject jsonObject = JSON.parseObject(json);
            String principal = jsonObject.getString(TokenConstant.PRINCIPAL_NAME);
            JSONArray tmpJSONArray = jsonObject.getJSONArray(TokenConstant.AUTHORITIES_NAME);
            String[] authorities = tmpJSONArray.toArray(new String[0]);
            LoginVal loginVal = new LoginVal();
            loginVal.setUsername(principal);
            loginVal.setAuthorities(authorities);
            httpServletRequest.setAttribute(RequestConstant.LOGIN_VAL_ATTRIBUTE,loginVal);
//            log.info("DecodeAuthenticationFilter LoginVal: {} ",loginVal.toString());
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
