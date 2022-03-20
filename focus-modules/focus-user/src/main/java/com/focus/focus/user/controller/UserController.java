package com.focus.focus.user.controller;

import com.focus.focus.user.dao.UserRepository;
import com.focus.focus.user.domain.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/save")
    public void saveUser(@RequestBody UserEntity user){
        userRepository.save(user);
    }

    @GetMapping("/get/{username}")
    public UserEntity getUser(@PathVariable("username") String username){
        System.out.println("username: " + username);
        UserEntity userEntity = userRepository.findByUsername(username);
        System.out.println("userEntity:" + userEntity.toString());
        return userEntity;
    }
}
