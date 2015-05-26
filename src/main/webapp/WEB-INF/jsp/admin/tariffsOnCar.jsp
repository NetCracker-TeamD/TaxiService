<%@ page import="org.springframework.data.domain.Page" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
  <title>Car class tariffs</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="authorisation form">
  <!-- Latest compiled and minified CSS -->
  <link rel="stylesheet" href="../../pages/resources/bootstrap/css/bootstrap.css">
  <link rel="stylesheet" href="../../pages/resources/project/css/admin.css">
  <script src="../../pages/resources/jquery/jquery-2.1.3.js"></script>
  <script src="../../pages/resources/bootstrap/js/bootstrap.js"></script>
  <script src="../../pages/resources/project/js/admin/tariffsOnCar.js"></script>
</head>
<body>
<!--common navigation bar for this service -->
<%@ include file="../../pages/admin/admin-header.html"%>


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
  <h2 class="sm-hr">Car class tariffs</h2>
  <div class="table-responsive">
    <div class="alert alert-success hide"></div>
    <table class="table table-striped table-hover">
      <thead>
      <tr>
        <th>#</th>
        <th>Car class</th>
        <th>Price coefficient</th>
        <th>Idle price coefficient</th>
        <th>Manage</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach varStatus="i" var="tariff" items="${carClasses}">
        <tr>
          <td>
              ${i.index+1}
          </td>
          <td>
              ${tariff.className}
          </td>
          <td>
              ${tariff.priceCoefficient}
          </td>
          <td>${tariff.idlePriceCoefficient}</td>
          <td>
            <a href="#editModal" role="button"
               class="btn btn-default btn_edit" data-toggle="modal">
              <span
                      class="glyphicon glyphicon-edit" aria-hidden="true"></span>
            </a>
            <div class="modal fade editModal" itabindex="-1" role="dialog">
              <div class="modal-dialog">
                <div class="modal-content">
                  <div class="modal-header">
                    <button class="close" type="button" data-dismiss="modal">x</button>
                    <h4 class="modal-title">${tariff.className}</h4>
                  </div>
                  <div class="modal-body">
                    <div class="form-group">
                      <label>Price coef</label>
                      <input value="${tariff.priceCoefficient}" class="form-control" name="price_coef" type="text">
                    </div>
                    <div class="form-group">
                      <label>Idle price coef</label>
                      <input value="${tariff.idlePriceCoefficient}" class="form-control" name="idle_price_coef" type="text">
                    </div>
                  </div>
                  <div class="modal-footer">
                    <input type="hidden" name="tariff_id" value="${tariff.id}">
                    <button class="btn btn-default" type="button" data-dismiss="modal">Close</button>
                    <button class="btn btn-primary save_edit" data-dismiss="modal" type="button">Save</button></div>
                </div>
              </div>
            </div>
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
</body>

</html>