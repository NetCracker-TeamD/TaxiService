$(document).ready(function(){
    $('.btn_edit').click(function(){
        $(this).parent().find(".editModal").modal();
    });
    $(".save_edit").click(function(){
        var price=$(this).parents().eq(2).find("input[name=price]").val();
        var id=$(this).parents().eq(2).find("input[name=id]").val();
        var data={"id": id, "price":price};
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