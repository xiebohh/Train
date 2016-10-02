<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<!DOCTYPE html>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<h2>登陆成功，欢迎你！</h2>
这里是session信息：${info}
<shiro:hasRole name="admin">
    欢迎有admin角色的用户
</shiro:hasRole>
<shiro:hasRole name="test">
    不应该显示
</shiro:hasRole>
<shiro:hasPermission name="user:select" >
    欢迎有user:select的用户
</shiro:hasPermission>
<shiro:hasPermission name="student:select" >
    欢迎有user:select的用户
</shiro:hasPermission>
<shiro:hasPermission name="test" >
    不应该显示
</shiro:hasPermission>
</body>
</html>