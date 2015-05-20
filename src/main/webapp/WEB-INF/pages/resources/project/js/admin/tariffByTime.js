$(document).ready(function(){
    $('.btn_edit').click(function(){
        $(this).parent().find(".editModal").modal();
        $(this).parent().find(".save_edit").click(function(){
            //TODO receive data and send to server
            /*var price_coef=$(this).parents().eq(2).find("input[name=price_coef]").val();
            var idle_price_coef=$(this).parents().eq(2).find("input[name=idle_price_coef]").val();
            var id=$(this).parents().eq(2).find("input[name=tariff_id]").val();
            var data={"id": id, "idlePriceCoefficient":idle_price_coef, "priceCoefficient":price_coef};*/
            var url=window.location.pathname+"/update";
            $.ajax({
                url: url,
                type: 'POST',
                contentType:'application/json',
                data: JSON.stringify(data),
                dataType:'json',
                success: function(){
                    location.reload();
                },
                error: function() {
                    $(".alert").removeClass("hide").addClass("alert-danger").html("Error! Incorrect data!");
                }
            });
        });
    });
    $('.btn_remove').click(function(){
        $(this).parent().find(".removeModal").modal();
        $(this).parent().find(".remove_rec").click(function(){
            //TODO receive data and send to server
            /*var price_coef=$(this).parents().eq(2).find("input[name=price_coef]").val();
             var idle_price_coef=$(this).parents().eq(2).find("input[name=idle_price_coef]").val();
             var id=$(this).parents().eq(2).find("input[name=tariff_id]").val();
             var data={"id": id, "idlePriceCoefficient":idle_price_coef, "priceCoefficient":price_coef};*/
            var url=window.location.pathname+"/remove";
            $.ajax({
                url: url,
                type: 'POST',
                contentType:'application/json',
                data: JSON.stringify(data),
                dataType:'json',
                success: function(){
                    location.reload();
                },
                error: function() {
                    $(".alert").removeClass("hide").addClass("alert-danger").html("Error! Incorrect data!");
                }
            });
        });
    });
    $('.btn_create').click(function(){
        $(this).parent().find(".createModel").modal();
        $(this).parent().find(".create_rec").click(function(){
            //TODO receive data and send to server
            var url=window.location.pathname+"/create";
            $.ajax({
                url: url,
                type: 'POST',
                contentType:'application/json',
                data: JSON.stringify(data),
                dataType:'json',
                success: function(){
                    location.reload();
                },
                error: function() {
                    $(".alert").removeClass("hide").addClass("alert-danger").html("Error! Incorrect data!");
                }
            });
        });
    });
    $('.pagination li a').each(function () {
        var _href = $(this).attr("href");
        $(this).attr("href", _href + attr);
    });
});