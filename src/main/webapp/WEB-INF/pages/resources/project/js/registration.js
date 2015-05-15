$(function () {
    $('#reg').on('click', function (event) {
        event.preventDefault();
        var data = $('#registrationForm').serialize();
        $.ajax({
            url: '/register',
            method: 'POST',
            data: data,
            success: function (data) {
                if (data.success) {
                    alert('Registration completed successful. Check your email.');
                    $('#registrationForm')[0].reset()
                    $('#t_and_c_m').modal('hide');
                } else {
                    alert('Validation errors');
                    console.log(data);
                }
            }
        })
    });
})