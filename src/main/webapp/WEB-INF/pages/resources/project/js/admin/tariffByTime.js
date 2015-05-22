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

    var selText;
    var nowTemp = new Date();
    var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
    $(".dropdown-menu li a").click(function () {
        selText = $(this).text();
        $(this).parents('.btn-group').find('.dropdown-toggle').html(selText + ' <span class="caret"></span>');
        console.log("Tariff type " + selText);
        switch (selText) {
            case "Daily tariff":
                now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

                break;
            case "Weekly tariff":
                now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0);
                break;
            case "The tariff by time":
                now = new Date(0, 0, 0, nowTemp.getHours(), nowTemp.getMinutes(), 0);
                break;
        }
    });

    /*
     var checkin = $('.from_date').datepicker({
     format: 'dd/mm/yyyy',
     onRender: function (date) {
     return date.valueOf() > now.valueOf() ? 'disabled' : '';
     }
     }).on('changeDate', function (ev) {
     if (ev.date.valueOf() > checkout.date.valueOf()) {
     var newDate = new Date(ev.date)
     newDate.setDate(newDate.getDate() + 1);
     checkout.setValue(newDate);
     }
     checkin.hide();
     $('#to_date')[0].focus();
     }).data('datepicker');
     var checkout = $('.to_date').datepicker({
     format: 'dd/mm/yyyy',
     onRender: function (date) {
     return date.valueOf() > now.valueOf() ? 'disabled' : '';
     }
     }).on('changeDate', function (ev) {
     checkout.hide();
     }).data('datepicker');

     */
    $('.btn_edit').click(function () {
        $(this).parent().find(".editModal").modal();
        $(this).parent().find(".save_edit").click(function () {
            //TODO receive data and send to server
            /*var price_coef=$(this).parents().eq(2).find("input[name=price_coef]").val();
             var idle_price_coef=$(this).parents().eq(2).find("input[name=idle_price_coef]").val();
             var id=$(this).parents().eq(2).find("input[name=tariff_id]").val();
             var data={"id": id, "idlePriceCoefficient":idle_price_coef, "priceCoefficient":price_coef};*/
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
            //TODO receive data and send to server
            /*var price_coef=$(this).parents().eq(2).find("input[name=price_coef]").val();
             var idle_price_coef=$(this).parents().eq(2).find("input[name=idle_price_coef]").val();
             var id=$(this).parents().eq(2).find("input[name=tariff_id]").val();
             var data={"id": id, "idlePriceCoefficient":idle_price_coef, "priceCoefficient":price_coef};*/
            var url = window.location.pathname + "/remove";
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
            //TODO receive data and send to server
            var price_coef_create = $("#price_coef_create").val();
            var tariff_type;
            if (selText=='The tariff by time'){
                tariff_type='TIME_OF_DAY' ;
            }else if(selText=='Weekly tariff'){
                tariff_type='DAY_OF_WEEK';
            }else{
                tariff_type='DAY_OF_YEAR';
            }
            var from_datetime = $('#from_create').val();
            var to_datetime = $('#to_create').val();
            var data = {
                "tariffType": tariff_type,
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
        });
    });


});