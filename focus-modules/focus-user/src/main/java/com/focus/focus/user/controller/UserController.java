package com.focus.focus.user.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.focus.focus.api.dto.SysUserDto;
import com.focus.focus.api.dto.UserInfoDto;
import com.focus.focus.api.oss.QiNiuService;
import com.focus.focus.api.util.ResponseCode;
import com.focus.focus.api.util.ResponseMsg;
import com.focus.focus.user.service.IAuthUserService;
import com.focus.focus.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
    public ResponseEntity<UserInfoDto> getUser(@PathVariable("username") String username){
        UserInfoDto userInfoDto = userService.getUserInfoByUsername(username);
        return ResponseEntity.ok(userInfoDto);
    }

    // 用户注册
    @PostMapping("/signUp")
    public void signUp(@RequestBody SysUserDto sysUserDto){
        authUserService.save(sysUserDto);
    }

    // 用户上传头像
    @PostMapping("/uploadAvatar")
    public ResponseEntity<ResponseMsg> uploadAvatar(@RequestParam("file") MultipartFile multipartFile,
                                                    @RequestParam("fileName") String fileName) throws IOException {
        if(multipartFile.isEmpty()){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.AVATAR_UPLOAD_ERROR.getCode(),
                    ResponseCode.AVATAR_UPLOAD_ERROR.getMsg(),null));
        }
        // 通过七牛云OSS上传图片获取URL
        String path = qiNiuService.saveImage(multipartFile,fileName);
        log.info("UPLOAD AVATAR PATH: {}",path);
        // 更新用户信息
        Boolean saveResult = userService.updateUserAvatar(path);
        if(!saveResult){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_REPOSITORY_ERROR.getCode(),
                    ResponseCode.USER_REPOSITORY_ERROR.getMsg(),null));
        }
        // 设置响应数据
        Map<String,Object> data = new HashMap<>();
        data.put("path",path);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.AVATAR_UPLOAD_SUCCESS.getCode(),
                ResponseCode.AVATAR_UPLOAD_SUCCESS.getMsg(),data));
    }

    // 用户上传背景图
    @PostMapping("/uploadBackground")
    public ResponseEntity<ResponseMsg> uploadBackground(@RequestParam("file") MultipartFile multipartFile,
                                                        @RequestParam("fileName") String fileName) throws IOException {
        if(multipartFile.isEmpty()){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_UPLOAD_BACKGROUND_ERROR.getCode(),
                    ResponseCode.USER_UPLOAD_BACKGROUND_ERROR.getMsg(),null));
        }
        String path = qiNiuService.saveImage(multipartFile,fileName);
        log.info("UPLOAD BACKGROUND PATH: {}",path);
        Boolean saveResult = userService.updateUserBackground(path);
        if(!saveResult){
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_UPLOAD_BACKGROUND_ERROR.getCode(),
                    ResponseCode.USER_UPLOAD_BACKGROUND_ERROR.getMsg(),null));
        }
        Map<String,Object> data = new HashMap<>();
        data.put("path",path);
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_UPLOAD_BACKGROUND_SUCCESS.getCode(),
                ResponseCode.USER_UPLOAD_BACKGROUND_SUCCESS.getMsg(),data));
    }

    // 用户更新个人信息
    @RequestMapping("/updateUserDetails")
    public ResponseEntity<ResponseMsg> updateUserDetails(@RequestBody UserInfoDto userInfoDto){
        if(ObjectUtil.isEmpty(userInfoDto))
            return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_UPDATE_DETAILS_ERROR.getCode(),
                    ResponseCode.USER_UPDATE_DETAILS_ERROR.getMsg(),null));
        // 提交更新信息给数据库，获取更新后的DTO
        UserInfoDto resDto = userService.updateUserDetails(userInfoDto);
        Map<String,Object> data = new HashMap<>();
        data.put("userInfoDto",resDto);
        log.info("{} Controller",resDto.toString());
        return ResponseEntity.ok(new ResponseMsg(ResponseCode.USER_UPDATE_DETAILS_SUCCESS.getCode(),
                ResponseCode.USER_UPDATE_DETAILS_SUCCESS.getMsg(),data));
    }

    // 批量获取用户信息
    @GetMapping("/getUserInfoDtos")
    public List<UserInfoDto> getUserInfoDtos(@RequestParam("ids") List<String> ids){
        List<UserInfoDto> userInfoDTOs = userService.getUserInfoDTOs(ids);
        return userInfoDTOs;
    }

    // 获取单个用户信息
    @GetMapping("/getUserInfoDto")
    public UserInfoDto getUserInfoDto(@RequestParam("username") String username){
        UserInfoDto userInfoDto = userService.getUserInfoByUsername(username);
        return userInfoDto;
    }

    // 获取单个用户信息ByUserId
    @GetMapping("getUserInfoDtoById")
    public UserInfoDto getUserInfoDtoById(@RequestParam("userId") String userId){
        UserInfoDto userInfoDto = userService.getUserInfoById(userId);
        if(ObjectUtil.isEmpty(userInfoDto))
            return null;
        else
            return userInfoDto;
    }

    // 搜索byNickname，搜索用户
    @GetMapping("/searchByNickname")
    public List<UserInfoDto> searchByNickname(@RequestParam("nickname") String nickname){
        List<UserInfoDto> userInfoDtos = userService.searchByNickname(nickname);
        if(CollectionUtil.isEmpty(userInfoDtos))
            return null;
        return userInfoDtos;
    }
}
