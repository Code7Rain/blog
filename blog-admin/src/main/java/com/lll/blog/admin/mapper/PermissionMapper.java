package com.lll.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lll.blog.admin.vo.Permission;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PermissionMapper extends BaseMapper<Permission> {

    List<Permission> findPermissionsByAdminId(Long adminId);
}