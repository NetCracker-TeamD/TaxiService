/**
 * Created by Vika on 4/29/15.
 */

$(function() {
    $( "#datepicker" ).datepicker();
});
$(function() {
    $( "#datepicker1" ).datepicker();
});
$(document).ready(function () {
        $('ul#my-menu ul').each(function (index) {
            $(this).prev().addClass('collapsible').click(function () {
                if ($(this).next().css('display') == 'none') {
                    $(this).next().slideDown(200, function () {
                        $(this).prev().removeClass('collapsed').addClass('expanded');
                    });
                } else {
                    $(this).next().slideUp(200, function () {
                        $(this).prev().removeClass('expanded').addClass('collapsed');
                        $(this).find('ul').each(function () {
                            $(this).hide().prev().removeClass('expanded').addClass('collapsed');
                        });
                    });
                }
                return false;
            });
        });

    $(".profit_by_period").click(function () {
        $('ul#my-menu li ul').hide();
        $("#pick_date").hide();
        var cont='<div class="container">' +
            ' <h2>Most profitable service</h2> ' ;
        var content=
            '<form class="container">' +
            '<input type="radio" name="period" checked>Week</input><br>' +
            '<input type="radio" name="period">Month</input><br>' +
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
        $("#title").html(cont);
        $("#main_content").html(content);
    });
    $(".popular_car").click(function () {
        $('ul#my-menu li ul').hide();
        $("#pick_date").hide();
        var cont='<div class="container">' +
            ' <h2>Most popular car</h2> ' ;
        var  content=
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
            '</div>';
        $("#title").html(cont);
        $("#main_content").html(content);
    });
    $(".option_for_user").click(function () {
        $('ul#my-menu li ul').hide();
        $("#pick_date").hide();
        var cont='<div class="container">' +
            '<h2>Most popular additional car options for each customer user</h2>';
        var content=
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
            '</div>';
        $("#title").html(cont);
        $("#main_content").html(content);
    });
    $(".options").click(function () {
        $('ul#my-menu li ul').hide();
        $("#pick_date").hide();
        var cont='<div class="container">' +
            '<h2>Most popular additional car options overall</h2>';
        var  content=
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
            '</div>';
        $("#title").html(cont);
        $("#main_content").html(content);
    });
    $(".new_order").click(function () {
        $('ul#my-menu li ul').hide();
        var content='<div class="container"> '+'<h2>New orders per period</h2> '
          ;
        $("#pick_date").show();
        var  content2='<div class="table-responsive"> '+
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
            '</div>';
        $("#title").html(content);
        $("#main_content").html(content2);
    });
    $(".month_profit").click(function () {
        $('ul#my-menu li ul').hide();
        $("#pick_date").hide();
        var cont='<div class="container">' +
            '<h2>Service profitability by month</h2> ';
        var content=
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
            '</div>';
        $("#title").html(cont);
        $("#main_content").html(content);
    });

});
