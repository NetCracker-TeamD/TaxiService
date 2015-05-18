<%@ page import="org.springframework.data.domain.Page" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
  <title>Tariffs by time</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="authorisation form">
  <!-- Latest compiled and minified CSS -->
  <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
  <link rel="stylesheet" href="/pages/resources/project/css/admin.css">
  <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
  <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>

  <!--<script src="../resources/project/js/admin/tariffByTime.js"></script>-->

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
            <li class="active"><a href="#">Tariffs by time</a></li>
            <li><a href="#">Service tariffs</a></li>
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

<div class="modal fade centered-modal" id="create_tariff" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">New Tariff</h4>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        <button type="button"
                onclick="createTariff()" class="btn btn-success">Create Tariff
        </button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade centered-modal" id="remove_tariff" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Remove tariff</h4>
      </div>
      <div class="modal-body">
        <div class="alert alert-danger alert-dismissible modal-error">
          <p>Error-Message</p>
        </div>
        <form>
          <input type="hidden" name="tariff_id"/>
        </form>
        <p class="lead">Are you really want to remove this tariff?</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-danger"
                onclick="removeTariff($('#remove_tariff').find('[name=\'tariff_id\']').val())">Remove Tariff
        </button>
      </div>
    </div>
  </div>
</div>

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

  <div class="sm-hr">
    <button type="button" class="btn btn-success btn-sm" data-toggle="modal" data-target="#create_tariff">
      <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> New Tariff
    </button>
  </div>
  <div class="table-responsive">
    <table class="table table-striped table-hover">
      <thead>
      <tr>
        <th>#</th>
        <th>From</th>
        <th>To</th>
        <th>Price</th>
        <th>Tariff type</th>
        <th>Manage</th>
      </tr>
      </thead>
      <tbody>
      <%int num = ((Page) request.getAttribute("page")).getSize() * ((Page) request.getAttribute("page")).getNumber();%>
      <c:forEach var="tariff" items="${page.content}">
        <tr>
          <td><%=++num%>
          </td>
          <td tariff-id="${tariff.id}">
            <fmt:formatDate value="${tariff.from.time}" pattern="yyyy.MM.dd HH:mm" />
          </td>
          <td>
            <fmt:formatDate value="${tariff.to.time}" pattern="yyyy.MM.dd HH:mm" />
          </td>
          <td>${tariff.price}</td>
          <td>${tariff.tariffType}</td>
          <td>
            <button title="Edit" type="button" onclick="startEditTariff(event)" data-toggle="modal"
                    data-target="#"
                    data-tariff-id="${tariff.id}" class="btn btn-default btn-xs" aria-label="Left Align">
              <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
            </button>
            <button title="Remove" type="button" data-toggle="modal" data-target="#remove_tariff"
                    data-tariff-id="${tariff.id}" class="btn btn-default btn-xs" aria-label="Left Align">
              <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
            </button>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <!--Pagination start-->
    <nav align="center">
      <ul class="pagination">
        <li <c:if test="${page.isFirst()}">class="disabled" onclick="return false"</c:if>>
          <a href="/admin/tariffs_by_time?page=${page.number-1}" title="Previous">
            <span aria-hidden="true">&laquo;</span>
          </a>
        </li>
        <c:forEach var="elem" items="${pagination}">
          <li <c:if test="${page.number == elem}">class="active"</c:if>>
            <a href="/admin/tariffs_by_time?page=${elem}">${elem+1}</a>
          </li>
        </c:forEach>
        <li <c:if test="${page.isLast()}">class="disabled" onclick="return false"</c:if>>
          <a href="/admin/tariffs_by_time?page=${page.number+1}" title="Next">
            <span aria-hidden="true">&raquo;</span>
          </a>
        </li>
      </ul>
    </nav>
    <!--Pagination end-->
  </div>

  <hr/>
  <footer>
    <p>&#169 TeamD 2015</p>
  </footer>
</div>
<script type="application/javascript" src="/pages/resources/project/js/admin/tariffByTime.js"></script>
</body>

</html>