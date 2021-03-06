package com.lll.blog.controller;

import com.lll.blog.service.SysUserService;
import com.lll.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UsersController {

    @Autowired
    private SysUserService SysUserService;

    @GetMapping("currentUser")
    public Result currentUser(@RequestHeader("Authorization") String token){

        return SysUserService.findUserInfoByToken(token);
    }
}