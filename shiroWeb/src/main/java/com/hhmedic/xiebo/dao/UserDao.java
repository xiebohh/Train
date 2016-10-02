package com.hhmedic.xiebo.dao;

import com.hhmedic.xiebo.entity.User;
import com.hhmedic.xiebo.repository.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by i on 2016/10/2.
 */
public class UserDao {

    private SqlSession session;
    public UserDao() {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        session = sqlSessionFactory.openSession();
    }

    public User selectByUserName(String userName) throws IOException {
        UserMapper mapper = session.getMapper(UserMapper.class);
        User user = mapper.selectByUserName(userName);
        return user;
    }

    public Set<String> selectRolesByUserName(String userName) throws IOException {
        Set<String> roles;
        UserMapper mapper = session.getMapper(UserMapper.class);
        roles = mapper.selectRolesByUserName(userName);
        return roles;
    }

    public Set<String> selectPermissionsByUserName(String userName) throws IOException {
        Set<String> permissions;
        UserMapper mapper = session.getMapper(UserMapper.class);
        permissions = mapper.selectPermissionsByUserName(userName);
        return permissions;
    }
}
