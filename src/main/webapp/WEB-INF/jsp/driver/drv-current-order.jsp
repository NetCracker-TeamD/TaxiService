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
    <script src="/pages/resources/project/js/driver/poliline-route.js"></script>
    <script src="/pages/resources/project/js/map/geolocation.js"></script>
    <script src="/pages/resources/project/js/driver/auto-complete-search.js"></script>
    <script src="/pages/resources/project/js/driver/auto-complete-search1.js"></script>
    <script src="/pages/resources/project/js/driver/auto-complete-search2.js"></script>

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
                <li><a href="queue.html">Queue</a></li>
                <li><a href="#history">History</a></li>
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
        <br>
        <br>
        <br>
        <h2>Current order</h2>
    </div>
</div>
<div class="container">
    <div class="jumbotron col">
        <div class="row">
            <div class="col">
                <div class="col-md-5" >

                    <div style="margin:20px;border-width:2px;">
                        <div class="form-group">
                            <label for="autocomplete" class="control-label">Current location</label>
                            <input  onFocus="geolocate()"  id="autocomplete"  class="form-control" name="adress" placeholder="Enter your address" type="text">
                            <script>initialize1()</script>
                        </div>
                        <div>
                            <br>
                            <b>Midlle points:</b> <br>

                            <select multiple id="waypoints">
                                <option value="lutsk">Lutsk, Volyns'ka oblast, Ukraine</option>
                                <option value="berdichev">Berdichev, Zhytomyrs'ka oblast, Ukraine</option>
                                <option value="rivne">Rivne, Ukraine</option>
                                <option value="zitomir">Žitomir, Žytomyrská, Ukrajina</option>+
                            </select>
                            <br>
                        </div>



                        <div class="form-group">
                            <label for="destLocation" class="control-label">Destination</label>
                            <input onFocus="geolocate()" type="text" class="form-control" id="destLocation"  >
                            <script>initialize3()</script>
                        </div>

                        <input type="submit" class="btn btn-info" value="Way" onclick="calcRoute();">

                        <!--<button class="btn btn-info" onclick="getLocation()">Geolocation</button>-->
                        <!--<p id="demo"></p>-->
                    </div>

                        <!--<div class="form-group">-->
                    <!--<div id="control_panel" class="list-inline">-->
                            <!--<label for="autocomplete" class="control-label">Current location</label>-->
                            <!--<input  onFocus="geolocate()"  id="autocomplete"  class="form-control" name="adress" placeholder="Enter your address" type="text">-->
                            <!--<script>initialize1()</script>-->
                            <!--<p id="demo"></p>-->
                            <!--<button class="btn btn-info" type="button" onclick="getLocation()" id="search">Search</button>-->
                        <!--</div>-->

                        <!--<div class="form-group">-->
                            <!--<br>-->
                            <!--<b>Midlle points:</b> <br>-->

                            <!--<select multiple id="waypoints">-->
                                <!--<option value="lutsk">Lutsk</option>-->
                                <!--<option value="berdichev">Berdichev</option>-->
                                <!--<option value="rivne">Rivne</option>-->
                            <!--</select>-->
                            <!--<br>-->
                            <!--&lt;!&ndash;<label for="sourceLocation" class="control-label">Source location</label>&ndash;&gt;-->
                            <!--&lt;!&ndash;<input onFocus="geolocate()"  type="text" class="form-control" id="sourceLocation" >&ndash;&gt;-->
                            <!--&lt;!&ndash;<script>initialize2()</script>&ndash;&gt;-->

                        <!--</div>-->
                        <!--<div class="form-group">-->
                            <!--<label for="destLocation" class="control-label">Destination</label>-->
                            <!--<input onFocus="geolocate()" type="text" class="form-control" id="destLocation"  >-->
                            <!--<script>initialize3()</script>-->

                        <!--</div>-->
                        <!--<button class="btn btn-info" type="submit" onclick="calcRoute()" id="way">Way</button>-->

                        <!--&lt;!&ndash;<input type="submit" onclick="calcRoute();">&ndash;&gt;-->
                   <!---->
                    <!--</div>-->
                    <div class="input-group col-md-5">
                        <span class="input-group-addon">$</span>
                        <input type="text" class="form-control" id="price" placeholder="Price">
                        <span class="input-group-addon">.00</span>
                    </div>
                    <br>

                </div>


                    <!--<div id="map-canvas" style="float:left;width:70%;height:100%;"></div>-->
                <div class="col">
                    <div class="col-md-7 pull-right">
                        <div class="panel panel-primary"  >
                            <div class="panel-heading"><h3 class="panel-title">Map</h3></div>
                            <div class="panel-body"  id="map-canvas" style="width:580px;height:350px;">
                                <!--id="google-map"-->

                            </div>
                            <script>initialize()</script>

                       </div>
                    </div>
                     <div class="col-md-3 pull-left">

                        <div><i class="glyphicon glyphicon-ok-sign"> Non smoking</i></div>
                        <div><i class="glyphicon glyphicon-ok-sign"> WiFI</i></div>
                        <div><i class="glyphicon glyphicon-ok-sign"> Male</i></div>

                    </div>
                </div>
                <div id="directions_panel" style="margin:20px;background-color:#FFEE77;"></div>
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-md-3 pull-left">
                <button type="submit" class="btn btn-success" aria-label="Left Align">
                    <span class="glyphicon glyphicon-ok"> Accept</span>
                </button>
               
            </div>
            <div class="col-md-3 pull-midlle">
                <a class="btn btn-danger" href="queue.html"><span class="glyphicon glyphicon-remove" aria-hidden="true"> Close</span></a>
            </div>
        </div>
    </div>
    <hr>
    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>