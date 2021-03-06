<%--
  Created by IntelliJ IDEA.
  User: anton
  Date: 5/25/15
  Time: 12:54 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<nav class="navbar navbar-inverse">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="#">SmartTaxi</a>
    </div>
    <div id="navbar" class="collapse navbar-collapse">
        <ul class="nav navbar-nav" data-type="menu-list">
            <li><a href="/about">About</a></li>
            <li><a href="/order">Make order</a></li>
            <sec:authorize access="hasRole('ROLE_CUSTOMER')">
                <li><a href="/user/history">View order history</a></li>
                <li><a href="/user/account">Edit account</a></li>
                <li><a href="/user/group">Statistic</a></li>
            </sec:authorize>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <sec:authorize access="isAnonymous()">
                <li><a href="/login"><span
                        class="glyphicon glyphicon-log-in"></span>&nbsp;Sign In</a></li>
                <li><a href="/register"><span
                        class="glyphicon glyphicon-user"></span>&nbsp;Sign Up</a></li>
            </sec:authorize>
            <sec:authorize access="isAuthenticated()">
                <li><a href="/logout"><span class="glyphicon glyphicon-log-out"></span>&nbsp;Logout</a></li>
            </sec:authorize>
        </ul>
    </div>
    <script>
        $(function () {
            /*console.log('dododod');*/
            var path = window.location.pathname;
            /*console.log(path);*/
            var links = $('a', $('#navbar'));
            for (var i in links) {
                /*console.log(links[i]);*/
                var href = $(links[i]).attr('href');
                /*console.log(href);*/
                if (path.indexOf(href) != -1) {
                    $(links[i]).parent().addClass('active');
                    /*console.log('match');
                     console.log(links[i]);*/
                    break;
                }
            }
        });
    </script>
</nav>
