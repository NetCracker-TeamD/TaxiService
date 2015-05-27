<%--
  Created by IntelliJ IDEA.
  User: anton
  Date: 5/25/15
  Time: 1:53 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
  <%@ include file="../../pages/head-block.html"%>
  <script type="text/javascript" src="/pages/user/js/page_register.js"></script>
</head>
<html>
<%@ include file="header.jsp"%>
<div class="container content">
<form id="reg-form" class="form-signin" method="post" action="/signup">
  <div class="form-group"><h2 class="form-signin-heading">Please sign up</h2></div>
  <%@ include file="contacts.jsp"%>
  <div class="form-group"><label>Provide password</label>
      <div class="form-group">
        <div class="input-group">
            <span class="input-group-addon glyphicon glyphicon-lock"></span>
            <input type="password" class="form-control" name="password"
                   placeholder="Enter your password" required>
        </div>
        <div class="help-block with-errors"></div>
      </div>
      <div class="form-group">
          <div class="input-group">
              <span class="input-group-addon glyphicon glyphicon-lock"></span>
              <input type="password" class="form-control" name="passwordConfirmation"
                     data-match="[name='password']" data-match-error="Passwords are diferent"
                     placeholder="Repeat password" required>
          </div>
          <div class="help-block with-errors"></div>
      </div>
  </div>
  <button class="btn btn-lg btn-primary btn-block has-spinner" data-action="reg" type="submit"><span class="spinner"><i
          class="glyphicon glyphicon-refresh glyphicon-spin"></i></span>&nbsp;Sign up
  </button>
</form>
</div>
</body>
</html>