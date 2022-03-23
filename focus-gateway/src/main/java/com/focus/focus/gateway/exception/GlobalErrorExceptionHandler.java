package com.focus.focus.gateway.exception;

import com.focus.auth.common.model.ResultCode;
import com.focus.auth.common.model.ResultMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-1)
@Component
@RequiredArgsConstructor
public class GlobalErrorExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if(response.isCommitted()){
            return Mono.error(ex);
        }
        ResultMsg resultMsg = new ResultMsg(ResultCode.UNAUTHORIZED.getCode(),ResultCode.UNAUTHORIZED.getMsg(),null);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // 设置响应的状态码
        if(ex instanceof ResponseStatusException){
            response.setStatusCode(((ResponseStatusException)ex).getStatus());
        }
        // token失效的异常处理
        if(ex instanceof InvalidTokenException){
            resultMsg = new ResultMsg(ResultCode.INVALID_TOKEN.getCode(),ResultCode.INVALID_TOKEN.getMsg(),null);
        }
        ResultMsg finalResultMsg = resultMsg;
        return response.writeWith(Mono.fromSupplier(()->{
            DataBufferFactory bufferFactory = response.bufferFactory();
            try{
                return bufferFactory.wrap(new ObjectMapper().writeValueAsBytes(finalResultMsg));
            } catch (Exception e) {
                return bufferFactory.wrap(new byte[0]);
            }
        }));

    }
}
