/**
 * Created by Anton on 14.05.2015.
 */
$(document).ready(function () {
    $("input[type=password]").keyup(function() {
        var newpass=$("#newpass").val();
        var repass=$("#repass").val();
        if(newpass.length >= 6){
            $("#6char").removeClass("glyphicon-remove");
            $("#6char").addClass("glyphicon-ok");
            $("#6char").css("color","#00A41E");
        }else{
            $("#6char").removeClass("glyphicon-ok");
            $("#6char").addClass("glyphicon-remove");
            $("#6char").css("color","#FF0004");
        }
        if (newpass.length!=0&&newpass == repass) {
            $("#pwmatch").removeClass("glyphicon-remove");
            $("#pwmatch").addClass("glyphicon-ok");
            $("#pwmatch").css("color", "#00A41E");
        } else {
            $("#pwmatch").removeClass("glyphicon-ok");
            $("#pwmatch").addClass("glyphicon-remove");
            $("#pwmatch").css("color", "#FF0004");
        }
    });
    $('#passwordForm').submit(function () {
        var newpass=$("#newpass").val();
        var repass=$("#repass").val();
        if($("#oldpass").val().length<6){
            $(".war_info").find("strong").html("Incorrect old password. Must be greater than five characters");
            $(".war_info").css("display","inline");
            $(".in_er").hide();
            return false;
        }else{
            $(".war_info").css("display","none");
        }
        if((newpass.length < 6)){
            $(".war_info").find("strong").html("Incorrect new password. Must be greater than five characters");
            $(".war_info").css("display","inline");
            $(".in_er").hide();
            return false;
        }
        if(newpass != repass){
            $(".war_info").find("strong").html("Incorrect new password. Password mismatch.");
            $(".war_info").css("display","inline");
            $(".in_er").hide();
            return false;
        }else{
            $(".war_info").css("display","none");
        }

    });
});