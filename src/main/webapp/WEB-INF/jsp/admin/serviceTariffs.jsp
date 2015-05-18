<%@ page import="org.springframework.data.domain.Page" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
  <title>Service tariffs</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="authorisation form">
  <!-- Latest compiled and minified CSS -->
  <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
  <link rel="stylesheet" href="/pages/resources/project/css/admin.css">
  <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
  <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>


</head>

<body>
<!--common navigation bar for this service -->
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
      <a class="navbar-brand" href="#">Smart Taxi</a>
    </div>
    <div id="navbar" class="navbar-collapse collapse">
      <ul class="nav navbar-nav">
        <li><a href="#">Users</a></li>
        <li><a href="#">Groups</a></li>
        <li><a href="#">Drivers</a></li>
        <li><a href="#">Cars</a></li>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Tariffs
            <span class="caret"></span></a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="#">Tariffs by time</a></li>
            <li class="active"><a href="#">Service tariffs</a></li>
            <li><a href="#">Feature tariffs</a></li>
            <li><a href="#">Class car tariffs</a></li>
          </ul>
        </li>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Reports
            <span class="caret"></span></a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="#">Report type 1</a></li>
            <li><a href="#">Report type 2</a></li>
            <li><a href="#">Report type 3</a></li>
          </ul>
        </li>
      </ul>
      <div class="navbar-form navbar-right">
        <button type="button" class="btn btn-primary">Sign out</button>
      </div>
    </div>
  </div>
</nav>

<div class="modal fade centered-modal" id="successModal" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Success</h4>
      </div>
      <div class="modal-body">
        <p class="lead">Successful operation</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-success" data-dismiss="modal">Ok</button>
      </div>
    </div>
  </div>
</div>

<div class="container" id="main_container">
  <h2 class="sm-hr">Tariffs by time</h2>
  <div class="table-responsive">
    <table class="table table-striped table-hover">
      <thead>
      <tr>
        <th>#</th>
        <th>Name</th>
        <th>PriceByTime</th>
        <th>PriceByDistance</th>
        <th>MinimalPrice</th>
        <th>Manage</th>
      </tr>
      </thead>
      <tbody>
      <%int num = 1;%>
      <c:forEach var="tariff" items="${services}">
        <tr>
          <td>
            <%=num++%>
          </td>
          <td tariff-id="${tariff.id}">
            ${tariff.name}
          </td>
          <td>
            ${tariff.priceByTime}
          </td>
          <td>${tariff.priceByDistance}</td>
          <td>${tariff.minPrice}</td>
          <td>
            <button title="Edit" type="button" onclick="startEditTariff(event)" data-toggle="modal"
                    data-target="#"
                    data-tariff-id="${tariff.id}" class="btn btn-default btn-xs" aria-label="Left Align">
              <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
            </button>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </div>

  <hr/>
  <footer>
    <p>&#169 TeamD 2015</p>
  </footer>
</div>
<script type="application/javascript" src="/pages/resources/project/js/admin/serviceTariffs.js"></script>
</body>

</html>