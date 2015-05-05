<%@ page import="org.springframework.data.domain.Page" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Cars</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/project/css/admin.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>

    <!--<script src="../resources/project/js/admin/car.js"></script>-->

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
                <li><a href="#">Users</a></li>
                <li><a href="#">Groups</a></li>
                <li><a href="#">Drivers</a></li>
                <li class="active"><a href="#">Cars</a></li>
                <li><a href="#">Tariffs</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Reports
                        <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="#">Report type 1</a></li>
                        <li><a href="#">Report type 2</a></li>
                        <li><a href="#">Report type 3</a></li>
                    </ul>
                </li>
            </ul>
            <div class="navbar-form navbar-right">
                <button type="button" class="btn btn-primary">Sign out</button>
            </div>
        </div>
    </div>
</nav>

<div class="modal fade centered-modal" id="create_car" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">New Car</h4>
            </div>
            <div class="modal-body">
                <div class="alert alert-danger alert-dismissible modal-error hidden">
                    <p>Error-Message</p>
                </div>
                <form>
                    <div class="form-group">
                        <label for="car_model" class="control-label">Model:</label>
                        <input type="text" class="form-control" id="car_model">
                    </div>
                    <div class="form-group">
                        <label for="car_class" class="control-label">Class:</label>
                        <select id="car_class" class="form-control">
                            <option>Premium</option>
                            <option selected="selected">Standard</option>
                            <option>Cheep</option>
                        </select>
                    </div>
                    <div class="checkbox">
                        <label>
                            <input id="car_wifi" type="checkbox" value="">
                            Wi-fi
                        </label>
                    </div>
                    <div class="checkbox">
                        <label>
                            <input id="car_animal" type="checkbox" value="">
                            Transportation of animals
                        </label>
                    </div>
                    <div class="checkbox">
                        <label>
                            <input id="car_cond" type="checkbox" value="">
                            Conditioner
                        </label>
                    </div>
                    <div class="checkbox">
                        <label>
                            <input id="car_smoke" type="checkbox" value="">
                            Smoking in car
                        </label>
                    </div>
                    <div class="form-group">
                        <label for="car_driver" class="control-label">Driver:</label>
                        <select id="car_driver" class="form-control">
                            <option>No driver</option>
                            <option>Igor Ivan</option>
                            <option>Vasil Vasil</option>
                        </select>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button"
                        onclick="createCar()" class="btn btn-success">Create Car
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade centered-modal" id="remove_car" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Remove car</h4>
            </div>
            <div class="modal-body">
                <div class="alert alert-danger alert-dismissible modal-error hidden">
                    <p>Error-Message</p>
                </div>
                <form>
                    <input type="hidden" name="car_id"/>
                </form>
                <p class="lead">Are you really want to remove this car?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger"
                        onclick="">Remove Car
                </button>
            </div>
        </div>
    </div>
</div>

<div class="container" id="main_container">
    <h2 class="sm-hr">Cars</h2>

    <div class="sm-hr">
        <button type="button" class="btn btn-success btn-sm" data-toggle="modal" data-target="#create_car">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> New Car
        </button>
        <form class="form-inline pull-right ">
            <div class="form-group">
                <form method="get">
                    <label for="sortInput">Sort</label>
                    <select class="form-control input-sm" id="sortInput" name="order" onchange="form.submit()">
                        <option value="modle">by Model</option>
                        <option value="driver">by Driver</option>
                        <option value="driver">by Class</option>
                    </select>
                </form>
            </div>
        </form>
    </div>
    <div class="table-responsive">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>#</th>
                <th>Model</th>
                <th>Category</th>
                <th>Class</th>
                <%--Car features start--%>
                <c:forEach var="feauture" items="${carFeatures}">
                    <th>${feauture.name}</th>
                </c:forEach>
                <%--Car features end--%>
                <th>Enabled</th>
                <th>Driver</th>
                <th>Manage</th>
            </tr>
            </thead>
            <tbody>
            <%int num = ((Page) request.getAttribute("page")).getSize() * ((Page) request.getAttribute("page")).getNumber();%>
            <c:forEach var="car" items="${page.content}">
                <tr>
                    <td><%=++num%></td>
                    <td car-id="${car.carId}">${car.model}</td>
                    <td>${car.category}</td>
                    <td>${car.carClass.className}</td>
                    <c:forEach var="feature1" items="${carFeatures}">
                        <td>
                        <span class="glyphicon <c:choose><c:when test="${car.features.contains(feature1)}">glyphicon-ok glyphicon-yes</c:when><c:otherwise>glyphicon-remove glyphicon-no</c:otherwise></c:choose>"
                              aria-hidden="true"></span>
                        </td>
                    </c:forEach>
                    <td>
                        <span class="glyphicon <c:choose><c:when test="${car.isEnabled()}">glyphicon-ok glyphicon-yes</c:when><c:otherwise>glyphicon-remove glyphicon-no</c:otherwise></c:choose>"
                              aria-hidden="true"></span>
                    </td>
                    <td driver-id="${car.driver.id}"><a href="">${car.driver.lastName} ${car.driver.firstName}</a></td>
                    <td>
                        <button title="Edit" type="button" onclick="startEditCar(event)" data-toggle="modal"
                                data-target="#"
                                data-car-id="" class="btn btn-default btn-xs" aria-label="Left Align">
                            <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
                        </button>
                        <button title="Remove" type="button" data-toggle="modal" data-target="#remove_car"
                                data-car-id="" class="btn btn-default btn-xs" aria-label="Left Align">
                            <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                        </button>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <!--Pagination start-->
        <nav align="center">
            <ul class="pagination">
                <li <c:if test="${page.first}">class="disabled" onclick="return false"</c:if>>
                    <a href="/admin/cars?page=${page.number-1}" title="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <c:forEach var="elem" items="${pagination}">
                    <li <c:if test="${page.number == elem}">class="active"</c:if>>
                        <a href="/admin/cars?page=${elem}">${elem+1}</a>
                    </li>
                </c:forEach>
                <li <c:if test="${page.last}">class="disabled" onclick="return false"</c:if>>
                    <a href="/admin/cars?page=${page.number+1}" title="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
        <!--Pagination end-->
    </div>

    <hr/>
    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>
<script type="application/javascript" src="/pages/resources/project/js/admin/car.js"></script>
</body>

</html>