<head>
    <title>Groups</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap-theme.css">
    <link rel="stylesheet" href="/pages/resources/project/css/welcome.css">

    <link rel="stylesheet" href="/pages/user/css/ts.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/jquery/jquery-ui.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>
</head>
<body>
<%@ include file="header.jsp" %>
<div class="form-group"><h2 class="form-signin-heading">Statistic</h2></div>
<div class="container">
    <div class="jumbotron">
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="row">
                    <div class="container" id="main_table">
                        <div class="table-responsive">
                            <h2 align="center">${message}</h2>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<hr>
<p>&#169 TeamD 201</p>
</body>
</html>