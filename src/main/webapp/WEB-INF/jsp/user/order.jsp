<%--
  Created by IntelliJ IDEA.
  User: anton
  Date: 5/25/15
  Time: 12:52 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<%@ include file="../../pages/head-block.html"%>
  <script type="text/javascript">
    var servicesInfo = ${servicesJSON}
    var userAddresses = ${addressesJSON}
    <c:if test="${not empty orderInfoJSON}">
    var orderInfo = ${orderInfoJSON}
    </c:if>
    <c:if test="${empty orderInfoJSON}">
    var orderInfo = null
    </c:if>
  </script>
  <script type="text/javascript" src="/pages/user/js/page_order.js"></script>
</head>
<body>
<%@ include file="header.jsp"%>
<div class="container content">
</div>
</body>
</html>
