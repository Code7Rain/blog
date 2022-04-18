package com.lll.blog.service;

import com.lll.blog.dao.pojo.SysUser;
import com.lll.blog.vo.Result;
import com.lll.blog.vo.params.LoginParam;

public interface LoginService {
    Result login(LoginParam loginParam);

    Result logout(String token);

    SysUser checkToken(String token);
}
