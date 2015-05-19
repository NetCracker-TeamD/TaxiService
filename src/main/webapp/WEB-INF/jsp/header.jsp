<%--
  Created by IntelliJ IDEA.
  User: anton
  Date: 5/19/15
  Time: 4:29 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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

        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav" data-type="menu-list">
                <li><a href="/about" data-action="about">About</a></li>
                <li class="active"><a href="/make-order" data-action="make-order">Make order</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right" data-type="action-list">
                <li><a href="/login" data-action="login"><span class="glyphicon glyphicon-log-in"></span>&nbsp;Sign
                    In</a></li>
                <li><a href="/register" data-action="register"><span class="glyphicon glyphicon-user"></span>&nbsp;Sign
                    Up</a></li>
            </ul>
        </div>
        <!--/.navbar-collapse -->
    </div>
</nav>
