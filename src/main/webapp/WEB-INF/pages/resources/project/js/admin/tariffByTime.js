$(function () {
    $('#newStart').datetimepicker({
        locale: 'en'
    });
});
$(function () {
    $('#newEnd').datetimepicker({
        locale: 'en'
    });
});
$(function () {
    $('#updateStart').datetimepicker({
        locale: 'en'
    });
});
$(function () {
    $('#updateEnd').datetimepicker({
        locale: 'en'
    });
});
$(function () {
    $('#deleteStart').datetimepicker({
        locale: 'en'
    });
});
$(function () {
    $('#deleteEnd').datetimepicker({
        locale: 'en'
    });
});


$(document).ready(function () {

    $(function () {
        $('.form-group #updateStart').each(function(){
            $(this).datetimepicker({
                locale: 'en'
            });
        });
    });

    $("#update").click(function(type){
        alert()
        if (type=='DAY_OF_YEAR'){
            $('#updateStart').datetimepicker.setFormat('MMM, dd');
        }else if (type='TIME_OF_DAY'){
            $('#updateStart').datetimepicker.setFormat('hh:mm:ss');
        }else{
            $('#updateStart').datetimepicker.setFormat('EEEE');
        }
    });

    $(function () {
        $('.form-group #updateEnd').each(function(){
            $(this).datetimepicker({
                locale: 'en'
            });
        });
    });
    $('.btn_edit').click(function () {
        $(this).parent().find(".editModal").modal();
        $(this).parent().find(".save_edit").click(function () {
            var tariffType = $("#tariff_type_update").val();
            var price_coef = $("#price_coef_update").val();
            var id = $(this).parents().eq(2).find("input[name=tariff_id]").val();
            var from_datetime = $("#from_date_update").val();
            var to_datetime = $("#to_date_update").val();
            var data = {
                "id": id,
                "tariffType": tariffType,
                "priceCoefficient": price_coef,
                "from": from_datetime,
                "to": to_datetime
            };
            var url = window.location.pathname + "/update";
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
    });
    $('.btn_remove').click(function () {
        $(this).parent().find(".removeModal").modal();
        $(this).parent().find(".remove_rec").click(function () {
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
    });
    $('.btn_create').click(function () {
        $(this).parent().find(".createModel").modal();
        $(this).parent().find(".create_rec").click(function () {
            var tariffType = $("#pick_tariff_type").val();
            var price_coef_create = $("#price_coef_create").val();
            var from_datetime = $('#from_create').val();
            var to_datetime = $('#to_create').val();
            var from_date = new Date(from_datetime);
            var to_date = new Date(to_datetime);
            console.log(from_datetime);
            console.log(from_date);
            var data = {
                "tariffType": tariffType,
                "priceCoefficient": price_coef_create,
                "from": from_datetime,
                "to": to_datetime
            };
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
        });
    });


});