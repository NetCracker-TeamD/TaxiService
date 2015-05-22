<html lang="en">
<head>
    <title>Statictic</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap-theme.css">
    <link rel="stylesheet" href="/pages/resources/project/css/userStatistic.css">
    <link rel="stylesheet" href="/pages/resources/project/css/welcome.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/jquery/jquery-ui.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>
    <script src="/pages/resources/project/js/user/statistic.js"></script>
    <script src="/pages/resources/jquery/jquery-ui.js"></script>
    <link rel="stylesheet" href="/pages/resources/jquery/css/jquery-ui.css">
</head>
<body>
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
                <li><a href="#">Home</a></li>
                <li><a href="#">Queue</a></li>
                <li><a href="#">History</a></li>
                <li><a href="group" class="active">Statistic</a></li>
            </ul>
            <div class="navbar-form navbar-right">
                <div class="form-group">
                    <button type="button" class="btn btn-primary">Log out</button>
                </div>
            </div>
        </div>
    </div>
</nav>

<div class="jumbotron welcome" style="height:150px;">
    <div class="container" style="height:150px;">
        <h1 style="color:yellow; text-align:right;">Statistic</h1>
    </div>
</div>


<div align="center" class="container">
    <ul id="my-menu">
        <li class="nav nav-pills nav-stacked" class="active"><h2>Pick type of report<span class="caret"></span></h2>
            <ul>
                <li role="presentation"><a class="profit_by_period">Most profitable taxi service</a></li>
                <li role="presentation"><a class="popular_car">Most popular car</a></li>
                <li role="presentation"><a class="option_for_user">Most popular additional car options for each
                    customer user</a></li>
                <li role="presentation"><a class="options">Most popular additional car options overall</a></li>
                <li role="presentation"><a class="new_order">New orders per period</a></li>
                <li role="presentation"><a class="month_profit">Service profitability by month</a></li>
            </ul>
        </li>
    </ul>
</div>


<div id="report_1" class="container">
    <h2>Most profitable service</h2>

    <form class="container" action="statistic/exportMostProfitable">
        <input type="submit" value="Export to Excel" class="btn btn-primary"><br>
        <input type="radio" name="period" id="week" value="WEEK" class="checkboxes" checked>Week</input><br>
        <input type="radio" name="period" id="month" value="MONTH" class="checkboxes">Month</input><br>
        <input type="radio" name="period" id="decade" value="DECADE" class="checkboxes">Decade</input>

    </form>
</div>


<div id="report_2" class="container">
    <h2>Most popular car</h2>

    <form action="statistic/exportMostPopularCar">
        <input type="submit" value="Export to Excel" class="btn btn-primary">
    </form>
</div>
<div id="report_3" class="container">
    <h2>Most popular additional car options for each customer user</h2>

    <form action="statistic/exportAdditionalOptionsForUser">
        <input type="submit" value="Export to Excel" class="btn btn-primary">
    </form>
</div>
<div id="report_4" class="container">
    <h2>Most popular additional car options overall</h2>

    <form action="statistic/exportAdditionalOptions">
        <input type="submit" value="Export to Excel" class="btn btn-primary">
    </form>
</div>
<div id="report_5" class="container">
    <h2>New orders per period</h2>
    <div class="alert alert-danger inform hide"></div>
    <table class="table">
        <thead>
        <td>
            <form action="statistic/exportNewOrders">
                Pick start date<input type="text" id="datepicker1" name="startDate">
        </td>
        <td>
            Pick end date<input type="text" id="datepicker" name="endDate">
        </td>
        <td>
            <input type="submit" value="Export to Excel" class="btn btn-primary" id="btn_export">
            </form>
        </td>

        <td>
            <button id="generate" class="btn btn-primary" type="submit">Generate report</button>
        </td>
        <td>
        </td>
        </thead>
    </table>


</div>
<div id="report_6" class="container">
    <h2>Service profitability by month</h2>

    <form action="statistic/exportServiceProfitability">
        <input type="submit" value="Export to Excel" class="btn btn-primary">
    </form>
</div>

<div class="container" id="main_table">
    <div class="table-responsive">
        <table class="table table-striped table-hover" id="main_table_content">

        </table>
    </div>
</div>


<hr>
<p>&#169 TeamD 201</p>
</body>
</html>