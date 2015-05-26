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
<%@ include file="../../jsp/admin/header.jsp" %>

<br><br><br>

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

<style>
    #excel {
        display: inline-block;
        float: right;
        margin: 0;
        position: relative;
    }

</style>
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

<hr/>
<div class="container">
    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>

</body>

</html>