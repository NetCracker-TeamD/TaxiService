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
<%--<%@ include file="../../pages/admin/admin-header.html" %>--%>
<%@ include file="../../jsp/admin/header.jsp" %>

<div class="modal fade" id="create_driver" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">New Driver</h4>
            </div>
            <div class="modal-body">
                <div class="alert alert-danger alert-dismissible modal-error"
                     onclick="hideErrorModal($('#create_driver'))">
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

                        <div class="input-group">
                            <div class="input-group-addon">@</div>
                            <input type="text" class="form-control" id="driver_mail">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="driver_phone" class="control-label">Phone Number:</label>
                        <input type="text" class="form-control" id="driver_phone">
                    </div>
                    <label class="radio-inline">
                        <input type="radio" name="driver_gender" id="driver_sex_male" value="MALE" checked="checked">
                        Male
                    </label>
                    <label class="radio-inline">
                        <input type="radio" name="driver_gender" id="driver_sex_female" value="FEMALE"> Female
                    </label>

                    <div class="checkbox">
                        <label>
                            <input id="driver_enabled" type="checkbox" checked="checked">
                            Enabled
                        </label>
                    </div>
                    <div class="checkbox">
                        <label>
                            <input id="driver_at_work" type="checkbox" checked="checked">
                            At Work
                        </label>
                    </div>
                    <hr/>
                    <%--Driver features start--%>
                    <c:forEach var="feauture" items="${driverFeatures}">
                        <div class="checkbox">
                            <label>
                                <input class="feature" type="checkbox" value="${feauture.id}">
                                    ${feauture.name}
                            </label>
                        </div>
                    </c:forEach>
                    <%--Driver features end--%>
                    <hr/>
                    <div class="form-group">
                        <label for="driver_license_serial" class="control-label">License Serial:</label>
                        <input type="text" class="form-control" id="driver_license_serial"
                               data-toggle="tooltip" data-placement="top" title="License example: A123456">
                    </div>
                    <div class="form-group">
                        <label for="driver_car" class="control-label">Car:</label>
                        <%--<select id="driver_car" class="form-control" onclick="getFreeCars()"></select>--%>
                        <select id="driver_car" comleted="false" class="form-control"></select>
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
                <div class="alert alert-danger alert-dismissible modal-error">
                    <p>Error-Message</p>
                </div>
                <form>
                    <input type="hidden" name="driver_id"/>
                </form>
                <p class="lead">Are you really want to remove this driver account?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger"
                        onclick="removeDriver($('#remove_driver').find('[name=\'driver_id\']').val())">Remove Account
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
                <%--onclick="removeCar($('#remove_car').find('[name=\'car_id\']').val())"--%>>Remove Car
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade centered-modal" id="unbind_car" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Unbind car</h4>
            </div>
            <div class="modal-body">
                <div class="alert alert-danger alert-dismissible modal-error">
                    <p>Error-Message</p>
                </div>
                <form>
                    <input type="hidden" name="driver_id"/>
                </form>
                <p class="lead">Are you really want to release this car?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-info"
                        onclick="unbindCar($('#unbind_car').find('[name=\'driver_id\']').val())">Release Car
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade centered-modal" id="successModal" tabindex="-1" role="dialog" aria-hidden="true">
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
    <h2 class="sm-hr">Driver</h2>

    <div class="sm-hr">
        <button type="button" class="btn btn-success btn-sm" data-toggle="modal" data-target="#create_driver">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> New Driver
        </button>
        <form class="form-inline pull-right ">
            <div class="form-group">
                <form method="get">
                    <label for="sort-input">Sort</label>
                    <select class="form-control input-sm" id="sort-input" name="order" onchange="form.submit()">
                        <option value="last_name">by Last Name</option>
                        <option value="first_name">by First Name</option>
                        <option value="sex">by Sex</option>
                        <option value="enabled">by Enabled</option>
                        <option value="at_work">by At Work</option>
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
                <th>Enabled</th>
                <th>At work</th>
                <th>Car model</th>
                <th>license</th>
            </tr>
            </thead>
            <tbody>
            <%int num = ((Page) request.getAttribute("page")).getSize() * ((Page) request.getAttribute("page")).getNumber();%>
            <c:forEach var="driver" items="${page.content}">
                <tr
                        <c:if test="${driver.car == null}">class="warning"</c:if>
                        onclick="openDriverInfo(event,${driver.id})">
                    <td>
                        <%=++num%>
                    </td>
                    <td driver-id="${driver.id}">${driver.lastName}</td>
                    <td>${driver.firstName}</td>
                    <td><a href="mailto:#">${driver.email}</a></td>
                    <td>${driver.phoneNumber}</td>
                    <td>${driver.sex}</td>
                    <td>
                        <span class="glyphicon <c:choose><c:when test="${driver.isEnabled()}">glyphicon-ok glyphicon-yes</c:when><c:otherwise>glyphicon-remove glyphicon-no</c:otherwise></c:choose>"
                              aria-hidden="true"></span>
                    </td>
                    <td>
                        <span class="glyphicon <c:choose><c:when test="${driver.isAtWork()}">glyphicon-ok glyphicon-yes</c:when><c:otherwise>glyphicon-remove glyphicon-no</c:otherwise></c:choose>"
                              aria-hidden="true"></span>
                    </td>
                    <td car-id="${driver.car.carId}">${driver.car.model}</td>
                    <td>${driver.license}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <!--Pagination start-->
        <nav align="center">
            <ul class="pagination">
                <li <c:if test="${page.isFirst()}">class="disabled" onclick="return false"</c:if>>
                    <a href="/admin/drivers?page=${page.number-1}&order=${order}" title="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <c:forEach var="elem" items="${pagination}">
                    <li <c:if test="${page.number == elem}">class="active"</c:if>>
                        <a href="/admin/drivers?page=${elem}&order=${order}">${elem+1}</a>
                    </li>
                </c:forEach>
                <li <c:if test="${page.isLast()}">class="disabled" onclick="return false"</c:if>>
                    <a href="/admin/drivers?page=${page.number+1}&order=${order}" title="Next">
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
<script>selectOrder('${order}');</script>
</body>

</html>