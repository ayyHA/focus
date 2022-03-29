package com.focus.focus.user.controller;

import com.focus.focus.api.dto.ResponseCode;
import com.focus.focus.api.dto.ResponseMsg;
import com.focus.focus.api.dto.SysUserDto;
import com.focus.focus.user.domain.entity.UserEntity;
import com.focus.focus.user.service.IAuthUserService;
import com.focus.focus.user.service.IUserService;
import com.focus.focus.user.service.impl.QiNiuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping
@RestController
public class UserController {
    @Autowired
    private IAuthUserService authUserService;
    @Autowired
    private QiNiuService qiNiuService;
    @Autowired
    private IUserService userService;

    // 根据用户名查询用户详情信息
    @GetMapping("/get/{username}")
    public UserEntity getUser(@PathVariable("username") String username){
        UserEntity userEntity = userService.getUserInfoByUsername(username);
        return userEntity;
    }

    // 用户注册
    @PostMapping("/signUp")
    public void signUp(@RequestBody SysUserDto sysUserDto){
        authUserService.save(sysUserDto);
    }

    // 用户上传头像
    @PostMapping("/uploadAvatar")
    public ResponseEntity<ResponseMsg> uploadAvatar(@RequestParam("file") MultipartFile multipartFile,@RequestParam("fileName") String fileName) throws IOException {
        if(multipartFile.isEmpty()){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.AVATAR_UPLOAD_ERROR.getCode(),ResponseCode.AVATAR_UPLOAD_ERROR.getMsg(),null));
        }
        // 通过七牛云OSS上传图片获取URL
        String path = qiNiuService.saveImage(multipartFile,fileName);
        log.info("UPLOAD PATH: {}",path);
        // 更新用户信息
        Boolean saveResult = userService.updateUserAvatar(path);
        if(!saveResult){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_REPOSITORY_ERROR.getCode(),ResponseCode.USER_REPOSITORY_ERROR.getMsg(),null));
        }
        // 设置响应数据
        Map<String,Object> data = new HashMap<>();
        data.put("path",path);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.AVATAR_UPLOAD_SUCCESS.getCode(),ResponseCode.AVATAR_UPLOAD_SUCCESS.getMsg(),data));
    }
}
