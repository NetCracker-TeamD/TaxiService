<%@ page import="com.teamd.taxi.entity.Route" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="com.teamd.taxi.entity.TaxiOrder" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"
           uri="http://www.springframework.org/tags/form" %>
<html>
<head>
  <title>Change Password</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- Latest compiled and minified CSS -->
  <link rel="stylesheet" href="../../pages/resources/bootstrap/css/datepicker.css">
  <link rel="stylesheet" href="../../pages/resources/bootstrap/css/bootstrap.css">
  <%--<link rel="stylesheet" href="../../pages/resources/project/css/welcome.css">--%>
  <link rel="stylesheet" href="/pages/resources/project/css/history.css">
  <script src="../pages/resources/jquery/jquery-2.1.3.js"></script>
  <script src="../pages/resources/bootstrap/js/bootstrap.js"></script>
  <script src="../pages/resources/bootstrap/js/bootstrap-datepicker.js"></script>
  <script src="../pages/resources/project/js/changePassword.js"></script>
</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
              aria-expanded="false" aria-controls="navbar">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="/">Smart Taxi</a>
    </div>
    <div id="navbar" class="navbar-collapse collapse">
      <ul class="nav navbar-nav">
        <li class="active"><a href="/">Home</a></li>

      </ul>
      <div class="navbar-form navbar-right">
        <div class="form-group">
          <input type="text" placeholder="Email" class="form-control">
        </div>
        <div class="form-group">
          <input type="password" placeholder="Password" class="form-control">
        </div>
        <button type="button" class="btn btn-primary">Sign in</button>
        <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#t_and_c_m">Sign up
        </button>
      </div>
    </div>
    <!--/.navbar-collapse -->
  </div>
</nav>
<div class="jumbotron welcome" style="background:#fff;height: 120px;">
  <div class="container" >
    <h2 style="">Change Password</h2>
  </div>
</div>
<div class="container">
  <div class="jumbotron">
      <div class="row">
        <div class="col-sm-12">
          <div class="panel panel-info">
            <div class="panel-heading"></div>
            <div class="panel-body">
              <div class="row">
                <div class="col-sm-6 col-sm-offset-3">
                  <p class="text-center">Use the form below to change your password.</p>
                  <div class="row">
                  <c:if test="${info!=null}">
                    <div class="in_er col-sm-12 alert alert-success">
                        ${info}
                    </div>
                  </c:if>
                  <c:if test="${error!=null}">
                    <div class="in_er col-sm-12 alert alert-danger">
                        ${error}
                    </div>
                  </c:if>
                  <div class="col-sm-12 alert alert-warning war_info" style="display:none">
                    <strong></strong>
                  </div>
                  </div>
                  <form method="post" action="" id="passwordForm">
                    <div class="form-group">
                      <label for="oldpass">Old Password:</label>
                      <input name="oldpass" type="password" class="form-control" id="oldpass" placeholder="Old Password" autocomplete="off">
                    </div>
                    <div class="form-group">
                      <label for="newpass">New Password:</label>
                      <input name="newpass" type="password" class="form-control" id="newpass" placeholder="New Password" autocomplete="off">
                      <span id="6char" class="glyphicon glyphicon-remove" style="color:#FF0004;"></span> 6 Characters Long<br>
                     </div>
                    <div class="form-group">
                      <label for="repass">Repeat Password:</label>
                      <input name="repass" type="password" class="form-control" id="repass" placeholder="Repeat Password" autocomplete="off">
                        <span id="pwmatch" class="glyphicon glyphicon-remove" style="color:#FF0004;"></span>Passwords Match
                    </div>
                    <input type="submit" class="col-xs-12 btn btn-primary btn-load btn-lg" value="Change Password">
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
  </div>
</div>
<hr>
<p>&#169 TeamD 2015</p>
</body>
</html>
