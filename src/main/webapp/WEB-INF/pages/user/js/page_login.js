/**
 * Created by anton on 5/25/15.
 */

$(document).ready(function() {
    var container = $(".container.content"),
        loginForm = container.find("#login-form"),
        loginBtn = container.find('[data-action="login"]'),
        tmpStorage = {}

    loginForm.validator()

    Templates.unlockAllControls(loginBtn);

    Templates.makeNiceSubmitButton({
        form: loginForm,
        button: loginBtn,
        success: function (response) {
            //loadPage("new-order")
            console.log("success")
            console.log(response)
            if (!response.authenticationStatus) {
                BootstrapDialog.show({
                    type: BootstrapDialog.TYPE_DANGER,
                    title: "Invalid login credentioals",
                    message: "Invalid login credentioals"
                })
            } else {
                $.ajax("/isUserLogged", {
                    'success': function (response) {
                        var user = {
                            "isLogged": response.isAuthenticated,
                            "userId" : response.userId,
                            "role": response.role
                        }
                        switch (user.role) {
                            case "ROLE_CUSTOMER" :
                                window.location = "/order"
                                break;
                            case "ROLE_DRIVER" :
                                window.location = "/driver/queue"
                                break;
                            case "ROLE_ADMINISTRATOR" :
                                window.location = "/admin/statistic"
                                break;
                            default:
                                console.log("UNKNOW ROLE NAME")
                                //window.location("/order")
                                break;
                        }
                    },
                    'error': function (response) {
                        console.log("error")
                        console.log(response)
                        BootstrapDialog.show({
                            type: BootstrapDialog.TYPE_DANGER,
                            title: "Server error",
                            message: "Server returns error with status '" + response.statusText + "'"
                        })
                    }
                })
            }
        },
        error: function (response) {
            console.log("error")
            console.log(response)
            BootstrapDialog.show({
                type: BootstrapDialog.TYPE_DANGER,
                title: "Server error",
                message: "Server returns error with status '" + response.statusText + "'"
            })
        }
    })

})