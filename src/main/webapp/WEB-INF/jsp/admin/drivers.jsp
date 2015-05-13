<%@ page import="org.springframework.data.domain.Page" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Drivers</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/project/css/admin.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>
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
                <li><a href="/admin/drivers">Groups</a></li>
                <li class="active"><a href="#">Drivers</a></li>
                <li><a href="/admin/cars">Cars</a></li>
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

<div class="modal fade" id="create_driver" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">New Driver</h4>
            </div>
            <div class="modal-body">
                <div class="alert alert-danger alert-dismissible modal-error hidden">
                    <p>Error-Message</p>
                </div>
                <form>
                    <div class="form-group">
                        <label for="driver_first_name" class="control-label">First Name:</label>
                        <input type="text" class="form-control" id="driver_first_name">
                    </div>
                    <div class="form-group">
                        <label for="driver_last_name" class="control-label">Last Name:</label>
                        <input type="text" class="form-control" id="driver_last_name">
                    </div>
                    <div class="form-group">
                        <label for="driver_mail" class="control-label">E-Mail:</label>
                        <input type="text" class="form-control" id="driver_mail">
                    </div>
                    <label class="radio-inline">
                        <input type="radio" name="driver_gender" id="driver_gender_man" value="man" checked="checked">
                        Man
                    </label>
                    <label class="radio-inline">
                        <input type="radio" name="driver_gender" id="driver_gender_woman" value="woman"> Woman
                    </label>

                    <div class="checkbox">
                        <label>
                            <input id="driver_smoke" type="checkbox" value="">
                            Smoke
                        </label>
                    </div>
                    <div class="form-group">
                        <label for="driver_license_serial" class="control-label">License Serial:</label>
                        <input type="text" class="form-control" id="driver_license_serial">
                    </div>
                    <div class="form-group">
                        <label for="car_driver" class="control-label">Car:</label>
                        <select id="car_driver" class="form-control">
                            <option selected="selected">No Car</option>
                            <option>Car 1</option>
                            <option>Car 2</option>
                        </select>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button"
                        onclick="createDriver()" class="btn btn-success">Create Driver
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade centered-modal" id="remove_driver" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Remove driver account</h4>
            </div>
            <div class="modal-body">
                <div class="alert alert-danger alert-dismissible modal-error hidden">
                    <p>Error-Message</p>
                </div>
                <form>
                    <input type="hidden" name="car_id"/>
                </form>
                <p class="lead">Are you really want to remove this driver account?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger"
                        onclick="">Remove
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
    <h2 class="sm-hr">Driver</h2>

    <div class="sm-hr">
        <button type="button" class="btn btn-success btn-sm" data-toggle="modal" data-target="#create_driver">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> New Driver
        </button>
        <form class="form-inline pull-right ">
            <div class="form-group">
                <form method="get">
                    <label for="sortInput">Sort</label>
                    <select class="form-control input-sm" id="sortInput" name="order" onchange="form.submit()">
                        <option value="name">by Name</option>
                        <option value="gender">by Gender</option>
                        <option value="license">by license due</option>
                    </select>
                </form>
            </div>
        </form>
    </div>
    <div class="table-responsive">
        <table class="table table-striped table-hover ">
            <thead>
            <tr>
                <th>#</th>
                <th>Last Name</th>
                <th>First Name</th>
                <th>E-mail</th>
                <th>Phone</th>
                <th>Sex</th>
                <%--&lt;%&ndash;Driver features start&ndash;%&gt;--%>
                <%--<c:forEach var="feauture" items="${driverFeatures}">--%>
                    <%--<th>${feauture.name}</th>--%>
                <%--</c:forEach>--%>
                <%--&lt;%&ndash;Driver features end&ndash;%&gt;--%>
                <th>Enabled</th>
                <th>At work</th>
                <th>Car model</th>
                <th>license</th>
            </tr>
            </thead>
            <tbody>
            <%int num = ((Page) request.getAttribute("page")).getSize() * ((Page) request.getAttribute("page")).getNumber();%>
            <c:forEach var="driver" items="${page.content}">
                <tr onclick="openDriverInfo(event,${driver.id})">
                    <td>
                        <%=++num%>
                    </td>
                    <td>${driver.lastName}</td>
                    <td>${driver.firstName}</td>
                    <td><a href="mailto:#">${driver.email}</a></td>
                    <td>${driver.phoneNumber}</td>
                    <td>${driver.sex}</td>
                    <%--<c:forEach var="feature" items="${driverFeatures}">--%>
                        <%--<td>--%>
                        <%--<span class="glyphicon <c:choose><c:when test="${driver.features.contains(feature)}">glyphicon-ok glyphicon-yes</c:when><c:otherwise>glyphicon-remove glyphicon-no</c:otherwise></c:choose>"--%>
                              <%--aria-hidden="true"></span>--%>
                        <%--</td>--%>
                    <%--</c:forEach>--%>
                    <td>
                        <span class="glyphicon <c:choose><c:when test="${driver.isEnabled()}">glyphicon-ok glyphicon-yes</c:when><c:otherwise>glyphicon-remove glyphicon-no</c:otherwise></c:choose>"
                              aria-hidden="true"></span>
                    </td>
                    <td>
                        <span class="glyphicon <c:choose><c:when test="${driver.isAtWork()}">glyphicon-ok glyphicon-yes</c:when><c:otherwise>glyphicon-remove glyphicon-no</c:otherwise></c:choose>"
                              aria-hidden="true"></span>
                    </td>
                    <td>${driver.car.model}</td>
                    <td>${driver.license}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <!--Pagination start-->
        <nav align="center">
            <ul class="pagination">
                <li <c:if test="${page.isFirst()}">class="disabled" onclick="return false"</c:if>>
                    <a href="/admin/drivers?page=${page.number-1}" title="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <c:forEach var="elem" items="${pagination}">
                    <li <c:if test="${page.number == elem}">class="active"</c:if>>
                        <a href="/admin/drivers?page=${elem}">${elem+1}</a>
                    </li>
                </c:forEach>
                <li <c:if test="${page.isLast()}">class="disabled" onclick="return false"</c:if>>
                    <a href="/admin/drivers?page=${page.number+1}" title="Next">
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
<script type="application/javascript" src="/pages/resources/project/js/admin/driver.js"></script>
</body>

</html>