<%--
  Created by IntelliJ IDEA.
  User: Anton
  Date: 16.05.2015
  Time: 1:47
  To change this template use File | Settings | File Templates.
--%>
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
    <title>Profile</title>
    <link rel="stylesheet" href="../../pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="../../pages/resources/project/css/welcome.css">
    <link rel="stylesheet" href="/pages/resources/project/css/history.css">
    <script src="../../pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="../../pages/resources/bootstrap/js/bootstrap.js"></script>
</head>
<body>
<%@ include file="../../pages/driver/drv-header.html"%>
<div class="jumbotron welcome" style="height:150px;">
  <div class="container" style="height:150px;">
    <h1 style="color:yellow; text-align:right;">Profile</h1>
  </div>
</div>

<div class="container">
  <div class="tabbable well">
      <ul class="nav nav-tabs">
          <li class="active"><a href="#tab1" data-toggle="tab">Driver</a></li>
          <li><a href="#tab2" data-toggle="tab">Driver's car</a></li>
      </ul>
      <div class="tab-content well" style="background:#ffffff;">
          <div class="tab-pane active" id="tab1">
              <div class="row">
                  <div class="col-sm-3" >
                      <img src="../../pages/resources/project/img/profile.png" class="img-rounded" alt="Cinque Terre" width="250" height="250">
                     </div>
                  <div class="col-sm-9">
                      <ul class="list-group">
                          <li class="list-group-item">
                              <b>ID Driver:</b> ${driver.id}
                          </li>
                          <li class="list-group-item">
                                  <b>Name:</b> ${driver.lastName} ${driver.firstName}
                          </li>
                          <li class="list-group-item">
                                  <b>E-mail:</b> ${driver.email}
                          </li>
                          <li class="list-group-item">
                                  <b>License:</b> ${driver.license}
                          </li>
                          <li class="list-group-item">
                              <b>Phone number:</b> ${driver.phoneNumber}
                          </li>
                          <if test="${driver.features.size()!=0}">
                              <li class="list-group-item">
                                  <b>Features:</b>
                                  <ul>
                                      <c:forEach var="feature" items="${driver.features}">
                                          <li>${feature.name}</li>
                                      </c:forEach>
                                  </ul>
                              </li>
                          </if>
                      </ul>
                      <a href="/changePassword" class="btn btn-info" role="button">ChangePassword</a>
                  </div>
              </div>
          </div>
          <div class="tab-pane" id="tab2">
              <div class="row">
                  <div class="col-sm-4" >
                      <img src="../../pages/resources/project/img/taxiCar.png" class="img-rounded" alt="Cinque Terre" width="300" height="200">
                  </div>
                  <div class="col-sm-8">
                      <ul class="list-group">
                          <li class="list-group-item">
                              <b>Car ID:</b> ${car.carId}
                          </li>
                          <li class="list-group-item">
                              <b>Model:</b> ${car.model}
                          </li>
                          <li class="list-group-item">
                              <b>Class:</b> ${car.carClass.className}
                          </li>
                          <li class="list-group-item">
                              <b>Category:</b> ${car.category}
                          </li>
                          <if test="${car.features.size()!=0}">
                              <li class="list-group-item">
                                  <b>Features:</b>
                                  <ul>
                                  <c:forEach var="feature" items="${car.features}">
                                       <li>${feature.name}</li>
                                  </c:forEach>
                                  </ul>
                              </li>
                          </if>
                      </ul>
                  </div>
              </div>
          </div>
      </div>
  </div>
</div>
</body>
</html>
