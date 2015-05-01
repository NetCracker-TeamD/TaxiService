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
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap-theme.css">
    <link rel="stylesheet" href="/pages/resources/project/css/welcome.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>
    <script src="/pages/resources/project/js/history/history.js" type="text/javascript"></script>
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
            <a class="navbar-brand" href="#">Smart Taxi</a>
        </div>

        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Home</a></li>
                <li><a href="#">Queue</a></li>
                <li><a href="/driver/history/">History</a></li>
            </ul>

            <div class="navbar-form navbar-right">
                <div class="form-group">
                    <input type="text" placeholder="Email" class="form-control">
                </div>
                <div class="form-group">
                    <input type="password" placeholder="Password" class="form-control">
                </div>
                <button type="button" class="btn btn-success">Sign in</button>
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
            <div class="row">
                <div class="col-sm-1">
                    <div class="dropdown">
                        <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Sort by
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu">
                            <li><a href="${page}sort=id">ID Order</a></li>
                            <li><a href="${page}sort=time">Delivery time car</a></li>
                            <li><a href="${page}sort=price">Cost of payment</a></li>
                            <li><a href="${page}sort=routes">Number of routes</a></li>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="panel-body">
                <c:forEach items="${routesList}" var="route">
                    <div id="history_list" class="panel panel-default">
                        <div id="history_node" class="panel-heading">
                            <div class="row">
                                <div class="col-sm-3">
                                    <a><span class="glyphicon glyphicon-chevron-down"> </span>
                                        <b>№ ${route.id}</b></a>
                                </div>
                                <div class="col-sm-3"><fmt:formatDate pattern="MMM dd, yyyy k:mm"
                                                                      value="${route.startTime.time}"/></div>
                                <div class="col-sm-3">${route.totalPrice} UAH</div>
                                <div class="col-sm-3">${route.status}</div>
                            </div>
                        </div>
                        <div id="history_details" style="display:none;" class="panel-body">
                            <div class="row">
                                <br>

                                <div class="col-md-6">
                                    <div class="panel panel-info">
                                        <div class="panel-heading">Information</div>
                                        <div class="panel-body">
                                            <ul class="list-group">
                                                <li class="list-group-item"><b>Tracking num:</b> ${route.order.id}</li>
                                                <li class="list-group-item"><b>Date:</b> <fmt:formatDate type="date"
                                                                                                         value="${route.order.executionDate.time}"/>
                                                </li>
                                                <li class="list-group-item"><b>Service type:</b> Cargo taxi</li>
                                                <li class="list-group-item"><b>Method of
                                                    payment:</b> ${route.order.paymentType.name()}</li>
                                                <li class="list-group-item"><b>Cost of
                                                    payment:</b> <%--${route.price}--%> UAH
                                                </li>
                                                <li class="list-group-item"><b>Comment:</b> ${route.order.comment}</li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="panel panel-info">
                                        <div class="panel-heading">Map</div>
                                        <div class="panel-body">
                                            <iframe src="https://www.google.com/maps/embed?pb=!1m14!1m8!1m3!1d5081.08804691991!2d30.4596392!3d50.4495934!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0000000000000000%3A0xbc67207d8e291fd0!2z0L_QsNGA0Log0JrQuNGX0LLRgdGM0LrQvtCz0L4g0L_QvtC70ZbRgtC10YXQvdGW0YfQvdC-0LPQviDRltC90YHRgtC40YLRg9GC0YM!5e0!3m2!1sru!2sua!4v1430052929432"
                                                    width="430" height="280" frameborder="0" style="border:0;"></iframe>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-12">
                                    <div class="panel panel-info">
                                        <div class="panel-heading">Routes</div>
                                        <div class="panel-body">
                                            <table class="table table-striped">
                                                <thead>
                                                <tr>
                                                    <th>Date and time</th>
                                                    <th>Pick-up address</th>
                                                    <th>Destination address</th>
                                                    <th>Distance</th>
                                                    <th>Time of trip</th>
                                                    <th>Status</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <c:forEach var="r" items="${route.order.routes}">
                                                    <tr>
                                                        <td><fmt:formatDate pattern="MMM dd, yyyy k:m"
                                                                            value="${r.startTime.time}"/></td>
                                                        <td>${r.sourceAddress}</td>
                                                        <td>${r.destinationAddress}</td>
                                                        <td>${r.distance} km</td>
                                                        <td><%--${r.completionTime-r.startTime}--%></td>
                                                        <td>${r.status}</td>
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
</div>
</body>
</html>
