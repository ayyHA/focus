package com.focus.focus.api.feign;

import com.focus.focus.api.dto.SysUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "authUserClient",value = "user-service")
public interface AuthUserClient {
    @PostMapping("/signUp")
    void signUp(@RequestBody SysUserDto sysUserDto);
}
