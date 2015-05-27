<%--
  Created by IntelliJ IDEA.
  User: Іван
  Date: 12.05.2015
  Time: 21:54
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
        <h2 style="color: rgb(19, 23, 95);">Error</h2>
    </div>
</div>

<div class="container">
    <div class="jumbotron">
        <div class="alert alert-warning alert-dismissible" role="alert">
            <h4 style="color: red"><strong>Error </strong> ${errorMessage}</h4>
        </div>
    </div>
</div>

</body>
</html>
