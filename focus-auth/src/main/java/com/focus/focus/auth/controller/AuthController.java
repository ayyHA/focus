package com.focus.focus.auth.controller;

import com.focus.auth.common.model.ResultCode;
import com.focus.auth.common.model.ResultMsg;
import com.focus.focus.api.dto.SysUserDto;
import com.focus.focus.auth.service.IAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
@RequestMapping
public class AuthController {
    @Autowired
    private IAuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<Object> signUp(@RequestBody SysUserDto sysUserDto){
        log.info("Before Save SysUser");
        SysUserDto _sysUserDto = authService.saveSysUser(sysUserDto);
        if(Objects.isNull(_sysUserDto))
            return ResponseEntity.ok(new ResultMsg(ResultCode.USERNAME_REPEATED.getCode(),ResultCode.USERNAME_REPEATED.getMsg(),null));
        return ResponseEntity.ok(_sysUserDto);
    }
}
