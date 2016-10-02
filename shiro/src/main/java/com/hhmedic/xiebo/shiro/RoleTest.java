package com.hhmedic.xiebo.shiro;

import com.hhmedic.xiebo.common.ShiroUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by i on 2016/10/1.
 */
public class RoleTest {

    @Test
    public void testHasRole() {
        Subject currentUser = ShiroUtil.login("classpath:shiro-role.properties", "java1234", "123456");
        System.out.println(currentUser.hasRole("role3"));
    }

    @Test
    public void testHasRoles() {
        Subject currentUser = ShiroUtil.login("classpath:shiro-role.properties", "java1234", "123456");
        boolean[] results = currentUser.hasRoles(Arrays.asList("role1", "role2", "role3"));
        System.out.println(results[0]);
        System.out.println(results[1]);
        System.out.println(results[2]);
    }

    @Test
    public void testHasAllRoles() {
        Subject currentUser = ShiroUtil.login("classpath:shiro-role.properties", "java1234", "123456");
        System.out.println(currentUser.hasAllRoles(Arrays.asList("role1", "role2", "role3")));
    }

    @Test
    public void testCheck() {
        Subject currentUser = ShiroUtil.login("classpath:shiro-role.properties", "java1234", "123456");
        currentUser.checkRole("role1");
        currentUser.checkRoles("role1", "role2");
        currentUser.checkRoles(Arrays.asList("role1", "role2", "role3"));
    }

    @Test
    @RequiresAuthentication
    public void testAuthentication() {
        // 读取配置文件，初始化SecurityManager工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro-role.properties");
        // 获取SecurityManager实例
        SecurityManager securityManager = factory.getInstance();
        // 把SecurityManager实例绑定到SecurityUtils
        SecurityUtils.setSecurityManager(securityManager);
        // 得到当前执行的用户
        Subject currentUser = SecurityUtils.getSubject();
        System.out.println(currentUser.isAuthenticated());
    }
}
