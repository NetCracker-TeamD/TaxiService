<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"  errorPage="../error-page.jsp" %>
<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form"
           uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html lang="en">
<head>
    <title>History</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="../../pages/resources/bootstrap/css/datepicker.css">
    <link rel="stylesheet" href="../../pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="../../pages/resources/bootstrap/css/bootstrap-select.css">
    <link rel="stylesheet" href="../../pages/resources/project/css/welcome.css">
    <link rel="stylesheet" href="../../pages/resources/project/css/history.css">
    <link type="text/css" rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500">
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true"></script>
    <script src="../../pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="../../pages/resources/bootstrap/js/bootstrap.js"></script>
    <script src="../../pages/resources/booetstrap/js/bootstrap-select.js"></script>
    <script src="../../pages/resources/bootstrap/js/bootstrap-datepicker.js"></script>
    <script src="../../pages/resources/project/js/driver/drv-history.js" type="text/javascript"></script></head>
<body>
<sec:authorize access="hasRole('ROLE_ADMINISTRATOR')">
    nklasnklasd
    <%@include file="../admin/header.jsp"%>
    <div class="jumbotron" style="background-color: #fff; ">
        <div class="container" style="height: 15px;">
            <h2 style="color:#000;">The history of the orders for driver ${driver.lastName} ${driver.firstName}</h2>
        </div>
    </div>
</sec:authorize>
<sec:authorize access="hasRole('ROLE_DRIVER')">
    <%@include file="drv-header.jsp"%>
    <div class="jumbotron welcome" style="height:150px;">
        <div class="container" style="height:150px;">
            <h1 style="color:yellow; text-align:right;">History</h1>
        </div>
    </div>
</sec:authorize>
<div class="container">
    <div class="jumbotron">
        <div class="panel panel-default">
            <div class="well well-sm">
                <form class="form-inline" role="form" action="">
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="form-group">
                                    <label>ID </label>
                                    <input  placeholder="ID Order" value="${param.id_order}" class="form-control" name="id_order" type="text">

                                    <label>Address</label>
                                    <input placeholder="Address" value="${param.address}" class="form-control" name="address" type="text">

                                    <label>Service type</label>
                                    <select name="service_type" class="selectpicker">
                                        <option></option>
                                        <c:forEach items="${serviceTypes}" var="service">
                                            <c:if test="${param.service_type==service.name}">
                                                <option selected="true" value="${service.name}" >${service.name}</option>
                                            </c:if>
                                            <c:if test="${param.service_type!=service.name}">
                                                <option value="${service.name}" >${service.name}</option>
                                            </c:if>
                                        </c:forEach>
                                    </select>
                            </div>
                        </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="col-sm-8">
                            <div class="input-daterange form-group">
                                <label id="datee">Date </label>
                                <input value="${param.startDate}" class="form-control" type="text" id="from_date" name="startDate"
                                       placeholder="Select start date"  >
                                <label>to</label>
                                <input value="${param.endDate}" class="form-control" type="text" id="to_date" name="endDate"
                                       placeholder="Select end date">
                            </div>
                        </div>
                        <div class="col-sm-4 text-right">
                            <input type="submit" class="btn btn-primary" value="Search"/>
                            <input type="button" class="btn btn-default clear_param" value="Clear"/>
                        </div>
                    </div>
                </form>
            </div>
            <div class="panel-heading">
                <div class="row">
                    <div class="col-sm-6">
                        <label for="type_sort">Sort by:</label>
                        <select  id="type_sort" class="selectpicker" onChange="window.location.href=this.value" >
                            <c:choose>
                                <c:when test="${param.sort=='oldest'}">
                                    <option value="?sort=newest">newest</option>
                                    <option selected="true" value="${page}sort=oldest">oldest</option>
                                </c:when>
                                <c:otherwise>
                                    <option selected="true" value="${page}sort=newest">newest</option>
                                    <option value="?sort=oldest">oldest</option>
                                </c:otherwise>
                            </c:choose>
                        </select>
                    </div>
                    <div class="col-sm-6 text-right">
                        <div class="btn-group" id="viewType">
                            <button type="button" class="btn btn-info" value="detailed">detailed</button>
                            <button type="button" class="btn btn-info active" value="list">list</button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel-body">
                <c:forEach items="${orderList}" var="order" varStatus="i">
                    <div class="panel panel-default history_list">
                        <div class="panel-heading history_node">
                            <div class="row">
                                <div class="col-sm-4">
                                    <a><span class="glyphicon glyphicon-chevron-down"> </span>
                                        <b>№ ${order.id}</b></a>
                                </div>
                                <div class="col-sm-6"></div>
                                <div class="col-sm-2"><fmt:formatDate pattern="dd/MM/yyyy HH:mm"
                                                                      value="${order.executionDate.time}" /></div>
                            </div>
                        </div>
                        <div style="display:none;" class="panel-body history_details">
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="panel panel-info">
                                        <div class="panel-heading">Information</div>
                                        <div class="panel-body">
                                            <ul class="list-group">
                                                <li class="list-group-item"><b>ID Order:</b> ${order.id}</li>
                                                <li class="list-group-item"><b>Date:</b>
                                                    <fmt:formatDate type="date"
                                                                    value="${order.executionDate.time}"/>
                                                </li>
                                                <li class="list-group-item"><b>Service type:</b> ${order.serviceType.name}</li>
                                                <li class="list-group-item"><b>Method of
                                                    payment:</b> ${order.paymentType.name()}</li>
                                                </li>
                                                <li class="list-group-item"><b>Comment:</b> ${order.comment}</li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="panel panel-info map-panel">
                                        <div class="panel-heading"><span class="glyphicon glyphicon-chevron-down"></span>
                                            Map</div>
                                        <div class="panel-body map" style="display:none;">
                                            <div id="map-canvas" style="min-width: 200px;height: 300px"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="panel panel-info">
                                        <div class="panel-heading">Routes</div>
                                        <div class="panel-body">
                                            <table class="table table_route table-bordered">
                                                <thead>
                                                <tr>
                                                    <th>Pick-up time</th>
                                                    <th>Pick-up address</th>
                                                    <th>Destination address</th>
                                                    <th>Distance</th>
                                                    <th>Completion Time</th>
                                                    <th>Status</th>
                                                    <th>Cost</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <c:forEach var="route" items="${order.routes}">
                                                    <tr>
                                                        <td><fmt:formatDate pattern="dd/MM/yyyy HH:mm"
                                                                            value="${route.startTime.time}"/></td>
                                                        <td class="source_add">${route.sourceAddress}</td>
                                                        <td class="dest_add">${route.destinationAddress}</td>
                                                        <td><fmt:formatNumber type="number"
                                                                              maxFractionDigits="2" value="${route.distance}" /> km</td>
                                                        <td>
                                                            <fmt:formatDate pattern="dd/MM/yyyy HH:mm"
                                                                            value="${route.completionTime.time}"/>

                                                        </td>
                                                        <td>${route.status}</td>
                                                        <td>
                                                            <c:if test="${route.totalPrice==null}">
                                                                <span class="glyphicon glyphicon-minus"></span>
                                                            </c:if>
                                                            <c:if test="${route.totalPrice!=null}">
                                                                ${route.totalPrice} UAH
                                                            </c:if>
                                                        </td>
                                                    </tr>

                                                </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                <%-- pagination--%>
                <ul class="pagination">
                    <c:set var="sort" value="?"/>
                    <c:if test="${param.sort!=null}">
                        <c:set var="sort" value="${sort}sort=${param.sort}&"/>
                    </c:if>
                    <c:forEach begin="1" end="${pages}" var="i">
                        <c:choose>
                            <c:when test="${(param.page==null)&&(i==1)}">
                                <li class="active"><a href="${sort}page=${i}">${i}</a></li>
                            </c:when>
                            <c:when test="${param.page==i}">
                                <li class="active"><a href="${sort}page=${i}">${i}</a></li>
                            </c:when>
                            <c:when test="${param.page!=i}">
                                <li><a href="${sort}page=${i}">${i}</a></li>
                            </c:when>
                        </c:choose>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>
</div>
<hr>
<p>&#169 TeamD 2015</p>
</body>
</html>
