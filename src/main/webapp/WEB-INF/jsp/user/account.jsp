<%--
  Created by IntelliJ IDEA.
  User: anton
  Date: 5/25/15
  Time: 4:27 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
  <%@ include file="../../pages/head-block.html"%>
  <script type="text/javascript">
    var userAddresses = ${addressesJSON},
        userInfo = ${userInfoJSON};
  </script>
  <script type="text/javascript" src="/pages/user/js/page_account.js"></script>
</head>
<body>
<%@ include file="header.jsp"%>
<div class="container content">
  <form id="contacts" class="form-signin" method="post" action="/user/updateAccount">
    <div class="form-group"><h2 class="form-signin-heading">Change contacts</h2>
      <%@ include file="contacts.jsp"%>
    </div>
    <button class="btn btn-primary pull-right has-spinner" data-action="save" data-form-id="contacts" type="submit" disabled>
      <span class="spinner"><i class="glyphicon glyphicon-refresh glyphicon-spin"></i></span>&nbsp;Save
    </button>
  </form>

  <form id="addresses" class="form-signin" method="post" action="/user/saveAddresses">
    <div class="form-group"><h2 class="form-signin-heading">Change locations</h2></div></form>
</div>
</body>
</html>
