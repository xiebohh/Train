package com.hhmedic.xiebo.shiro;

import com.hhmedic.xiebo.common.ShiroUtil;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by i on 2016/10/1.
 */
public class PermissionTest {

    @Test
    public void testIsPermmited() {
        Subject currentUser = ShiroUtil.login("classpath:shiro-permission.properties", "jack", "123");
        System.out.println(currentUser.isAuthenticated());
        System.out.println(currentUser.isPermitted("user:add"));
        boolean[] results = currentUser.isPermitted("user:add", "user:select", "user:update");
        System.out.println(results[0]);
        System.out.println(results[1]);
        System.out.println(results[2]);
    }

    @Test
    public void testIsPermmitedAll() {
        Subject currentUser = ShiroUtil.login("classpath:shiro-permission.properties", "jack", "123");
        System.out.println(currentUser.isPermittedAll("user:add", "user:select", "user:update"));
    }
}
