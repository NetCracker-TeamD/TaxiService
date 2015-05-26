<%--
  Created by IntelliJ IDEA.
  User: anton
  Date: 5/25/15
  Time: 12:54 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<nav class="navbar navbar-inverse navbar-fixed-top">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
              data-target="#navbar" aria-expanded="false" aria-controls="navbar">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">SmartTaxi</a>
    </div>
    <div id="navbar" class="collapse navbar-collapse">
      <c:set var="page" value="${fn:substringBefore(pageContext.request.servletPath, '.jsp')}"/>
      <ul class="nav navbar-nav" data-type="menu-list">
        <li${fn:endsWith(page, 'about') ? ' class="active"' : ''}><a href="/about">About</a></li>
        <li${fn:endsWith(page, 'order') ? ' class="active"' : ''}><a href="/order">Make order</a></li>
        <sec:authorize access="isAuthenticated()">
          <li${fn:endsWith(page, '/user/history') ? ' class="active"' : ''}><a href="/user/history">View order history</a></li>
          <li${fn:endsWith(page, '/user/account') ? ' class="active"' : ''}><a href="/user/account">Edit account</a></li>
        </sec:authorize>
      </ul>
      <ul class="nav navbar-nav navbar-right">
        <sec:authorize access="isAnonymous()">
          <li${fn:endsWith(page, '/login') ? ' class="active"' : ''}><a href="/login"><span class="glyphicon glyphicon-log-in"></span>&nbsp;Sign In</a></li>
          <li${fn:endsWith(page, '/register') ? ' class="active"' : ''}><a href="/register"><span class="glyphicon glyphicon-user"></span>&nbsp;Sign Up</a></li>
        </sec:authorize>
        <sec:authorize access="isAuthenticated()">
          <li${fn:endsWith(page, '/logout') ? ' class="active"' : ''}><a href="/logout"><span class="glyphicon glyphicon-log-out"></span>&nbsp;Logout</a></li>
        </sec:authorize>
      </ul>
    </div>
  </div>
</nav>
