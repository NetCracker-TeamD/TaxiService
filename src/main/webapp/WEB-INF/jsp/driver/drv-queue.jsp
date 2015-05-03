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
                <li><a href="#history">History</a></li>
                <li><a href="current-order.jsp">Current order</a></li>
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

<div class="container">
    <div class="jumbotron row">
        <div class="panel-primary col-md-9">
            <div class="panel-heading">Recent orders</div>
            <table class="table table-hover table-bordered">
                <thead>
                <tr class="info">
                    <th>#</th>
                    <th>Service type</th>
                    <th>Execution Time</th>
                    <th>Payment</th>
                    <th>Class</th>
                    <th>View</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${orderList}" var="order">
                    <tr class="info" data-toggle="collapse" data-target="#collapseExample">
                        <td>${order.id}</td>
                        <td>${order.serviceType}</td>
                        <td>${order.executionDate}</td>
                        <td>${order.paymentType}</td>
                        <td>${order.carClass}</td>
                        <td>
                            <button type="button" data-toggle="modal" data-target="#orderDetail" data-whatever="ID"
                                    class="btn btn-info" title="View details"><i
                                    class="glyphicon glyphicon-eye-open"></i></button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>

            </table>
        </div>

        <div class="col-md-3 pull-right">
            <div class="panel panel-primary">
                <div class="panel-heading"><h3 class="panel-title">Filter</h3></div>

                <div class="panel-body col">
                    <div class="input-group">
                        <div>
                            <label class="radio-inline">
                                <input type="checkbox" name="driver_gender" id="taxi_asap"> Taxi asap
                            </label>
                        </div>
                        <div>
                            <label class="radio-inline">
                                <input type="checkbox" name="sober_driver" id="sober_driver"> Sober driver
                            </label>
                        </div>
                        <div>
                            <label class="radio-inline">
                                <input type="checkbox" name="non_smoking" id="non_smoking" value="non_smoking">
                                Non-smoking
                            </label>
                        </div>
                        <div>
                            <label class="radio-inline">
                                <input type="checkbox" name="wi_fi" id="wi_fi" value="wi_fi"> WiFi
                            </label>
                        </div>
                        <div>
                            <label class="radio-inline">
                                <input type="checkbox" name="cargo_taxi" id="cargo_taxi" value="cargo_taxi"> Cargo Taxi
                            </label>
                        </div>
                        <div>
                            <label class="radio-inline">
                                <input type="checkbox" name="foodstuff_delivery" id="foodstuff_delivery"
                                       value="foodstuff_delivery"> Foodstuff delivery
                            </label>
                        </div>

                    </div>
                    <hr>
                    <div>
                        <button type="submit" class="btn btn-info" aria-label="Left Align">
                            <span class="glyphicon glyphicon-search" aria-hidden="true"> Search</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <hr>
    <!-- Modal -->

    <div class="modal fade" id="orderDetail" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel">Order description</h4>
                </div>
                <div class="modal-body">
                    <div class="panel-primary">
                        <div class="panel-heading">Routes</div>
                        <table class="table table-hover table-bordered">
                            <thead>
                            <tr class="info">
                                <th>#</th>
                                <th>Source address</th>
                                <th>Destination address</th>
                                <th>Distance</th>
                                <th>Accept</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr class="info">
                                <td>6</td>
                                <td>Glushkova str</td>
                                <td>Chevchenka str</td>
                                <td>15 km</td>
                                <td>
                                    <div>
                                        <a class="btn btn-success" href="current-order.jsp"><i
                                                class="glyphicon glyphicon-plus"></i></a>
                                    </div>
                                </td>
                            </tr>

                            <tr class="info">
                                <td>6</td>
                                <td>Glushkova str</td>
                                <td>Maidan Nezaleznosti</td>
                                <td>17 km</td>
                                <td>
                                    <div>
                                        <a class="btn btn-success" href="current-order.jsp"><i
                                                class="glyphicon glyphicon-plus"></i></a>
                                    </div>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="orderDetail1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
         aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel1">Order description</h4>
                </div>
                <div class="modal-body">
                    <div class="panel-primary">
                        <div class="panel-heading">Routes</div>
                        <table class="table table-hover table-bordered">
                            <thead>
                            <tr class="info">
                                <th>#</th>
                                <th>Source address</th>
                                <th>Destination address</th>
                                <th>Distance</th>
                                <th>Accept</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr class="info">
                                <td>8</td>
                                <td>Glushkova str</td>
                                <td>Goncharska str</td>
                                <td>15 km</td>
                                <td>
                                    <div>
                                        <a class="btn btn-success" href="current-order.jsp"><i
                                                class="glyphicon glyphicon-plus"></i></a>
                                    </div>
                                </td>
                            </tr>

                            <tr class="info">
                                <td>8</td>
                                <td>Glushkova str</td>
                                <td>Chevchenka str</td>
                                <td>15 km</td>
                                <td>
                                    <div>
                                        <a class="btn btn-success" href="current-order.jsp"><i
                                                class="glyphicon glyphicon-plus"></i></a>
                                    </div>
                                </td>
                            </tr>

                            <tr class="info">
                                <td>8</td>
                                <td>Glushkova str</td>
                                <td>Maidan Nezaleznosti</td>
                                <td>17 km</td>
                                <td>
                                    <div>
                                        <a class="btn btn-success" href="current-order.jsp"><i
                                                class="glyphicon glyphicon-plus"></i></a>
                                    </div>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>