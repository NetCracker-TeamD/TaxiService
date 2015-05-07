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
                <li  class="active"><a href="drv-current-order.jsp">Current order</a></li>
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
        <h2>Current order</h2>
    </div>
</div>
<div class="container">
    <div class="jumbotron col">
        <div class="row">
            <div class="col">
                <div class="col-md-4" >

                    <div style="margin:20px;border-width:2px;">
                        <div class="form-group">
                            <label for="curLoc" class="control-label">Current location</label>
                            <div>
                                <button type="button" id="curLoc" class="btn btn-primary">
                                <span class="glyphicon glyphicon-send" aria-hidden="true"></span>
                                </button>
                            </div>

                        </div>
                        <div class="form-group">
                            <label for="sourceLoc" class="control-label">Source location</label>
                            <input id="sourceLoc"  class="form-control" name="source" value="ololo" type="text" placeholder="Enter your address" readonly="readonly">
                        </div>
                        <div class="form-group">
                            <label for="destinationLoc" class="control-label">Destination</label>
                            <input onFocus="geolocate()" id="destinationLoc" class="form-control" name="destination" value="tratata" type="text" placeholder="Enter your address" readonly="readonly">
                            <%--<script>initializeAuto()</script>--%>
                        </div>
                        <div class="form-group row">
                            <div class="pull-left">
                                <input type="submit" id="newRoute" class="btn btn-warning" value="New route" >
                            </div>
                            <div class="pull-right">
                                <input type="submit" id="acceptNewRoute" class="disabled btn btn-success" value="Accept route">
                            </div>
                        </div>
                        <div class="form-group row">
                            <div class="input-group pull-right">
                                <input type="text" class="form-control" id="price" placeholder="mileage">
                                <span class="input-group-addon">km</span>
                            </div>
                            <div id="timer" class="pull-left" style="margin-bottom: 5px">
                                <input type="submit" id="inPlace" class="btn btn-info" value="In place">
                                <span id="countdown" class="timer"></span>
                            </div>
                        </div>

                        <div class="form-group row" style="margin-bottom: 5px">
                            <div class="pull-left">
                                <input type="submit" id="start" class="btn btn-primary" value="Start" >
                            </div>
                            <div class="pull-right">
                                <input type="submit" id="stop" class="btn btn-primary" value="Stop">
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col">
                    <div class="col-md-7 pull-right">
                        <div class="panel panel-primary"  >
                            <div class="panel-heading"><h3 class="panel-title">Map</h3></div>
                            <div class="panel-body"  id="map-canvas" style="width:580px;height:450px;">
                                <script>initialize("map-canvas")</script>
                            </div>

                       </div>
                    </div>
                    <%--<div class="col-md-3 pull-left">--%>
                         <%--<c:forEach items="${features}" var="feature">--%>
                            <%--<div><i class="glyphicon glyphicon-ok-sign"> feature.name</i></div>--%>
                        <%--</c:forEach>--%>
                    <%--</div>--%>
                </div>
                <div id="directions_panel" style="margin:20px;background-color:#FFEE77;"></div>
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-md-3 pull-left">
                <button type="submit" class="btn btn-success" aria-label="Left Align">
                    <span class="glyphicon glyphicon-ok"> Complete</span>
                </button>
               
            </div>
            <div class="col-md-3 pull-midlle">
                <a class="btn btn-danger" href="queue"><span class="glyphicon glyphicon-remove" aria-hidden="true"> Close</span></a>
            </div>
        </div>
    </div>
    <hr>
    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>