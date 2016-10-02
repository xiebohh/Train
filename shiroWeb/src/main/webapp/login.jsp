<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
    <form action="login" method="post">
        userName:<input type="text" name="userName"/><br/>
        password:<input type="password" name="password"/><br/>
        <input type="submit" value="登陆"/>
        ${errorInfo}
    </form>
</body>
</html>