$(function () {
    $('#reg').on('click', function (event) {
        event.preventDefault();
        var data = $('#registrationForm').serialize();
        $.ajax({
            url: '/register',
            method: 'POST',
            data: data,
            success: function (data) {
                console.log(data);
            }
        })
    });
})