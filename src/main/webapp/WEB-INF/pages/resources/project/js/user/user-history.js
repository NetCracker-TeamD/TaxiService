$(function () {
    PagingUtils.install({
        paginationId: 'pagination',
        dataStoreUrl: '/user/loadHistory',
        collectDataCallback: function collectData() {
            //all necessary data except page number
            var data = {};
            data.sort = $('#sort-menu a.selected-property').data('property');
            //add data filtration
            data.additional = {};
            var fromDate = $('#date-from').datepicker('getDate');
            if (fromDate != null) {
                data.additional.from = fromDate.getTime();
            }
            var toDate = $('#date-to').datepicker('getDate');
            if (toDate != null) {
                data.additional.to = toDate.getTime();
            }
            return data;
        },
        displayDataCallback: function updateMainContent(status, orders, details) {
            console.log(orders);
            console.log(details);
            var container = $("#items-container");
            if (status === 'ok') {
                container.html(
                    $('#orderItemTemplate').render(orders)
                );
                $('.history_node').on('click', function (event) {
                    $(this).parent()
                        .children('.history_details')
                        .stop()
                        .slideToggle();
                });
            } else if (status == 'notFound') {
                container.html('<h2>Items not found</h2>')
            }
        },
        initState: startState
    });

    var okGlyph = $('#ok-glyph');
    $('#sort-menu').on('click', 'a', function (event) {
        event.preventDefault();
        var previous = $('#sort-menu a.selected-property').removeClass('selected-property');
        var target = $(event.target);
        target.append(okGlyph);
        target.addClass('selected-property');
        PagingUtils.update();
    });

    $("#date-from").datepicker();
    if (startState.additional.from) {
        $("#date-from").datepicker('setDate', new Date(startState.additional.from))
    }
    $("#date-to").datepicker();
    if (startState.additional.to) {
        $("#date-to").datepicker('setDate', new Date(startState.additional.to))
    }

    $('#date-apply-button').on('click', PagingUtils.update);
});