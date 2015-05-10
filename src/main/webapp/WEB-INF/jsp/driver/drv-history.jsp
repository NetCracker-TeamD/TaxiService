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
<html lang="en">
<head>
    <title>History</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="../../pages/resources/bootstrap/css/datepicker.css">
    <link rel="stylesheet" href="../../pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="../../pages/resources/bootstrap/css/bootstrap-theme.css">
    <link rel="stylesheet" href="../../pages/resources/project/css/welcome.css">
    <link rel="stylesheet" href="/pages/resources/project/css/history.css">
    <script src="../../pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="../../pages/resources/bootstrap/js/bootstrap.js"></script>
    <script src="../../pages/resources/bootstrap/js/bootstrap-datepicker.js"></script>
    <script src="../../pages/resources/project/js/driver/drv-history.js" type="text/javascript"></script></head>
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
            <a class="navbar-brand" href="#">Smart Taxi</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Home</a></li>
                <li><a href="#">Queue</a></li>
                <li><a href="#">History</a></li>
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
<div class="jumbotron welcome" style="height:150px;">
    <div class="container" style="height:150px;">
        <h1 style="color:yellow; text-align:right;">History</h1>
    </div>
</div>
<c:set var="page" value="?"/>
<c:if test="${param.page!=null}">
    <c:set var="page" value="${page}page=${param.page}&"/>
</c:if>
<div class="container">
    <div class="jumbotron">
        <div class="panel panel-default">
            <div class="well well-sm">
                <form class="form-inline" role="form" action="">
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="form-group">
                                    <label>ID</label>
                                    <input  placeholder="ID Order" value="${param.id_order}" class="form-control" name="id_order" type="text">
                            </div>
                            <div class="form-group">
                                    <label>Address</label>
                                    <input placeholder="Address" value="${param.address}" class="form-control" name="address" type="text">
                            </div>
                            <div class="form-group">
                                    <label>Service Type</label>
                                    <select placeholder="Service type" name="service_type" class="form-control" style="cursor:pointer;">
                                        <option></option>
                                        <c:forEach items="${serviceTypes.keySet()}" var="service">
                                            <c:if test="${param.service_type==service}">
                                                <option selected="true" value="${service}" >${service}</option>
                                            </c:if>
                                            <c:if test="${param.service_type!=service}">
                                                <option value="${service}" >${service}</option>
                                            </c:if>
                                        </c:forEach>
                                    </select>
                            </div>
                        </div>
                    </div>
                    <br>
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="input-daterange form-group">
                                <label>Date </label>
                                <input value="${param.startDate}" class="form-control" type="text" id="from_date" name="startDate"
                                       placeholder="Select start date"  >
                                <label>to</label>
                                <input value="${param.endDate}" class="form-control" type="text" id="to_date" name="endDate"
                                       placeholder="Select end date">
                            </div>
                            <input type="button" class="btn btn-default clear_param" value="Clear"/>
                            <input type="submit" class="btn btn-primary" value="Search"/>
                        </div>
                    </div>
                </form>
            </div>
            <div class="row">
                <div class="col-sm-1">
                    <div class="dropdown" style="margin-left:15px;">
                        <button class="btn btn-info dropdown-toggle" type="button" data-toggle="dropdown">Sort by
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" id="type_sort" >
                            <c:choose>
                                <c:when test="${param.sort=='id'}">
                                    <li><a href="${page}sort=date" >Date</a></li>
                                    <li class="selected-property">
                                        <a href="${page}sort=id" >ID Order
                                            <span id="ok-glyph" class="glyphicon glyphicon-ok"></span></a>
                                    </li>
                                </c:when>
                                <c:otherwise>
                                    <li class="selected-property">
                                        <a href="${page}sort=date">Date
                                            <span id="ok-glyph" class="glyphicon glyphicon-ok"></span></a>
                                    </li>
                                    <li><a href="${page}sort=id">ID Order</a></li>
                                </c:otherwise>
                            </c:choose>
                        </ul>
                    </div>
                </div>
                <div class="col-sm-9"></div>
                <div class="col-sm-2">
                    <div class="btn-group" id="viewType" style="margin-left:10px;">
                        <button type="button" class="btn btn-info" value="detailed">detailed</button>
                        <button type="button" class="btn btn-info active" value="list">list</button>
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
                                <div class="col-sm-2"><fmt:formatDate pattern="dd/MM/yyyy kk:mm"
                                                                      value="${order.executionDate.time}"/></div>
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
                                            <iframe src="https://www.google.com/maps/embed?pb=!1m14!1m8!1m3!1d5081.08804691991!2d30.4596392!3d50.4495934!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0000000000000000%3A0xbc67207d8e291fd0!2z0L_QsNGA0Log0JrQuNGX0LLRgdGM0LrQvtCz0L4g0L_QvtC70ZbRgtC10YXQvdGW0YfQvdC-0LPQviDRltC90YHRgtC40YLRg9GC0YM!5e0!3m2!1sru!2sua!4v1430052929432"
                                                    width="430" height="280" frameborder="0" style="border:0;"></iframe>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="panel panel-info">
                                        <div class="panel-heading">Routes</div>
                                        <div class="panel-body">
                                            <table class="table table-bordered">
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
                                                        <td><fmt:formatDate pattern="dd/MM/yyyy kk:mm"
                                                                            value="${route.startTime.time}"/></td>
                                                        <td>${route.sourceAddress}</td>
                                                        <td>${route.destinationAddress}</td>
                                                        <td><fmt:formatNumber type="number"
                                                                              maxFractionDigits="2" value="${route.distance}" /> km</td>
                                                        <td>
                                                            <fmt:formatDate pattern="dd/MM/yyyy kk:mm"
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
