$(document).ready(function () {
    $('#login-form').on('submit', function (event) {
        var form = event.target;
        var data = {
            username: $("input[name='username']", form).val(),
            password: $("input[name='password']", form).val(),
            radioAuthenticationType: $("input:radio[name='radioAuthenticationType']:checked", form).val()
        };

        console.log('Data to send: ' + data.username + ', ' + data.password + ', ' + data.radioAuthenticationType);
        $.ajax({
            url: $(form).attr('action'),
            method: 'POST',
            data: data,
            success: function (responseData) {
                if (responseData.authenticationStatus === true) {
                    location.href = '/index';
                } else {
                    alert('Authentication failed');
                }
            }
        });
        event.preventDefault();
    });
})