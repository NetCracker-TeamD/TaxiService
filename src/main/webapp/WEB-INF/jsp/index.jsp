<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/project/css/welcome.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>
    <script src="/pages/resources/project/js/common/authentication.js"></script>
    <script src="/pages/resources/project/js/registration.js"></script>
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
                <li class="active"><a href="#">Home</a></li>
                <li><a href="#about">About</a></li>
                <li><a href="#contact">Contact</a></li>
            </ul>

            <div class="navbar-form navbar-right">
                <button type="button" class="btn btn-success" data-toggle="modal" data-target="#login_modal">Log in
                </button>
                <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#t_and_c_m">Sign up
                </button>
            </div>


        </div>
        <!--/.navbar-collapse -->
    </div>
</nav>

<div class="jumbotron welcome">
    <div class="container">
        <br>
        <br>

        <h1>Welcome to Smart Taxi</h1>

        <p>This is a template for a simple marketing or informational website. It includes a large callout called a
            jumbotron and three supporting pieces of content.
            Use it as a starting point to create something more unique.</p>

        <p><a class="btn btn-primary btn-lg" href="#">Learn more</a></p>
    </div>
</div>

<!--Content and may be button order taxi-->
<div class="container">
    <!-- Example row of columns -->
    <div class="jumbotron">

        <p>A taxicab, also known as a taxi or a cab, is a type of vehicle for hire with a driver,
            used by a single passenger or small group of passengers, often for a non-shared ride.
            A taxicab conveys passengers between locations of their choice.
            This differs from other modes of public transport where the pick-up and drop-off locations are determined by
            the service provider, not by the passenger, although demand responsive transport and share taxis provide a
            hybrid bus/taxi mode.</p>
    </div>

    <hr>

    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>

<!-- Registratin Modal -->
<div class="modal fade" id="t_and_c_m" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">

            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
                <h2 class="modal-title" id="myModalLabel">Registration</h2>
            </div>
            <form:form commandName="registrationForm" action="${pageContext.request.contextPath}/register"
                       method="post">
                <div class="modal-body">
                    <div class="row">
                        <div class="col-xs-12 col-sm-6 col-md-6">
                            <div class="form-group">
                                <form:input path="firstName" placeholder="First Name" cssClass="form-control input-lg"
                                            id="first_name" tabindex="1"/>
                            </div>
                        </div>
                        <div class="col-xs-12 col-sm-6 col-md-6">
                            <div class="form-group">
                                <form:input path="lastName" placeholder="Last Name" cssClass="form-control input-lg"
                                            id="last_name" tabindex="2"/>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <form:input path="email" placeholder="Email Address" id="email" cssClass="form-control input-lg"
                                    tabindex="3"/>
                    </div>
                    <div class="form-group">
                        <form:input path="phoneNumber" placeholder="Phone Number" id="phoneNumber"
                                    cssClass="form-control input-lg" tabindex="4"/>
                    </div>
                    <div class="row">
                        <div class="col-xs-12 col-sm-6 col-md-6">
                            <div class="form-group">
                                <form:password path="password" id="password"
                                               cssClass="form-control input-lg"
                                               placeholder="Password" tabindex="5"/>
                            </div>
                        </div>
                        <div class="col-xs-12 col-sm-6 col-md-6">
                            <div class="form-group">
                                <form:password path="passwordConfirmation" id="password"
                                               cssClass="form-control input-lg"
                                               placeholder="Confirm Password" tabindex="6"/>
                            </div>
                        </div>
                    </div>

                </div>

                <div class="modal-footer">
                    <div>
                        <button id="reg" type="submit" class="btn btn-success">Register</button>
                        <button type="button" class="btn btn-primary" data-dismiss="modal">Cancel</button>
                    </div>
                </div>
            </form:form>

        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->


<!-- Login Modal -->
<div class="modal fade" id="login_modal" tabindex="-1" role="dialog" aria-labelledby="loginModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
                <h2 class="modal-title" id="loginModalLabel">Login</h2>
            </div>
            <form id="login-form" action="${pageContext.request.contextPath}/checkLogin"
                  method="post">
                <div class="modal-body">
                    <div class="form-group">
                        <input type="text" placeholder="Email" name="username" class="form-control input-lg"
                               tabindex="1">
                    </div>
                    <div class="form-group">
                        <input type="password" placeholder="Password" name="password" class="form-control input-lg"
                               tabindex="2">
                    </div>
                    <div class="radio">
                        <label>
                            <input type="radio" name="radioAuthenticationType" id="userRadioButton" value="user"
                                   checked>
                            User
                        </label>
                    </div>
                    <div class="radio">
                        <label>
                            <input type="radio" name="radioAuthenticationType" id="driverRadioButton" value="driver">
                            Driver
                        </label>
                    </div>
                </div>
                <div class="modal-footer">
                    <div>
                        <button id="login-submit" type="submit" class="btn btn-success">Log in</button>
                        <button type="button" class="btn btn-primary" data-dismiss="modal">Cancel</button>
                    </div>
                </div>
            </form>

        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
<!-- /.modal -->

</body>

</html>