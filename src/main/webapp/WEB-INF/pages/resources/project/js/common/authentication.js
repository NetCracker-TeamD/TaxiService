$(document).ready(function () {
    $('#login-form').on('submit', function (event) {
        var form = event.target;
        var email = $("input[name='username']", form).val();
        var password = $("input[name='password']", form).val();

        console.log('Before send');
        $.ajax({
            url: '/ajax',
            method: 'POST',
            data: {
                username: email,
                password: password
            },
            success: function (data) {
                console.log(data);
            }
        });
        event.preventDefault();
    });
})