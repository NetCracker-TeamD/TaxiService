$(document).ready(function(){
    $('.btn_edit').click(function(){
        $(this).parent().find(".editModal").modal();
    });
    $(".save_edit").click(function(){
        var price_coef=$(this).parents().eq(2).find("input[name=price_coef]").val();
        var idle_price_coef=$(this).parents().eq(2).find("input[name=idle_price_coef]").val();
        var id=$(this).parents().eq(2).find("input[name=tariff_id]").val();
        var data={"id": id, "idlePriceCoefficient":idle_price_coef, "priceCoefficient":price_coef};
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