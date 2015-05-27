<%--
  Created by IntelliJ IDEA.
  User: Іван
  Date: 12.05.2015
  Time: 21:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Error page</title>
    <link rel="stylesheet" href="../../pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="../../pages/resources/project/css/welcomeDriver.css">
    <script src="../../pages/resources/bootstrap/js/bootstrap.js"></script>
</head>
<body>
<%@ include file="../../pages/driver/drv-header.html" %>

<div class="jumbotron welcome">
    <div class="container">
        <h2 style="color: rgb(19, 23, 95);">Current order</h2>
    </div>
</div>

<div class="container">
    <div class="jumbotron">
        <h1>${errorMessage}</h1>
    </div>
</div>

</body>
</html>
