package com.lll.blog.service;

import com.lll.blog.dao.pojo.SysUser;
import com.lll.blog.vo.Result;
import com.lll.blog.vo.UserVo;

public interface SysUserService {

    SysUser findUserById(Long userId);

    SysUser findUser(String account, String password);

    void save(SysUser sysUser);

    SysUser findUserByAccount(String account);

    Result findUserInfoByToken(String token);

    UserVo findUserVoById(Long Id);
}