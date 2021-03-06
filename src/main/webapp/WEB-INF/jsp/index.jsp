<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- <link rel="icon" href="../../favicon.ico"> -->
    <title>Taxi Service</title>

    <!-- Loader css -->
    <link href="/pages/user/css/loader.css" rel="stylesheet">

    <!-- Bootstrap core CSS -->
    <link href="/pages/user/frameworks/css/bootstrap.min.css" rel="stylesheet">
    <link href="/pages/user/frameworks/css/bootstrap-datetimepicker.min.css" rel="stylesheet">
    <link href="/pages/user/frameworks/css/bootstrap-dialog.min.css" rel="stylesheet">
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <!-- app css -->
    <link rel="stylesheet" type="text/css" href="/pages/user/css/menu.css">
</head>
<body>
<div id="loader">
    <div>
        <div class="anim">
            <div class="rect1"></div>
            <div class="rect2"></div>
            <div class="rect3"></div>
            <div class="rect4"></div>
            <div class="rect5"></div>
        </div>
        <p class="text">App is loading, please wait</p>
    </div>
</div>

<!-- JavaScript frameworks
================================================== -->
<!-- jQuery + plugins -->
<script type="text/javascript" src="/pages/user/frameworks/js/jquery-1.11.2.min.js"></script>
<script type="text/javascript" src="/pages/user/frameworks/js/jquery.maskedinput.min.js"></script>
<script type="text/javascript" src="/pages/user/frameworks/js/jquery.numeric.min.js"></script>
<script type="text/javascript" src="/pages/user/frameworks/js/jquery.validate.min.js"></script>
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
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script type="text/javascript" src="/pages/user/frameworks/js/ie10-viewport-bug-workaround.js"></script>
<script type="text/javascript" src="/pages/user/frameworks/js/moment-with-locales.min.js"></script>
<!-- Google Map Api -->
<script type="text/javascript"
        src="http://maps.googleapis.com/maps/api/js?v=3&key=AIzaSyBUz7000chLRCxGpF9VilUhXv6y-Sl_t6c&sensor=false&libraries=places"></script>

<!-- app links -->
<script type="text/javascript" src="/pages/user/js/tools.js"></script>
<script type="text/javascript" src="/pages/user/js/loader.js"></script>
<script type="text/javascript" src="/pages/user/js/map_tools.js"></script>
<script type="text/javascript" src="/pages/user/js/data_tools.js"></script>
<script type="text/javascript" src="/pages/user/js/templates.js"></script>
<script type="text/javascript" src="/pages/user/js/app.js"></script>
</body>
</html>
