package com.focus.auth.common.utils;

import com.focus.auth.common.model.RequestConstant;
import com.focus.focus.api.util.LoginVal;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

//import com.focus.auth.common.model.LoginVal;

/**
 * 这个工具类封装了我们从请求中获取解密后的用户信息的操作
 * 只需要通过 OauthUtils.getCurrentUser()即可以获取到请求头中的loginVal_attribute对应的值：
 * 即我们解密后的用户信息
 */
public class OauthUtils {
    public static LoginVal getCurrentUser(){
      ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
      if(Objects.nonNull(attributes)){
          HttpServletRequest request = attributes.getRequest();
          LoginVal loginVal= (LoginVal) request.getAttribute(RequestConstant.LOGIN_VAL_ATTRIBUTE);
          return loginVal;
      }
      return null;
    }
}
