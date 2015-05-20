var period;

//время from to
$(function () {
    var pickerOpts = {
        dateFormat: $.datepicker.ATOM
    };
    $("#datepicker").datepicker(pickerOpts);
    $(function () {
        $("#datepicker1").datepicker(pickerOpts);
    });
});


$(document).ready(function () {
    //выпадающий спикок все типов отчетов
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

    //изменение параметра перод по изменинию checkbox
    $('.checkboxes').change(function () {
        findProfitByPeriod();
    });

    //Most profitable service
    $(".profit_by_period").click(function () {
        $('ul#my-menu li ul').hide("normal");
        $("#report_1").show();
        $("#report_5").hide();
        $("#report_2").hide();
        $("#report_3").hide();
        $("#report_4").hide();
        $("#report_6").hide();
        findProfitByPeriod();
    });

    //нахождение параметра периода

    function findProfitByPeriod() {
        if ($("#decade").is(":checked")) {
            period = "DECADE";
        } else if ($("#month").is(":checked")) {
            period = "MONTH";
        } else {
            period = "WEEK";
        }
        var parameterName = {
            "period": period
        };
        var recursiveDecoded = decodeURIComponent($.param(parameterName));
        var mapping = "statistic/" + "mostProfitableService?" + recursiveDecoded;
        $.get(mapping, function (data) {
            if (data.length != 0) {
                drawTable(data);
            }
            else {
                drawAttention();
            }
        });
    }


    function drawTable(data) {
        $('#main_table_content').empty();
        var row_ap = $("<thead> <tr>");
        $("#main_table_content").append(row_ap)
        row_ap.append($("<th>" + "# " + "</th>"));
        for (var prop in data[0]) {
            row_ap.append($("<th>" + prop + "</th>"));
        }
        var row_down = $(" </tr> </thead> <tbody >");
        $("#main_table_content").append(row_down)
        for (var i = 0; i < data.length; i++) {
            var row = $("<tr />");
            $("#main_table_content").append(row);
            row.append($("<td>" + (i + 1) + "</td>"));
            for (var prop in data[i]) {
                row.append($("<td>" + data[i][prop] + "</td>"));
            }
        }
        var row_end = $("</tbody>");
        $("#main_table_content").append(row_end);
    }

    function drawAttention() {
        $('#main_table_content').empty();
        $("#main_table_content").append("<h2 align='center'>No records found</h2>");
    }

    function drawError() {
        $('#main_table_content').empty();
        $("#main_table_content").append("<h2 align='center'>Wrong date</h2>");
    }


    //Most popular car
    $(".popular_car").click(function () {
        $('ul#my-menu li ul').hide("normal");
        $("#report_1").hide();
        $("#report_2").show();
        $("#report_3").hide();
        $("#report_4").hide();
        $("#report_6").hide();
        $("#report_5").hide();
        $.get("statistic/mostPopularCar", function (data) {
            if (data.length != 0) {
                drawTable(data);
            } else {
                drawAttention();
            }
        });
    });


    //Most popular additional car options for each customer user
    $(".option_for_user").click(function () {
        $("#report_1").hide();
        $("#report_2").hide();
        $("#report_3").show();
        $("#report_4").hide();
        $("#report_6").hide();
        $("#report_5").hide();
        $('ul#my-menu li ul').hide("normal");
        $.get("statistic/mostPopularAdditionalCarOptionsForEachCustomerUser", function (data) {
            if (data.length != 0) {
                drawTable(data);
            } else {
                drawAttention();
            }
        });

    });

    ////Most popular additional car options
    $(".options").click(function () {
        $('ul#my-menu li ul').hide("normal");
        $("#report_1").hide();
        $("#report_5").hide();
        $("#report_2").hide();
        $("#report_3").hide();
        $("#report_4").show();
        $("#report_6").hide();
        $.get("statistic/mostPopularAdditionalCarOptionsOverall", function (data) {
            if (data.length != 0) {
                drawTable(data);
            } else {
                drawAttention();
            }
        });
    });

    //New orders per period show table
    $(".new_order").click(function () {
        $("#datepicker1").datepicker('setDate', 'today');
        $("#datepicker").datepicker('setDate', 'today');
        $('#main_table_content').empty();
        $('ul#my-menu li ul').hide("normal");
        $("#report_5").show();
        $("#report_1").hide();
        $("#report_2").hide();
        $("#report_3").hide();
        $("#report_4").hide();
        $("#report_6").hide();
    });
    //ask for json from server with params
    $("#generate").click(function () {
        if ($("#datepicker").datepicker('getDate') != null & $("#datepicker1").datepicker('getDate') != null) {
            var from = $('#datepicker1').datepicker({dateFormat: $.datepicker.ATOM}).val();
            var to = $('#datepicker').datepicker({dateFormat: $.datepicker.ATOM}).val();
            var parameterName = {
                "startDate": from,
                "endDate": to
            };
            var recursiveDecoded = decodeURIComponent($.param(parameterName));
            var mapping = "statistic/" + "newOrdersPerPeriod" + "?" + recursiveDecoded;
            $.ajax({
                url: mapping,
                type: 'GET',
                success: function (data) {
                    if (data.length != 0) {
                        drawTable(data);
                    }
                    else {
                        drawAttention();
                    }
                },
                error: function (data) {
                    drawError();
                }
            });

        } else {
            alert("Please pick period of time");
        }

    });

    //Service profitability by month
    $(".month_profit").click(function () {
        $('ul#my-menu li ul').hide("normal");
        $("#report_1").hide();
        $("#report_5").hide();
        $("#report_2").hide();
        $("#report_3").hide();
        $("#report_4").hide();
        $("#report_6").show();
        $.get("statistic/serviceProfitabilityByMonth", function (data) {
            if (data.length != 0) {
                drawTable(data);
            } else {
                drawAttention();
            }
        });

    });


});
