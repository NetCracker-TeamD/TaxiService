<%@ page import="org.springframework.data.domain.Page" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Tariffs by time</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <link rel="stylesheet" href="../../pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="../../pages/resources/project/css/admin.css">
    <link rel="stylesheet" href="../../pages/resources/bootstrap/css/datepicker.css">
    <script src="../../pages/resources/jquery/jquery-2.1.3.js"></script>

    <!-- Moment -->
    <script type="text/javascript" src="/pages/user/frameworks/js/moment-with-locales.min.js"></script>
    <!-- Bootstrap + plugins -->
    <script type="text/javascript" src="/pages/user/frameworks/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/pages/user/frameworks/js/transition.js"></script>
    <script type="text/javascript" src="/pages/user/frameworks/js/collapse.js"></script>
    <script type="text/javascript" src="/pages/user/frameworks/js/tooltip.js"></script>
    <script type="text/javascript" src="/pages/user/frameworks/js/bootstrap-datetimepicker.min.js"></script>
    <script type="text/javascript" src="/pages/user/frameworks/js/bootstrap-dialog.min.js"></script>
    <script type="text/javascript" src="/pages/user/frameworks/js/jquery-validate.bootstrap-tooltip.min.js"></script>
    <script src="/pages/resources/jquery/jquery-ui.js"></script>
    <link rel="stylesheet" href="/pages/resources/jquery/css/jquery-ui.css">


    <script src="../../pages/resources/project/js/admin/tariffByTime.js"></script>
</head>

<body>

<%@ include file="../../jsp/admin/header.jsp" %>


<div class="container" id="main_container">
    <h2 class="sm-hr">Tariffs by time</h2>


    <div class="sm-hr">
        <button type="button" class="btn btn-success btn-sm btn_create" data-toggle="createModel">
            <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> New Tariff
        </button>
        <div class="modal fade createModel" itabindex="-1" role="dialog">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button class="close" type="button" data-dismiss="modal">x</button>
                        <h4 class="modal-title">${tariff.tariffType}</h4>
                    </div>
                    <div class="modal-body">
                        <form>
                            <div class="well carousel-search hidden-sm">
                                <div class="alert alert-danger inform hide"></div>
                                <div class="form-group">
                                    <label for="pick_tariff_type" class="control-label">Tariff type</label>
                                    <select class="form-control" id="pick_tariff_type">
                                        <option value="DAY_OF_YEAR">Daily tariff</option>
                                        <option value="DAY_OF_WEEK">Weekly tariff</option>
                                        <option value="TIME_OF_DAY">Tariff by time</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="start" class="control-label">Start of period </label>
                                    <br>
                                    <input type="text" id="start" name="startDate" class="form-control"/>
                                    <input type="time" id="start_time" name="startTime" class="form-control"/>
                                    <select class="form-control" id="pick_day_start">
                                        <option value="SUNDAY">Sunday</option>
                                        <option value="MONDAY">Monday</option>
                                        <option value="TUESDAY">Tuesday</option>
                                        <option value="WEDNESDAY">Wednesday</option>
                                        <option value="THURSDAY">Thursday</option>
                                        <option value="FRIDAY">Friday</option>
                                        <option value="SATURDAY">Saturday</option>
                                    </select>
                                    <br>
                                    <label for="end" class="control-label">End of period </label>
                                    <br>
                                    <input type="text" id="end" name="endDate" class="form-control"/>
                                    <input type="time" id="end_time" name="startTime" class="form-control"/>
                                    <select class="form-control" id="pick_day_end">
                                        <option value="SUNDAY">Sunday</option>
                                        <option value="MONDAY">Monday</option>
                                        <option value="TUESDAY">Tuesday</option>
                                        <option value="WEDNESDAY">Wednesday</option>
                                        <option value="THURSDAY">Thursday</option>
                                        <option value="FRIDAY">Friday</option>
                                        <option value="SATURDAY">Saturday</option>
                                    </select>

                                </div>
                                <div class="form-group">
                                    <label for="price_coef_create" class="control-label">Price coefficient</label>
                                    <input placeholder="price" id="price_coef_create" class="price form-control"
                                           name="price_coefitient" type="text">
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-default" type="button" data-dismiss="modal">Close</button>
                        <button class="btn btn-primary create_rec" data-dismiss="modal" type="button">Create</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="table-responsive">
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>#</th>
                <th>From</th>
                <th>To</th>
                <th>Tariff type</th>
                <th>Price coefficient</th>
                <th>Manage</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="tariff" items="${tariffs.content}">
                <tr>
                    <td>
                            ${tariff.id}
                    </td>
                    <c:choose>
                        <c:when test="${tariff.tariffType=='DAY_OF_YEAR'}">
                            <td>
                                <fmt:setLocale value="en_US" scope="session"/>
                                <fmt:formatDate value="${tariff.from.time}" pattern="MMM, dd"/>
                            </td>
                            <td>
                                <fmt:setLocale value="en_US" scope="session"/>
                                <fmt:formatDate value="${tariff.to.time}" pattern="MMM, dd"/>
                            </td>
                            <td>
                                Daily tariff
                            </td>
                        </c:when>
                        <c:when test="${tariff.tariffType=='TIME_OF_DAY'}">
                            <td>
                                <fmt:formatDate value="${tariff.from.time}" type="time"/>
                            </td>
                            <td>
                                <fmt:formatDate value="${tariff.to.time}" type="time"/>
                            </td>
                            <td>
                                The tariff by time
                            </td>
                        </c:when>
                        <c:when test="${tariff.tariffType=='DAY_OF_WEEK'}">
                            <td>
                                <fmt:setLocale value="en_US" scope="session"/>
                                <fmt:formatDate value="${tariff.from.time}" pattern="EEEE"/>
                            </td>
                            <td>
                                <fmt:setLocale value="en_US" scope="session"/>
                                <fmt:formatDate value="${tariff.to.time}" pattern="EEEE"/>

                            </td>
                            <td>
                                Weekly tariff
                            </td>
                        </c:when>
                    </c:choose>
                    <td>${tariff.price}</td>
                    <td>
                        <a role="button"
                           class="btn btn-default btn_edit" data-toggle="editModal"><span
                                class="glyphicon glyphicon-edit" aria-hidden="true"></span></a>

                        <div class="modal fade editModal" itabindex="-1" role="dialog">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button class="close" type="button" data-dismiss="modal">x</button>
                                        <h4 class="modal-title" id="update">${tariff.tariffType}</h4>
                                    </div>
                                    <div class="modal-body">
                                        <form>
                                            <div class="well carousel-search hidden-sm">
                                                <div class="form-group">
                                                    <label for="price_coef_update" class="control-label">Price
                                                        coefficient</label>
                                                    <input value="${tariff.price}" id="price_coef_update"
                                                           class="form-control" name="price_coefitient" type="text"
                                                            />
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="modal-footer">
                                        <input type="hidden" name="tariff_type" id="tariff_update"
                                               value="${tariff.tariffType}">
                                        <input type="hidden" name="tariff_id" id="id_update" value="${tariff.id}">
                                        <input type="hidden" name="tariff_from" id="from_update"
                                               value="${tariff.from.time}">
                                        <input type="hidden" name="tariff_to" id="to_update" value="${tariff.to.time}">
                                        <button class="btn btn-default" type="button" data-dismiss="modal">Close
                                        </button>
                                        <button class="btn btn-primary save_update" data-dismiss="modal" type="button">
                                            Save
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <a role="button"
                           class="btn btn-default btn_remove" data-toggle="removeModal"><span
                                class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>

                        <div class="modal fade removeModal" itabindex="-1" role="dialog">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-body">

                                            <h3 class="text-center">Are you sure?</h3>

                                    </div>
                                    <div class="modal-footer">
                                        <input type="hidden" name="tariff_type" value="${tariff.tariffType}">
                                        <input type="hidden" name="tariff_id" value="${tariff.id}">
                                        <input type="hidden" name="tariff_price" value="${tariff.price}">
                                        <input type="hidden" name="tariff_from" value="${tariff.from.time}">
                                        <input type="hidden" name="tariff_to" value="${tariff.to.time}">
                                        <button class="btn btn-default" type="button" data-dismiss="modal">Close
                                        </button>
                                        <button class="btn btn-primary remove_rec" data-dismiss="modal" type="button">
                                            Remove
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <!--Pagination start-->
        <nav align="center">
            <ul class="pagination">
                <c:forEach begin="1" end="${tariffs.totalPages}" var="i">
                    <li ${i == tariffs.number + 1 ? "class=\"active\"" : ""}>
                        <a href="?page=${i}">${i}</a>
                    </li>
                </c:forEach>
            </ul>
        </nav>
        <!--Pagination end-->
    </div>

    <hr/>
    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>
</body>

</html>