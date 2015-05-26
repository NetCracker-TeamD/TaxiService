$(function () {
    var pickerOpts = {
        dateFormat: $.datepicker.ATOM
    };
    $("#start").datepicker(pickerOpts);
    $("#end").datepicker(pickerOpts);
});


$(document).ready(function () {

    $('.btn_edit').click(function () {
        $(this).parent().find(".editModal").modal();

    });

    $(".save_update").click(function () {
        var tariffType = $(this).parents().eq(2).find("input[name=tariff_type]").val();
        var price_coef = $(this).parents().eq(2).find("input[name=price_coefitient]").val();
        var id = $(this).parents().eq(2).find("input[name=tariff_id]").val();
        var from_datetime = $(this).parents().eq(2).find("input[name=tariff_from]").val();
        var to_datetime = $(this).parents().eq(2).find("input[name=tariff_to]").val();
        console.log(price_coef);
        if (price_coef == "") {
            console.log("n")
        } else {
            var data = {
                "id": id,
                "tariffType": tariffType,
                "priceCoefficient": price_coef,
                "from": from_datetime,
                "to": to_datetime
            };
            console.log(data);
            var url = window.location.pathname + "/update";
            $.ajax({
                url: url,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                dataType: 'json',
                success: function (data) {
                    //  location.reload();
                },
                error: function () {
                    $(".alert").removeClass("hide").addClass("alert-danger").html("Error! Incorrect data!");
                }
            });
        }
    });

    $('[data-toggle="createModel"]').click(function () {
        $("#end").datepicker('setDate', 'today');
        $("#start").datepicker('setDate', 'today');
        $("#start_time").val("00:00");
        $("#end_time").val("23:59");
        $("#start_time").hide();
        $("#pick_day_start").hide();
        $("#end_time").hide();
        $("#pick_day_end").hide();
        $("#end").show();
        $("#start").show();
    });

    $("#pick_tariff_type").change(function () {
        var tariffType = $("#pick_tariff_type").val();
        if (tariffType == "DAY_OF_YEAR") {
            $("#start_time").hide();
            $("#pick_day_start").hide();
            $("#end_time").hide();
            $("#pick_day_end").hide();
            $("#end").show();
            $("#start").show();
        } else {
            if (tariffType == "DAY_OF_WEEK") {
                $("#start_time").hide();
                $("#pick_day_start").show();
                $("#end_time").hide();
                $("#pick_day_end").show();
                $("#end").hide();
                $("#start").hide();
            } else {
                $("#start_time").show();
                $("#pick_day_start").hide();
                $("#end_time").show();
                $("#pick_day_end").hide();
                $("#end").hide();
                $("#start").hide();
            }
        }
    });

    $('.btn_remove').click(function () {
        $(this).parent().find(".removeModal").modal();
    });

    $(".remove_rec").click(function () {
        var id = $(this).parents().eq(2).find("input[name=tariff_id]").val();
        var tariffType = $(this).parents().eq(2).find("input[name=tariff_type]").val();
        var price_coef = $(this).parents().eq(2).find("input[name=tariff_price]").val();
        var from_datetime = $(this).parents().eq(2).find("input[name=tariff_from]").val();
        var to_datetime = $(this).parents().eq(2).find("input[name=tariff_to]").val();
        console.log(id);
        var url = window.location.pathname + "/remove";
        var data = {
            "id": id,
            "tariffType": tariffType,
            "priceCoefficient": price_coef,
            "from": from_datetime,
            "to": to_datetime
        };
        $.ajax({
            url: url,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            dataType: 'json',
            success: function () {
                location.reload();
            },
            error: function () {
                $(".alert").removeClass("hide").addClass("alert-danger").html("Error! Incorrect data!");
            }
        });
    });

    $('.btn_create').click(function () {
        $(this).parent().find(".createModel").modal();
    });
    $(".create_rec").click(function () {
        var tariffType = $("#pick_tariff_type").val();
        var price_coef_create = $("#price_coef_create").val();
        var from_datetime;
        var to_datetime;
        if (tariffType == "DAY_OF_YEAR") {
            from_datetime = $("#start").val();
            to_datetime = $("#end").val();
        } else if (tariffType == "DAY_OF_WEEK") {
            from_datetime = $("#pick_day_start").val();
            to_datetime = $("#pick_day_end").val();
        } else {
            from_datetime = $("#end_time").val();
            to_datetime = $("#start_time").val();
        }

        if (price_coef_create == "") {
            $(".inform").removeClass("hide").html("Enter price coefficient!");
            return false;
        } else {
            var data = {
                "tariffType": tariffType,
                "priceCoefficient": price_coef_create,
                "from": from_datetime,
                "to": to_datetime
            };
            console.log(data);
            var url = window.location.pathname + "/create";
            $.ajax({
                url: url,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                dataType: 'json',
                success: function () {
                    location.reload();
                },
                error: function () {
                    $(".alert").removeClass("hide").addClass("alert-danger").html("Error! Incorrect data!");
                }
            });
        }
    });

});