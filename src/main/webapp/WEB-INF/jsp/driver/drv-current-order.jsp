<!-- avtor Ivaniv Ivan 23.04.2015 version(1.0)-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Welcome driver</title>
    <meta charset="utf-8">

    <meta name="description" content="authorisation form">
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/project/css/welcomeDriver.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>


    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true&libraries=places"></script>
    <link type="text/css" rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500">

    <script src="/pages/resources/project/js/driver/drv-order.js" type="text/javascript"></script>
    <style>
        .row {
            margin-right: 0px;
            margin-left: 0px;
        }

        .btn-circle {
            width: 40px;
            height: 40px;
            line-height: 40px; /* adjust line height to align vertically*/
            padding: 0;
            border-radius: 50%;
        }
    </style>
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
                <li><a href="queue">Queue</a></li>
                <li><a href="history">History</a></li>
                <li class="active"><a href="order">Current order</a></li>
            </ul>
            <div class="navbar-form navbar-right">
                <button type="button" class="btn btn-warning">Sign out</button>
            </div>
        </div>
    </div>
</nav>

<div class="jumbotron welcome">
    <div class="container">
        <h2>Current order</h2>
    </div>
</div>
<div class="container">
    <div class="jumbotron col" style="padding-right: 30px; padding-left: 30px;">
        <div class="row">
            <div class="col-sm-5" style="margin:10px;border-width:2px;margin-top: 0px;">
                <div class="panel panel-primary">
                    <div class="panel-body" id="board2">
                        <div class="panel panel-primary">
                            <div class="panel-body" id="innerBoard">
                                <div class="form-inline">
                                    <div class="form-group pull-left col-sm-8" style="padding: 0px; width:250px">
                                        <input type="text" style="width:250px" class="form-control" id="currentLocation"
                                               placeholder="Current location" readonly>
                                    </div>
                                    <div class="form-group pull-right" style="padding-right:5px;padding-left:5px;">
                                        <button type="button" id="paintWay"
                                                class="btn btn-primary"  ${!isActiveOrder ? "disabled=\"on\"" : ""}>
                                            <span class="glyphicon glyphicon-road" aria-hidden="true"></span>
                                        </button>
                                    </div>
                                    <div class="form-group pull-right">
                                        <button type="button" id="curLoc" class="btn btn-primary">
                                            <span class="glyphicon glyphicon-send" aria-hidden="true"></span>
                                        </button>
                                    </div>
                                    <div class="form-group pull-left col-sm-6" style="padding:0px">
                                        <h5>
                                            <%--<span class="label label-info glyphicon glyphicon-time"> Time </span>--%>
                                            <span class="label label-info glyphicon glyphicon-time"
                                                  id="currentTime"></span>
                                        </h5>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <c:if test="${isActiveOrder}">
                            <div class="panel panel-primary">
                                <div class="panel-body" id="innerBoard2">
                                    <div class="form-group row" style="padding-bottom: 5px; padding-top: 5px">
                                        <div class="execTime row">
                                            <div class="col-md-8" style="padding:0px">
                                                <h5 style="margin-top: 0px;">
                                                    <span class="label label-info glyphicon glyphicon-time"
                                                          id="executionTime"></span>
                                                </h5>
                                            </div>
                                            <div class="row">

                                                <label for="customLate" class="label label-info control-label"
                                                       style="padding-bottom: 0px; margin-bottom: 0px;padding: 0px;">Customer is late</label>
                                                <input type="checkbox" name="customerIsLate" id="customLate">

                                            </div>
                                        </div>


                                        <div id="controlPanel" class="row">
                                            <c:choose>
                                                <c:when test="${inProgress}">
                                                    <div class="pull-left">
                                                        <button type="submit" class="start disabled btn btn-primary">
                                                            <span> Start</span>
                                                        </button>
                                                    </div>
                                                    <div class="pull-right">
                                                        <button type="submit" class="completeBtn btn btn-success">
                                                            <span> Complete </span>
                                                        </button>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="pull-left">
                                                        <button type="submit" class="start btn btn-primary">
                                                            <span> Start</span>
                                                        </button>
                                                    </div>
                                                    <div class="pull-right">
                                                        <button type="submit"
                                                                class="completeBtn disabled btn btn-success">
                                                            <span> Complete </span>
                                                        </button>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="hidden" id="refusePanel" style="margin-top: 10px;">
                                            <div class="pull-left">
                                                <button id="refuseBtn" type="submit" class="btn btn-danger">
                                                    <span> Refuse</span>
                                                </button>
                                            </div>

                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
                <c:if test="${isActiveOrder}">
                    <div class="panel panel-primary">
                        <div class="panel-body" id="board1">
                            <div class="col">
                                <div>
                                    <c:forEach items="${sortRoutes}" var="route">
                                        <div id class="input-group" style="padding-left: 0px;">
                                            <div>
                                                <input type="text" style="margin-top: 5px" class="form-control"
                                                       value="${route.sourceAddress}" name="source" readonly>
                                                <input type="text" style="margin-top: 5px" class="form-control"
                                                       value="${route.destinationAddress}" name="dest" readonly>
                                            </div>
                                            <div style="padding-top: 5px; padding-bottom: 10px;">
                                                <c:choose>
                                                    <c:when test="${route.status==\"COMPLETED\"}">
                                                        <span id="${route.id}"
                                                              class="label label-success glyphicon glyphicon-ok"> COMPLETED</span>
                                                    </c:when>
                                                    <c:when test="${route.status==\"ASSIGNED\"}">
                                                        <span id="${route.id}"
                                                              class="label label-info glyphicon glyphicon-list findForRefuse"> ASSIGNED</span>
                                                    </c:when>
                                                    <c:when test="${route.status==\"REFUSED\"}">
                                                        <span id="${route.id}"
                                                              class="label label-danger glyphicon glyphicon-remove"> REFUSED</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span id="${route.id}"
                                                              class="label label-primary glyphicon glyphicon-hourglass findForRefuse"> ${route.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                                <div class="input-group" id="newRoute" style="padding-left: 0px;">
                                <span class="input-group-btn">
                                    <button id="newRouteBtn" class="btn btn-default"
                                            type="button" ${blockNewRouteBtn ? "disabled=\"on\"" : ""}>Add
                                    </button>
                                </span>
                                    <input type="text" class="form-control" id="newAddress" name="dest"
                                           placeholder="Enter route..." ${blockNewRouteBtn ? "disabled=\"on\"" : ""}>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

            </div>
            <div class="col-sm-6 pull-right" style="padding-left: 0px">
                <div class="panel panel-primary">
                    <div class="panel-body" id="map-canvas" style="height:450px;">
                    </div>

                </div>
            </div>
        </div>
        <hr>
    </div>
    <hr>
    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>