package com.focus.focus.user.controller;

import com.focus.focus.api.dto.SysUserDto;
import com.focus.focus.user.dao.UserRepository;
import com.focus.focus.user.domain.entity.UserEntity;
import com.focus.focus.user.service.IAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping
@RestController
public class UserController {
    @Autowired
    private IAuthUserService authUserService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/get/{username}")
    public UserEntity getUser(@PathVariable("username") String username){
        System.out.println("username: " + username);
        UserEntity userEntity = userRepository.findByUsername(username);
        System.out.println("userEntity:" + userEntity.toString());
        return userEntity;
    }


    // 用户注册
    @PostMapping("/signUp")
    public void signUp(@RequestBody SysUserDto sysUserDto){
        authUserService.save(sysUserDto);
    }
}
