package com.focus.focus.api.feign;

import com.focus.focus.api.dto.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "userClient",value="user-service")
public interface UserClient {
    @GetMapping("/get/{username}")
    ResponseEntity<UserInfoDto> getUser(@PathVariable("username") String username);

    // 批量获取用户信息
    @GetMapping("/getUserInfoDtos")
    List<UserInfoDto> getUserInfoDtos(@RequestParam("ids") List<String> ids);

    // 获取单个用户信息
    @GetMapping("/getUserInfoDto")
    UserInfoDto getUserInfoDto(@RequestParam("username") String username);

    // 获取单个用户信息ByUserId
    @GetMapping("getUserInfoDtoById")
    UserInfoDto getUserInfoDtoById(@RequestParam("userId") String userId);

    // 搜索byNickname，搜索用户
    @GetMapping("/searchByNickname")
    List<UserInfoDto> searchByNickname(@RequestParam("nickname") String nickname);
}
