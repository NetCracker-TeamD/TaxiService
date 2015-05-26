/**
 * Created by Anton on 14.05.2015.
 */
$(document).ready(function () {
    $("input[type=password]").keyup(function () {
        var newpass = $("#newpass").val();
        var repass = $("#repass").val();
        if (newpass.length != 0 && newpass == repass) {
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
        var newpass = $("#newpass").val();
        var repass = $("#repass").val();
        if (newpass != repass) {
            $(".war_info").find("strong").html("Incorrect new password. Password mismatch.");
            $(".war_info").css("display", "inline");
            $(".in_er").hide();
            return false;
        } else {
            $(".war_info").css("display", "none");
        }

    });
});