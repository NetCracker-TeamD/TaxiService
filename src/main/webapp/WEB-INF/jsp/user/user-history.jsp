<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <title>History</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap-theme.css">
    <link rel="stylesheet" href="/pages/resources/jquery/css/jquery-ui.css">
    <link rel="stylesheet" href="/pages/resources/project/css/welcome.css">
    <link rel="stylesheet" href="/pages/resources/project/css/history.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/jquery/jquery-ui.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>

    <script src="/pages/resources/jsrenderer/jsrender.min.js/"></script>
    <script src="/pages/resources/project/js/paging.js" type="text/javascript"></script>
    <script src="/pages/resources/project/js/user/user-history.js" type="text/javascript"></script>
    <script>
        var startState = {
            pageNum: ${pageable.pageNumber + 1},
            sort: [<c:forEach var="sort" items="${sorts}" varStatus="status">'${sort}'${!status.last ? "," : ""}</c:forEach>],
            additional: {
                <c:forEach var="entry" items="${additionalParams}">
                "${entry.key}": ${entry.value},
                </c:forEach>
            }
        };
    </script>
    <script id="orderItemTemplate" type="text/x-jsrender">
        <div class="history_list panel panel-default">
            <div class="history_node panel-heading">
                <div class="row">
                    <div class="col-sm-4">
                        <a><span class="glyphicon glyphicon-chevron-down"> </span>
                            <b>№{{:order.id}}</b></a>
                    </div>
                    <div class="col-sm-8 text-right">{{:order.registrationDate}}</div>
                </div>
            </div>
            <div style="display:none;" class="history_details panel-body">
                <div class="row">
                    <div class="col-md-6">
                        <div class="panel panel-info">
                            <div class="panel-heading">Information</div>
                            <div class="panel-body">
                                <ul class="list-group">
                                    <li class="list-group-item"><b>Order ID:</b>{{:order.id}}
                                        </li>
                                    <li class="list-group-item"><b>Date:</b>{{:order.executionDate}}
                                        </li>
                                    <li class="list-group-item"><b>Service
                                        type:</b> {{:order.serviceType.name}}</li>
                                    <li class="list-group-item"><b>Car class:</b> {{:order.carClass}}
                                        </li>
                                    <li class="list-group-item"><b>Driver sex:</b> {{:order.driverSex}}
                                        </li>
                                    <li class="list-group-item"><b>Method of
                                        payment:</b> {{:order.paymentType}}</li>
                                    <li class="list-group-item"><b>Cost of
                                        payment:</b> {{:order.totalPrice}} UAH
                                        </li>
                                    <li class="list-group-item"><b>Comment:</b> {{:order.comment}}
                                        </li>
                                    <li class="list-group-item"><b>Complete:</b> {{:complete}}
                                        </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="panel panel-info">
                            <div class="panel-heading">Additional options:</div>
                            <div class="panel-body">
                                <ul class="list-group">
                                    {{for order.features}}
                                        <li class="list-group-item"><b>{{:featureName}}: </b>
                                            <span class="glyphicon glyphicon-ok-sign"></span>
                                        </li>
                                    {{/for}}
                                </ul>
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
                                            <th>Pick-up address</th>
                                            <th>Destination address</th>
                                            {{if order.serviceType.isDestinationRequired === true}}
                                                <th>Distance</th>
                                            {{/if}}
                                            <th>Price</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    {{for assembledRoutes}}
                                        <tr>
                                            <td>{{:sourceAddress}}</td>
                                            <td>{{:destinationAddress}}</td>
                                            {{if #parent.parent.data.order.serviceType.isDestinationRequired === true}}
                                                <td>{{:distance}}</td>
                                            {{/if}}
                                            <td>{{:totalPrice}}</td>
                                            <td>{{:finishedCars}}/{{:totalCars}}</td>
                                        </tr>
                                    {{/for}}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="panel panel-info">
                            <div class="panel-heading">Map</div>
                            <div class="panel-body">
                                <!--<iframe src="https://www.google.com/maps/embed?pb=!1m14!1m8!1m3!1d5081.08804691991!2d30.4596392!3d50.4495934!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0000000000000000%3A0xbc67207d8e291fd0!2z0L_QsNGA0Log0JrQuNGX0LLRgdGM0LrQvtCz0L4g0L_QvtC70ZbRgtC10YXQvdGW0YfQvdC-0LPQviDRltC90YHRgtC40YLRg9GC0YM!5e0!3m2!1sru!2sua!4v1430052929432"
                                        width="430" height="280" frameborder="0" style="border:0;"></iframe>-->
                            </div>
                        </div>
                    </div>
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
<div class="container">
    <div class="jumbotron">
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="row">
                    <div class="col-sm-1">
                        <div class="dropdown">
                            <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Sort by
                                <span class="caret"></span>
                            </button>
                            <ul id="sort-menu" class="dropdown-menu" role="menu">
                                <c:forEach items="${allowedSortProperties}" var="prop">
                                    <li role="presentation">
                                        <c:set var="isSelected" value="${selectedSorts.contains(prop.key)}"/>
                                        <a ${isSelected ? "class=\"selected-property\"" : ""}
                                                role="menuitem" href="#" data-property="${prop.key}">${prop.value}
                                            <c:if test="${isSelected}">
                                                <span id="ok-glyph" class="glyphicon glyphicon-ok"></span>
                                            </c:if>
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="row">
                            <div class="col-sm-4 col-sm-offset-1">
                                <div class="input-group">
                            <span class="input-group-addon" id="basic-addon1">
                                <span class="glyphicon glyphicon glyphicon-calendar"></span>
                            </span>
                                    <input id="date-from" type="text" class="form-control" placeholder="From"
                                           aria-describedby="basic-addon1">
                                </div>
                            </div>
                            <div class="col-sm-4 col-sm-offset-1">
                                <div class="input-group">
                            <span class="input-group-addon" id="basic-addon2">
                                <span class="glyphicon glyphicon glyphicon-calendar"></span>
                            </span>
                                    <input id="date-to" type="text" class="form-control" placeholder="To"
                                           aria-describedby="basic-addon2">
                                </div>
                            </div>
                            <div class="col-sm-2">
                                <button id="date-apply-button" class="btn btn-default" type="submit">Apply</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="panel-body">
                <div id="items-container"></div>
                <ul id="pagination" class="pagination">
                </ul>
            </div>
        </div>
    </div>
</div>
<hr>

<p>&#169 TeamD 201</p>
</body>
</html>