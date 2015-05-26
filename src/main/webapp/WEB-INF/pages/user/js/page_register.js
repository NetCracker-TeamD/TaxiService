/**
 * Created by anton on 5/25/15.
 */
$(document).ready(function() {
    $('[data-type="phone"]').mask("(999) 999-9999");

    var container = $(".container.content"),
        form = container.find("#reg-form"),
        submBtn = container.find('[data-action="reg"]')

    Templates.makeNiceSubmitButton({
        form : form,
        button : submBtn,
        success : function(response){
            console.log(response)
            BootstrapDialog.show({
                type: BootstrapDialog.TYPE_SUCCESS,
                title: "Congradulations!",
                message: "You are succesfully registered. Plese, check your mail for confirmation link"
            })
        },
        error : function(response){
            console.log(response)
            BootstrapDialog.show({
                type: BootstrapDialog.TYPE_DANGER,
                title: "Server error",
                message: "Server returns error with status '"+response.statusText+"'"
            })
        }
    })
})