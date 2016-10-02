package com.hhmedic.xiebo.repository;

import com.hhmedic.xiebo.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * Created by i on 2016/10/2.
 */
public interface UserMapper {
    User selectByUserName(@Param("userName") String userName);
    Set<String> selectRolesByUserName(@Param("userName") String userName);
    Set<String> selectPermissionsByUserName(@Param("userName") String userName);
}
