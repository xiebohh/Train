package com.hhmedic.xiebo.realm;

import com.hhmedic.xiebo.dao.UserDao;
import com.hhmedic.xiebo.entity.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.io.IOException;

/**
 * Created by i on 2016/10/2.
 */
public class MyRealm extends AuthorizingRealm {

    private UserDao userdao = new UserDao();
    @Override
    //为当前登陆的用户授予角色和权限
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userName = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        try {
            authorizationInfo.setRoles(userdao.selectRolesByUserName(userName));
            authorizationInfo.setStringPermissions(userdao.selectPermissionsByUserName(userName));
            return authorizationInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    //验证当前登陆的用户
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String userName = (String) authenticationToken.getPrincipal();
        try {
            User user = userdao.selectByUserName(userName);
            if (user != null) {
                AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getUserName(), user.getPassword(), "");
                return authenticationInfo;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
