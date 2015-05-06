<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html lang="en">
<!DOCTYPE html>
<html>
<head>
    <title>Welcome driwer</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/project/css/welcomeDriver.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>

    <script src="/pages/resources/project/js/driver/drv-queue.js" type="text/javascript"></script>

    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>

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
                <li class="active"><a href="#">Queue</a></li>
                <li><a href="history">History</a></li>
                <li><a href="order">Current order</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Dropdown
                        <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="#">Action</a></li>
                        <li><a href="#">Another action</a></li>
                        <li><a href="#">Something else here</a></li>
                        <li class="divider"></li>
                        <li class="dropdown-header">Nav header</li>
                        <li><a href="#">Separated link</a></li>
                        <li><a href="#">One more separated link</a></li>
                    </ul>
                </li>
            </ul>
            <div class="navbar-form navbar-right">
                <button type="button" class="btn btn-warning">Sign out</button>
            </div>
        </div>
    </div>
</nav>


<div class="jumbotron welcome">
    <div class="container">
        <br>
        <br>
        <br>
        <h2>Queue</h2>
    </div>
</div>


<c:if test="${param.services!=null}">
    <c:set var="services" value="${services}services=${param.services}&"/>
</c:if>
<c:if test="${param.curPage!=null}">
    <c:set var="curPage" value="${curPage}curPage=${param.curPage}&"/>
</c:if>

<div class="container">
    <div class="jumbotron">

            <div class="row">
                <div  class="panel col-md-8" style="padding: 0px;background-color: transparent;">
                    <div class="panel panel-primary col-md-12" id="accordion1" style="padding: 0px;margin: 0px">
                        <div class="panel-heading panel-info">
                            <div class="panel-info">
                                <div class="row" >
                                    <div class="col-sm-1"><strong>#</strong></div>
                                    <div class="col-sm-3"><strong>Time</strong></div>
                                    <div class="col-sm-4"><strong>Service</strong></div>
                                    <div class="col-sm-2"><strong>Payment type</strong></div>
                                    <div class="col-sm-2"><strong>View</strong></div>
                                </div>
                            </div>
                        </div>

                        <c:forEach items="${orders}" var="order">
                            <div id="queue_order1" class="panel-info panel-group" style="margin: 0px">

                                <div id="queue_order2" class="panel-heading">
                                    <div class="row" data-toggle="collapse" data-parent="#accordion1" href="#freeRoute">
                                        <strong>
                                        <div class="col-sm-1">${order.id}</div>
                                        <div class="col-sm-3">
                                            <fmt:formatDate pattern="dd, yyyy k:mm" value="${order.executionDate.time}"/>
                                        </div>
                                        <div class="col-sm-4">${order.serviceType.name}</div>
                                        <div class="col-sm-2">${order.paymentType}</div>
                                        <div class="col-sm-2">
                                            <button type="button" id="view" data-toggle="collapse"  class="btn btn-info" title="View details">
                                                <i class="glyphicon glyphicon-eye-open"></i>
                                            </button>
                                        </div>
                                        </strong>
                                    </div>
                                </div>

                                <div id="freeRoute"   class="panel-body collapse" style="padding: 0px">
                                    <div class="panel">
                                        <table class="table table-hover table-bordered" >
                                            <thead>
                                                <tr class="info">
                                                <th class="col-sm-1">#</th>
                                                <th class="col-sm-3">Source address</th>
                                                <th class="col-sm-3">Destination address</th>
                                                <th class="col-sm-3">Distance</th>
                                                <th class="col-sm-2">Accept</th>
                                                </tr>
                                            </thead>
                                                <tbody>
                                                <c:forEach items="${order.routes}" var = "route">
                                                    <tr class="info">
                                                        <td class="col-sm-1">${route.id}</td>
                                                        <td class="col-sm-3">${route.sourceAddress}</td>
                                                        <td class="col-sm-3">${route.destinationAddress}</td>
                                                        <td class="col-sm-3">${route.distance}</td>
                                                        <td class="col-sm-2">
                                                            <div>
                                                                <a class="btn btn-success" href="order"><i class="glyphicon glyphicon-plus"></i></a>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <ul class="pagination pagination-sm" style="padding-left: 30%;padding-right: 30%;background-color: transparent;">
                        <c:forEach begin="1" end="${countPage}" var="i">
                            <c:choose>
                                <c:when test="${(param.curPage==null)&&(i==1)}">
                                    <li class="active"><a href="queue?curPage=${i}">${i}</a></li>
                                </c:when>
                                <c:when test="${param.curPage==i}">
                                    <li class="active"><a href="queue?curPage=${i}">${i}</a></li>
                                </c:when>
                                <c:when test="${param.curPage!=i}">
                                    <li><a href="queue?curPage=${i}">${i}</a></li>
                                </c:when>
                            </c:choose>
                        </c:forEach>
                    </ul>
                </div>

                <div class="col-md-3 pull-right">
                    <div class="panel panel-primary">
                        <div class="panel-heading"><h3 class="panel-title">Filter services</h3></div>

                        <div class="panel-body col">
                            <form:form method="POST" action="queue?">
                                <c:forEach items="${services}" var="service">
                                    <input type="checkbox" name="${service.id}" id="${service.id}" checked>
                                    <label for="${service.id}" class="control-label">${service.name}</label><br>
                                </c:forEach>

                                <button type="submit" class="btn btn-info" name="submit" value="search" id="search">
                                    <span class="glyphicon glyphicon-search" aria-hidden="true"> Search</span>
                                </button>

                            </form:form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    <hr>
<!-- Modal -->
    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>