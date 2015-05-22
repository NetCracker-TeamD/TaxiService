/**
 * Created by Іван on 04.05.2015.
 */
$(document).ready(function () {
    //$('#orders-container .order-details').css('cursor', 'pointer');

    $('#orders-container').on('click', '.order-details', function (event) {
        $(this).parent()
            .children('.free-route')
            .stop()
            .slideToggle();
    });

    PagingUtils.install({
        dataStoreUrl: '/driver/loadQueue',
        paginationId: 'pagination',
        initState: initState,
        collectDataCallback: function () {
            var data = {additional: {}};
            $('#service-types-form input[type="checkbox"]:checked').each(function (index, elem) {
                data.additional[$(elem).attr('name')] = 'on';
            })
            return data;
        },
        displayDataCallback: function (status, data, details) {
            console.log(data);
            console.log(details);
            var container = $("#orders-container");
            if (status === 'ok') {
                container.html(
                    $('#orderItemTemplate').render(data,{ encodeLocation:
                        function(locationAddress) {
                            var res = encodeURIComponent(locationAddress);
                            return res;
                        }
                    })
                );
            } else if (status == 'notFound') {
                container.html('<h2>Items not found</h2>')
            }
        }
    });

    $('#service-types-form').on('submit', function (event) {
        event.preventDefault();
        PagingUtils.update();
    })
});