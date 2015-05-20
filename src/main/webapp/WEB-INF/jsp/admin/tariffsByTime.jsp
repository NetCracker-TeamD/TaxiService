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
  <link rel="stylesheet" href="../../pages/resources/bootstrap/css/bootstrap.css">
  <link rel="stylesheet" href="../../pages/resources/project/css/admin.css">
  <link rel="stylesheet" href="../../pages/resources/bootstrap/css/datepicker.css">
  <script src="../../pages/resources/jquery/jquery-2.1.3.js"></script>
  <script src="../../pages/resources/bootstrap/js/bootstrap.js"></script>
  <script src="../../pages/resources/bootstrap/js/bootstrap-datepicker.js"></script>
  <script src="../../pages/resources/project/js/admin/tariffByTime.js"></script>
</head>

<body>

<%@include file="../../pages/admin/admin-header.html"%>




<div class="container" id="main_container">
  <h2 class="sm-hr">Tariffs by time</h2>

  <div class="sm-hr">
    <button type="button" class="btn btn-success btn-sm btn_create" data-toggle="createModel">
      <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> New Tariff
    </button>
    <div class="modal fade createModel" itabindex="-1" role="dialog">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <button class="close" type="button" data-dismiss="modal">x</button>
            <h4 class="modal-title">${tariff.tariffType}</h4>
          </div>
          <div class=modal-body">
              <!--TODO calendar to-->
              <!--TODO calendar from-->
            <div class="form-group">
              <label>Tariff Type</label>
              <!--TODO dropdown list-->
            </div>
            <div class="form-group">
              <label>Price</label>
              <input placeholder="price" class="form-control" name="price" type="text">
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-default" type="button" data-dismiss="modal">Close</button>
            <button class="btn btn-primary create_rec" data-dismiss="modal" type="button">Create</button></div>
        </div>
      </div>
    </div>
  </div>
  <div class="table-responsive">
    <table class="table table-striped table-hover">
      <thead>
      <tr>
        <th>#</th>
        <th>From</th>
        <th>To</th>
        <th>Tariff type</th>
        <th>Price(UAH)</th>
        <th>Manage</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="tariff" items="${tariffs}">
        <tr>
          <td>
            ${tariff.id}
          </td>
            <c:choose>
              <c:when test="${tariff.tariffType=='DAY_OF_YEAR'}">
                <td>
                  <fmt:formatDate value="${tariff.from.time}" pattern="MM-dd HH:mm" />
                </td>
                <td>
                  <fmt:formatDate value="${tariff.to.time}" pattern="MM-dd HH:mm" />
                </td>
                <td>
                  Daily tariff
                </td>
              </c:when>
              <c:when test="${tariff.tariffType=='TIME_OF_DAY'}">
                <td>
                  <fmt:formatDate value="${tariff.from.time}" pattern="HH:mm" />
                </td>
                <td>
                  <fmt:formatDate value="${tariff.to.time}" pattern="HH:mm" />
                </td>
                <td>
                  The tariff by time
                </td>
              </c:when>
              <c:when test="${tariff.tariffType=='DAY_OF_WEEK'}">
                <td>
                  <fmt:formatDate value="${tariff.from.time}" pattern="MM-dd HH:mm" />
                </td>
                <td>
                  <fmt:formatDate value="${tariff.to.time}" pattern="MM-dd HH:mm" />
                </td>
                <td>
                  Weekly tariff
                </td>
              </c:when>
            </c:choose>
          <td>${tariff.price}</td>
          <td>
            <a href="#editModal" role="button"
               class="btn btn-default btn_edit" data-toggle="editModal">Edit</a>
            <div class="modal fade editModal" itabindex="-1" role="dialog">
              <div class="modal-dialog">
                <div class="modal-content">
                  <div class="modal-header">
                    <button class="close" type="button" data-dismiss="modal">x</button>
                    <h4 class="modal-title">${tariff.tariffType}</h4>
                  </div>
                  <div class="modal-body">
                    <!--TODO calendar to-->
                    <!--TODO calendar from-->
                    <div class="form-group">
                      <label>Price coef</label>
                      <input value="${tariff.price}" class="form-control" name="price" type="text">
                    </div>
                  </div>
                  <div class="modal-footer">
                    <input type="hidden" name="tariff_id" value="${tariff.id}">
                    <button class="btn btn-default" type="button" data-dismiss="modal">Close</button>
                    <button class="btn btn-primary save_edit" data-dismiss="modal" type="button">Save</button></div>
                  </div>
              </div>
            </div>
            <a role="button"
               class="btn btn-default btn_remove" data-toggle="btn_remove">Remove</a>
            <div class="modal fade removeModal" itabindex="-1" role="dialog">
              <div class="modal-dialog">
                <div class="modal-content">
                  <div class="modal-header">
                    <button class="close" type="button" data-dismiss="modal">x</button>
                    <h4 class="modal-title">${tariff.tariffType}</h4>
                  </div>
                  <div class="modal-body">
                    <!--TODO calendar to-->
                    <!--TODO calendar from-->
                    <div class="form-group">
                      <label>Price coef</label>
                      <input value="${tariff.price}" class="form-control" name="price" type="text">
                    </div>
                  </div>
                  <div class="modal-footer">
                    <input type="hidden" name="tariff_id" value="${tariff.id}">
                    <button class="btn btn-default" type="button" data-dismiss="modal">Close</button>
                    <button class="btn btn-primary remove_rec" data-dismiss="modal" type="button">Remove</button></div>
                </div>
              </div>
            </div>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <!--Pagination start-->
    <nav align="center">
      <ul class="pagination">
        <c:forEach begin="1" end="${pages}" var="i">
          <c:choose>
            <c:when test="${(param.page==null)&&(i==1)}">
              <li class="active"><a href="?page=${i}">${i}</a></li>
            </c:when>
            <c:when test="${param.page==i}">
              <li class="active"><a href="?page=${i}">${i}</a></li>
            </c:when>
            <c:when test="${param.page!=i}">
              <li><a href="?page=${i}">${i}</a></li>
            </c:when>
          </c:choose>
        </c:forEach>
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