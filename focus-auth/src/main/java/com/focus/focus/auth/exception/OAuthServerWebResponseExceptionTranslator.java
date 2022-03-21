package com.focus.focus.auth.exception;

import com.focus.auth.common.model.ResultCode;
import com.focus.auth.common.model.ResultMsg;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

/**
 * 此类用于处理：用户名或密码错误异常、授权类型（GrantType）异常
 */
@SuppressWarnings("ALL")
public class OAuthServerWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    @Override
    public ResponseEntity translate(Exception e) throws Exception {
        ResultMsg resultMsg = doTranslateHandler(e);
        return new ResponseEntity<>(resultMsg, HttpStatus.UNAUTHORIZED);
    }

    private ResultMsg doTranslateHandler(Exception e){
        ResultCode resultCode = ResultCode.UNAUTHORIZED;
        if(e instanceof UnsupportedGrantTypeException){
            resultCode = ResultCode.UNSUPPORTED_GRANT_TYPE;
        }else if(e instanceof InvalidGrantException){
            resultCode = ResultCode.USERNAME_OR_PASSWORD_ERROR;
        }
        return new ResultMsg(resultCode.getCode(),resultCode.getMsg(),null);
    }
}
