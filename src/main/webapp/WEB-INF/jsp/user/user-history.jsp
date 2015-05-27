<%@ page import="org.springframework.data.domain.Page" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Orders</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/jquery/css/jquery-ui.css">
    <link rel="stylesheet" href="/pages/resources/project/css/admin.css">
    <link rel="stylesheet" href="/pages/user/css/menu.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/jquery/jquery-ui.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>
    <script src="/pages/resources/project/js/history/history.js" type="text/javascript"></script>
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
            },
            hidden: {
                userId: ${userId}
            }
        };
    </script>
    <script id="orderItemTemplate" type="text/x-jsrender">
        <tr>
            <td>{{:order.id}}</td>
            <td>{{:order.registrationDate}}</td>
            <td>{{:order.executionDate}}</td>
            <td>{{:order.serviceType.name}}</td>
            <td>{{: order.driverSex ? ~initCap(order.driverSex) : "Any"}}</td>
            <td>{{:~initCap(order.paymentType)}}</td>
            <td>{{:totalPrice}}</td>
            <td><button type="button" class="btn {{:~getButtonClass(status)}}">{{:~normalize(status)}}</button></td>
        </tr>





    </script>
</head>

<body>
<!--common navigation bar for this service -->
<jsp:include page="header.jsp"/>

<div class="container" id="main_container">
    <div style="margin-top: 10px" class="panel panel-default">
        <div class="panel-heading">
            <h2 style="margin:0; font-size: 30px" class="panel-title">Orders</h2></div>
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
                <div class="col-sm-11">
                    <div class="row">
                        <div class="col-sm-3 col-sm-offset-2">
                            <div class="input-group">
                            <span class="input-group-addon" id="basic-addon1">
                                <span class="glyphicon glyphicon glyphicon-calendar"></span>
                            </span>
                                <input id="date-from" type="text" class="form-control" placeholder="From"
                                       aria-describedby="basic-addon1">
                            </div>
                        </div>
                        <div class="col-sm-3 col-sm-offset-1">
                            <div class="input-group">
                            <span class="input-group-addon" id="basic-addon2">
                                <span class="glyphicon glyphicon glyphicon-calendar"></span>
                            </span>
                                <input id="date-to" type="text" class="form-control" placeholder="To"
                                       aria-describedby="basic-addon2">
                            </div>
                        </div>
                        <div class="col-sm-2 col-sm-offset-1">
                            <button id="date-apply-button" class="btn btn-default" type="submit">Apply</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <%--    <div class="table-responsive">--%>
        <table class="table table-striped table-hover">
            <thead>
            <tr>
                <th>#</th>
                <th>Registration date</th>
                <th>Execution date</th>
                <th>Service type</th>
                <th>Driver sex</th>
                <th>Payment type</th>
                <th>Price</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody id="order-table-body">
            </tbody>
        </table>
        <div id="info"></div>
        <!--Pagination start-->
        <nav align="center">
            <ul id="pagination" class="pagination">
            </ul>
        </nav>
        <!--Pagination end-->
        <%--    </div>--%>
    </div>
    <hr/>
    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>
</body>

</html>