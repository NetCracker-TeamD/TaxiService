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
                                <div class="btn-group" id="tariffType">
                                    <select id="pick_tariff_type">
                                        <option value="DAY_OF_YEAR">Daily tariff</option>
                                        <option value="DAY_OF_WEEK">Weekly tariff</option>
                                        <option value="TIME_OF_DAY">Tariff by time</option>
                                    </select>
                                </div>
                                <br><br>

                                <div class="btn-group input-daterange form-group">
                                    <div class="col-lg-5" style="padding-left: 0px">
                                        <label for="newStart" class="control-label">Start of period </label>

                                        <div class="container">
                                            <div class="row">
                                                <div class='col-sm-5'>
                                                    <div class="form-group">
                                                        <div class='input-group date' id='newStart'>
                                                            <input type='text' class="form-control" id="from_create"/>
                    <span class="input-group-addon">
                        <span class="glyphicon glyphicon-calendar"></span>
                    </span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <script>

                                    </script>
                                    <br><br><br><br>

                                    <div class="col-lg-5" style="padding-left: 0px">
                                        <label for="newEnd" class="control-label">End of period </label>

                                        <div class="container">
                                            <div class="row">
                                                <div class='col-sm-5'>
                                                    <div class="form-group">
                                                        <div class='input-group date' id='newEnd'>
                                                            <input type='text' class="form-control" id="to_create"/>
                    <span class="input-group-addon">
                        <span class="glyphicon glyphicon-calendar"></span>
                    </span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
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
            <c:forEach var="tariff" items="${tariffs}">
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
                                                <div class="btn-group" id="tariffTypeEdit">
                                                    <a class="btn btn-default dropdown-toggle btn-select"
                                                       data-toggle="dropdown" id="tariff_type_update">
                                                            ${tariff.tariffType} <span class="caret"></span>
                                                    </a>
                                                    <ul class="dropdown-menu"> 
                                                        <li><a>Daily tariff</a></li>
                                                        <li><a>Weekly tariff</a></li>
                                                        <li><a>The tariff by time</a></li>
                                                    </ul>
                                                     
                                                </div>
                                                <br>

                                                <div class="btn-group input-daterange form-group">
                                                    <div class="col-lg-5" style="padding-left: 0px">
                                                        <label for="updateStart"
                                                               class="control-label">Start of period </label>

                                                        <div class="container">
                                                            <div class="row">
                                                                <div class='col-sm-5 style="padding-left: 0px'>
                                                                    <div class="form-group">
                                                                        <div class='input-group date' id='updateStart'>
                                                                            <input type='text' class="form-control"
                                                                                   value="${tariff.from.time}"
                                                                                   id="from_date_update"
                                                                                   name="startDate"/>
                                                                             <span class="input-group-addon">
                                                                                     <span class="glyphicon glyphicon-calendar"></span>
                                                                             </span>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <br><br><br><br>

                                                    <div class="col-lg-5" style="padding-left: 0px">
                                                        <label for="updateEnd" class="control-label">End of
                                                            period </label>

                                                        <div class="container">
                                                            <div class="row">
                                                                <div class='col-sm-5'>
                                                                    <div class="form-group">
                                                                        <div class='input-group date' id='updateEnd'>
                                                                            <input type='text' class="form-control"
                                                                                   value="${tariff.to.time}"
                                                                                   name="endDate"
                                                                                   id="to_date_update"/>
                                                                            <span class="input-group-addon">
                                                                                    <span class="glyphicon glyphicon-calendar"></span>
                                                                            </span>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

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
                                        <input type="hidden" name="tariff_id" value="${tariff.id}">
                                        <button class="btn btn-default" type="button" data-dismiss="modal">Close
                                        </button>
                                        <button class="btn btn-primary update_rec" data-dismiss="modal" type="button">
                                            Remove
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
                                        <div class="well carousel-search hidden-sm">
                                                <h3 align="center">Are you sure?</h3>

                                        </div>
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
                <c:forEach begin="1" end="${pages}" var="i">
                    <c:choose>
                        <c:when test="${(param.page==null)&&(i==1)}">
                            <li class="active"><a href="?page=${i}">${i}</a></li>
                        </c:when>
                        <c:when test="${param.page==i}">
                            <li class="active"><a href="?page=${i}">${i}</a></li>
                        </c:when>
                        <c:when test="${param.page!=i}">
                            <li><a href="?page=${i}">${i}</a></li>
                        </c:when>
                    </c:choose>
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
<script type="application/javascript" src="/pages/resources/project/js/admin/tariffByTime.js"></script>
</body>

</html>