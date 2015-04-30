/**
 * Created by Vika on 4/29/15.
 */
$(document).ready(function () {
    $(".profit_by_period").click(function () {
        var content='<div class="container">' +
            ' <h2>Most profitable service</h2> ' +
            '<form>' +
            '<input type="radio" name="period" checked>Week</input>' +
            '<input type="radio" name="period">Month</input>' +
            '<input type="radio" name="period">Decade</input>' +
            '</form> ' +
            '<div class="table-responsive"> ' +
            '<table class="table table-striped table-hover"> ' +
            '<thead>' +
            '<tr>' +
            '<th>#</th> <th>Service name</th><th>Profit</th> <th>Period of time</th> ' +
            '</tr>' +
            '</thead>' +
            '<tbody>' +
            '<tr>' +
            '<td>1</td><td>ASAP</td><td>1000</td><td>27 of April 2015 - 01 of May 2015</td>' +
            '</tr>' +
            '</tbody>' +
            '</table> ' +
            '</div>' +
            '</div>';
        $("#main_content").html(content);
    });
    $(".popular_car").click(function () {
        var  content='<div class="container">' +
            ' <h2>Most popular car</h2> ' +
            '<div class="table-responsive"> ' +
            '<table class="table table-striped table-hover"> ' +
            '<thead> ' +
            '<tr> <th>#</th> <th>Car model</th> <th>Class</th> <th>Count of usage</th>' +
            '</tr> ' +
            '</thead>' +
            '<tbody>' +
            '<tr><td>1</td><td>DEO Lanos</td><td>Economy</td><td>15</td>' +
            '</tr>' +
            '</tbody>' +
            '</table>' +
            '</div>' +
            '</div>'
        $("#main_content").html(content);
    });
    $(".option_for_user").click(function () {
        var content='<div class="container">' +
            '<h2>Most popular additional car options for each customer user</h2>' +
            '<div class="table-responsive">' +
            '<table class="table table-striped table-hover">' +
            '<thead>' +
            '<tr>' +
            '<th>#</th> <th>Option name</th><th>Customer name</th> <th>Count of usage</th>' +
            '</tr>' +
            '</thead>' +
            '<tbody>' +
            '<tr>' +
            '<td>1</td><td>Conditioner</td><td>Ivanov Ivan</td><td>7</td>' +
            '</tr>' +
            '</tbody>' +
            '</table>' +
            '</div>' +
            '</div>'
        $("#main_content").html(content);
    });
    $(".options").click(function () {
        var  content='<div class="container">' +
            '<h2>Most popular additional car options for each customer user</h2>' +
            '<div class="table-responsive">' +
            '<table class="table table-striped table-hover">' +
            '<thead>' +
            '<tr>' +
            '<th>#</th> <th>Option name</th> <th>Count of usage</th>' +
            '</tr>' +
            '</thead>' +
            '<tbody>' +
            '<tr>' +
            '<td>1</td><td>Wi-fi</td><td>17</td>' +
            '</tr>' +
            '</tbody>' +
            '</table>' +
            '</div>' +
            '</div>'
        $("#main_content").html(content);
    });
    $(".new_order").click(function () {
        var content='<div class="container"> ' +
            '<h2>New orders per period</h2> ' +
            '<form>Pick start date <input type="date" id="theStartDate"/>' +
            '      Pick the end date <input type="date" id="theEndDate"/>' +
            '</form>' +
            '<div class="table-responsive"> ' +
            '<table class="table table-striped table-hover">' +
            '<thead>' +
            '<tr> <th>#</th> <th>Customer name</th><th>Driver name</th> <th>Price</th><th>Service name</th> ' +
            '</tr>' +
            '</thead>' +
            '<tbody><tr><td>1</td><td>Petrov Petr</td><td>Ivanov Ivan</td><td>73</td><td>ASAP</td>' +
            '</tr>' +
            '</tbody>' +
            '</table>' +
            '</div>' +
            '</div>'
        $("#main_content").html(content);
    });
    $(".month_profit").click(function () {
        var content='<div class="container">' +
            '<h2>Service profitability by month</h2> ' +
            '<div class="table-responsive"> ' +
            '<table class="table table-striped table-hover"> ' +
            '<thead> ' +
            '<tr> ' +
            '<th>#</th> <th>Profit</th><th>Date</th> ' +
            '</tr> ' +
            '</thead> ' +
            '<tbody>' +
            '<tr>' +
            '<td>1</td><td>1000</td><td>April, 2015</td>' +
            '</tr>' +
            '</tbody>' +
            '</table>' +
            '</div> ' +
            '</div>'
        $("#main_content").html(content);
    });

});
