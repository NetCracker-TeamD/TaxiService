<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap-theme.css">
    <link rel="stylesheet" href="/pages/resources/jquery/css/jquery-ui.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/jquery/jquery-ui.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>
    <script src="/pages/resources/project/js/paging.js" type="text/javascript"></script>
    <title>Taxi-Service</title>
</head>
<body>

<%@ include file="../pages/admin/admin-header.html" %>
<style>
    #center {
        position:fixed;
        top:50%;
        left:15%;
        margin-top: -15%;
        margin-left:-15%;
    }
    #center_text{
        text-align: center;
    }

</style>
<div class="container" id="center">
    <div class="row">
        <div class="container">
            <div class="row">
                <div class="col-md-12">
                    <div class="error-template" id="center_text">
                        <h1>
                            Oops!</h1>

                        <h2>
                            404 Not Found</h2>

                        <div class="error-details">
                            Sorry, an error has occured, Requested page not found!
                        </div>
                        <br><br>
                        <div class="error-actions">
                            <a href="#" class="btn btn-primary btn-lg"><span
                                    class="glyphicon glyphicon-home"></span>
                                Take Me Home </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
