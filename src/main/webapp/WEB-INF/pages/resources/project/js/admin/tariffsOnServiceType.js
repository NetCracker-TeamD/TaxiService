$(document).ready(function(){
    $('.btn_edit').click(function(){
        $(this).parent().find(".editModal").modal();
        $(this).parent().find(".save_edit").click(function(){
            var priceByTime=$(this).parents().eq(2).find("input[name=priceByTime]").val();
            var priceByDistance=$(this).parents().eq(2).find("input[name=priceByDistance]").val();
            var minPrice=$(this).parents().eq(2).find("input[name=minPrice]").val();
            var id=$(this).parents().eq(2).find("input[name=tariff_id]").val();
            var data={"id": id, "priceByTime":priceByTime, "priceByDistance":priceByDistance, "minPrice": minPrice};
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
});