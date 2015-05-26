<%--
  Created by IntelliJ IDEA.
  User: anton
  Date: 5/25/15
  Time: 1:53 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <%@ include file="../../pages/head-block.html"%>
  <script type="text/javascript" src="/pages/user/js/page_login.js"></script>
</head>
<body>
<%@ include file="header.jsp"%>
<div class="container content">
<form id="login-form" class="form-signin" method="post" action="/checkLogin">
  <div class="form-group">
    <h2 class="form-signin-heading">Please sign in</h2>
    <div class="form-group">
      <div class="input-group">
        <span class="input-group-addon glyphicon glyphicon-envelope"></span>
        <input type="email" class="form-control" name="username" placeholder="Enter your email"
               data-error="Email address is invalid" required>
      </div>
      <div class="help-block with-errors"></div>
    </div>
    <div class="form-group">
      <div class="input-group">
        <span class="input-group-addon glyphicon glyphicon-lock"></span>
        <input type="password" class="form-control" name="password"
               placeholder="Enter your password" required>
      </div>
      <div class="help-block with-errors"></div>
    </div>
    <div class="form-group">
      <div class="radio">
        <label>
          <input type="radio" name="radioAuthenticationType" id="userRadioButton" value="user" checked="" required>
          User</label>
      </div>
      <div class="radio">
        <label>
          <input type="radio" name="radioAuthenticationType" id="driverRadioButton" value="driver" required>
          Driver</label>
      </div>
      <div class="help-block with-errors"></div>
    </div>
    <button type="submit" class="btn btn-lg btn-primary btn-block has-spinner" data-action="login" type="submit" disabled><span
            class="spinner"><i class="glyphicon glyphicon-refresh glyphicon-spin"></i></span>&nbsp;Sign in
    </button>
  </div>
</form>
</div>
</body>
</html>