/**
 * Created by Іван on 04.05.2015.
 */
$(document).ready(function () {
    $('#queue_order1 #queue_order2').click(function () {
        $(this).parent()
            .children('#freeRoute')
            .stop()
            .slideToggle();
    });
});
