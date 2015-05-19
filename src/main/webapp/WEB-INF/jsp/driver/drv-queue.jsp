<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html lang="en">
<!DOCTYPE html>
<html>
<head>
    <title>Driver queue</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/project/css/welcomeDriver.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>

    <script src="/pages/resources/jsrenderer/jsrender.min.js/"></script>
    <script src="/pages/resources/project/js/driver/drv-queue.js" type="text/javascript"></script>
    <script src="/pages/resources/project/js/paging.js" type="text/javascript"></script>

    <script>
        var initState = {
            pageNum: ${pageable.pageNumber + 1},
            additional: {
                <c:forEach varStatus="status" var="entry" items="${selectedServices}">
                <c:if test="${entry.value}">
                "${entry.key.id}": "on",
                </c:if>
                </c:forEach>
            }
        };
        function formatDistance(number) {
            try {
                return number.toFixed(2);
            } catch(e) {
                return "-";
            }
        }
        $.views.helpers({format: formatDistance});

    </script>
    <script id="orderItemTemplate" type="text/x-jsrender">
    <div class="order-container panel-info panel-group" style="margin: 0px">
        <div class="order-details panel-heading">
            <div class="row">
                <strong>
                    <div class="col-sm-4">{{:order.executionDate}}</div>
                    <div class="col-sm-4">{{:order.serviceType.name}}</div>
                    <div class="col-sm-2">{{:order.paymentType}}</div>
                    <div class="col-sm-2">
                        <button type="button" id="view" data-toggle="collapse" class="btn btn-info"
                                 title="View details">
                                <i class="glyphicon glyphicon-eye-open"></i>
                        </button>
                        {{if order.serviceType.isDestinationLocationsChain === true}}
                            <a class="btn btn-success" href="assign?id={{:order.id}}" ${activeOrder ? "disabled=\"on\"" : ""}>
                            <i class="glyphicon glyphicon-link"></i></a>
                        {{/if}}
                    </div>
                </strong>
            </div>
        </div>
        <div class="panel-body free-route" style="padding: 5px; display:none;">
            <div class="panel">
                <table class="table table-hover table-bordered">
                    <thead>
                        <tr class="info">
                            <th class="col-sm-3">Source address</th>
                            <th class="col-sm-3">Destination address</th>
                            <th class="col-sm-2">Cars</th>
                            <th class="col-sm-2">Distance (km)</th>
                            <th class="col-sm-2">Accept</th>
                        </tr>
                    </thead>
                    <tbody>
                        {{for assembledRoutes }}
                            <tr class="info">
                                <td class="col-sm-3">{{:sourceAddress}}</td>
                                <td class="col-sm-3">{{:destinationAddress}}</td>
                                <td class="col-sm-2">{{:totalCars}}</td>
                                <td class="col-sm-2">{{:~format(totalDistance)}}</td>
                                <td class="col-sm-2">
                                     {{if #parent.parent.data.order.serviceType.isDestinationLocationsChain !== true}}
                                        <a class="btn btn-success" href="assign?id={{:#parent.parent.parent.data.order.id}}&source={{:sourceAddress}}&dest={{:destinationAddress}}"
                                        ${activeOrder ? "disabled=\"on\"" : ""}>
                                        <i class="glyphicon glyphicon-plus"></i></a>
                                    {{/if}}
                                </td>
                            </tr>
                        {{/for}}
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    </script>

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
            </ul>
            <div class="navbar-form navbar-right">
                <button type="button" class="btn btn-warning">Sign out</button>
            </div>
        </div>
    </div>
</nav>
<div class="jumbotron welcome">
    <div class="container">
        <h2>Queue</h2>
    </div>
</div>
<div class="container">
    <div class="jumbotron">
        <div class="row">
            <div class="panel col-md-8" style="padding: 0px;background-color: transparent;">
                <div class="panel panel-primary col-md-12" id="accordion1" style="padding: 0px;margin: 0px">
                    <div class="panel-heading panel-info">
                        <div class="panel-info">
                            <div class="row">
                                <div class="col-sm-4"><strong>Time</strong></div>
                                <div class="col-sm-4"><strong>Service</strong></div>
                                <div class="col-sm-2"><strong>Payment type</strong></div>
                                <div class="col-sm-2"><strong>View</strong></div>
                            </div>
                        </div>
                    </div>
                    <div id="orders-container">

                    </div>
                </div>
                <ul id="pagination" class="pagination pagination-sm"
                    style="padding-left: 30%;padding-right: 30%;background-color: transparent;">
                </ul>
            </div>

            <div class="col-md-3 pull-right">
                <div class="panel panel-primary">
                    <div class="panel-heading"><h3 class="panel-title">Filter services</h3></div>

                    <div class="panel-body col">
                        <form id="service-types-form">
                            <c:forEach items="${selectedServices}" var="service">
                                <input type="checkbox" name="${service.key.id}" id="service${service.key.id}"
                                    ${service.value ? "checked=\"on\"" : ""}>
                                <label for="${service.key.id}" class="control-label">${service.key.name}</label>
                                <br>
                            </c:forEach>
                            <button type="submit" class="btn btn-info" name="submit" value="search" id="search">
                                <span class="glyphicon glyphicon-search" aria-hidden="true" > Search</span>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <hr>
    <!-- Modal -->
    <footer>
        <p>&#169 TeamD 20157</p>
    </footer>
</div>