<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>Statistic</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <!-- Latest compiled and minified CSS -->
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">

    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/jquery/jquery-ui.js"></script>
    <link rel="stylesheet" href="/pages/resources/jquery/css/jquery-ui.css">

    <script src="/pages/resources/project/js/admin/statistic.js"></script>
    <link rel="stylesheet" href="/pages/resources/project/css/statistic.css">
    <link rel="stylesheet" href="/pages/resources/project/css/admin.css">
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
                <li><a href="#">Users</a></li>
                <li><a href="#">Groups</a></li>
                <li><a href="#">Drivers</a></li>
                <li><a href="#">Cars</a></li>
                <li><a href="#">Tariffs</a></li>
                <li><a href="statistic">Reports</a></li>
            </ul>
            <div class="navbar-form navbar-right">
                <button type="button" class="btn btn-primary">Sign out</button>
            </div>
        </div>
    </div>
</nav>

<br><br><br>

<div align="center" class="container">
    <ul id="my-menu">
        <li class="nav nav-pills nav-stacked" class="active"><h2>Pick type of report</h2>
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

    <form class="container">
        <input type="radio" name="period" id="week" class="checkboxes" checked>Week</input><br>
        <input type="radio" name="period" id="month" class="checkboxes">Month</input><br>
        <input type="radio" name="period" id="decade" class="checkboxes">Decade</input>
    </form>
</div>
<div id="report_2" class="container">
    <h2>Most popular car</h2>
</div>
<div id="report_3" class="container">
    <h2>Most popular additional car options for each customer user</h2>
</div>
<div id="report_4" class="container">
    <h2>Most popular additional car options overall</h2>
</div>
<div id="report_5" class="container">
    <h2>New orders per period</h2>
    Pick start date<input type="text" id="datepicker1">
    Pick end date<input type="text" id="datepicker">
    <button id="generate" class="btn btn-default" type="submit">Generate report</button>
</div>
<div id="report_6" class="container">
    <h2>Service profitability by month</h2>
</div>

<div class="container" id="main_table">
    <div class="table-responsive">
        <table class="table table-striped table-hover" id="main_table_content">

        </table>
    </div>
</div>

<hr/>
<div class="container">
    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>

</body>

</html>