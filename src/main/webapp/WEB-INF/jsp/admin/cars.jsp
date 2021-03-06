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
<%@ include file="../../jsp/admin/header.jsp" %>

<div class="modal fade" id="create_car" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">New Car</h4>
            </div>
            <div class="modal-body">
                <form>
                    <div class="form-group">
                        <label for="car_model" class="control-label">Model:</label>
                        <div id="modelNameError" class="alert alert-danger alert-dismissible modal-error hidden">
                            <p></p>
                        </div>
                        <input onclick="" type="text" class="form-control" id="car_model">
                    </div>
                    <div class="form-group">
                        <label class="control-label">Class:</label>
                        <div id="classIdError" class="alert alert-danger alert-dismissible modal-error hidden">
                            <p></p>
                        </div>
                        <select id="car_class" class="form-control">
                            <option value="3">Bussines</option>
                            <option value="2">Standard</option>
                            <option value="1">Economy</option>
                            <option value="4">Cargo</option>
                        </select>
                    </div>
                    <ul class="list-group">
                        <label class="control-label">Features:</label>
                        <div id="mapFeaturesError" class="alert alert-danger alert-dismissible modal-error hidden">
                            <p></p>
                        </div>
                        <li class="list-group-item">
                            <div id="car_features_generated">
                            </div>
                        </li>
                    </ul>
                    <div class="form-group">
                        <label  class="control-label">Category:</label>
                        <div id="categoryError" class="alert alert-danger alert-dismissible modal-error hidden">
                            <p></p>
                        </div>
                        <select id="car_category" class="form-control">
                            <option value="A">A</option>
                            <option value="B" selected="selected">B</option>
                            <option value="C">C</option>
                            <option value="D">D</option>
                        </select>
                    </div>
                    <ul class="list-group">
                        <label class="control-label">Car enabled:</label>
                        <div id="enableError" class="alert alert-danger alert-dismissible modal-error hidden">
                            <p></p>
                        </div>
                        <li class="list-group-item">
                            <div class="checkbox">
                                <label>
                                    <input id="car_enable" type="checkbox" onclick="switcherFeatures(this)" value="false">
                                    Enable
                                </label>
                            </div>
                        </li>
                    </ul>
                    <div class="form-group">
                        <label for="car_driver" class="control-label">Driver:</label>
                        <div id="driverIdError" class="alert alert-danger alert-dismissible modal-error hidden">
                            <p></p>
                        </div>
                        <select id="car_driver" load="true" loadByChange="true" class="form-control"  onclick="generationDrivers(this, 'No driver','')" onchange="changeDriver(this)">

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

<div class="modal fade centered-modal" id="remove_car" reloadPage="true" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Remove car</h4>
            </div>
            <div class="modal-body">
                <div class="alert alert-danger alert-dismissible modal-error">
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
                        onclick="removeCar($('#remove_car').find('[name=\'car_id\']').val())">Remove Car
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade centered-modal" id="successModal" reloadPage="true" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Success</h4>
            </div>
            <div class="modal-body">
                <p class="lead">Successful operation</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-success" data-dismiss="modal">Ok</button>
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
                    <label for="sort-input">Sort</label>
                    <select class="form-control input-sm" id="sort-input" name="order" onchange="form.submit()">
                        <option value="model">by Model</option>
                        <option value="driver">by Driver</option>
                        <option value="class">by Class</option>
                        <option value="category">by Category</option>
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
                <tr style="display: none;">
                    <td colspan="10" style="padding: 0px;">
                        <div id="update_errors_${car.carId}" class="alert alert-danger alert-dismissible modal-error"  style="margin-bottom:0px">
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <%=++num%>
                    </td>
                    <td car-id="${car.carId}" id="${car.carId}">${car.model}</td>
                    <td>${car.category}</td>
                    <td>
                    ${car.carClass.className}
                        <p class="hidden">${car.carClass.id}</p>
                    </td>
                    <c:forEach var="feature" items="${carFeatures}">
                        <td>
                        <span id="${feature.id}" class="glyphicon <c:choose><c:when test="${car.features.contains(feature)}">glyphicon-ok glyphicon-yes</c:when><c:otherwise>glyphicon-remove glyphicon-no</c:otherwise></c:choose>"
                              aria-hidden="true"></span>
                        </td>
                    </c:forEach>
                    <td>
                        <span  class="glyphicon <c:choose><c:when test="${car.isEnabled()}">glyphicon-ok glyphicon-yes enable</c:when><c:otherwise>glyphicon-remove glyphicon-no enable</c:otherwise></c:choose>"
                              aria-hidden="true"></span>
                    </td>
                    <td driver-id="${car.driver.id}"><a href="">${car.driver.lastName} ${car.driver.firstName}</a></td>
                    <td>
                        <button title="Edit" type="button" onclick="startEditCar(event)" data-toggle="modal"
                                data-target="#"
                                data-car-id="${car.carId}" class="btn btn-default btn-xs" aria-label="Left Align">
                            <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
                        </button>
                        <button title="Remove" type="button" data-toggle="modal" data-target="#remove_car"
                                data-car-id="${car.carId}" class="btn btn-default btn-xs" aria-label="Left Align">
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
                <li <c:if test="${page.isFirst()}">class="disabled" onclick="return false"</c:if>>
                    <a href="/admin/cars?page=${page.number-1}&order=${order}" title="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <c:forEach var="elem" items="${pagination}">
                    <li <c:if test="${page.number == elem}">class="active"</c:if>>
                        <a href="/admin/cars?page=${elem}&order=${order}">${elem+1}</a>
                    </li>
                </c:forEach>
                <li <c:if test="${page.isLast()}">class="disabled" onclick="return false"</c:if>>
                    <a href="/admin/cars?page=${page.number+1}&order=${order}" title="Next">
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
<script>selectOrder('${order}');</script>
</body>

</html>