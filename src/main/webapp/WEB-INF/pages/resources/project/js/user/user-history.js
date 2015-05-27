$(function () {
    var userId = startState.hidden.userId;

    function initCap(str) {
        if (typeof str === "string") {
            return str.substr(0, 1).toUpperCase() + str.substr(1).toLowerCase();
        }
        return "";
    }

    PagingUtils.install({
        paginationId: 'pagination',
        dataStoreUrl: '/user/loadHistory',
        collectDataCallback: function collectData() {
            //all necessary data except page number
            var data = {additional: {}, hidden: {}};
            data.sort = $('#sort-menu a.selected-property').data('property');
            //add data filtration
            var fromDate = $('#date-from').datepicker('getDate');
            if (fromDate != null) {
                data.additional.from = fromDate.getTime();
            }
            var toDate = $('#date-to').datepicker('getDate');
            if (toDate != null) {
                data.additional.to = toDate.getTime();
            }
            data.hidden.userId = userId;
            return data;
        },
        displayDataCallback: function updateMainContent(status, orders, details) {
            console.log(orders);
            console.log(details);
            var container = $("#order-table-body");
            var info = $('#info');
            if (status === 'ok') {
                container.html(
                    $('#orderItemTemplate').render(orders, {
                        initCap: initCap,
                        normalize: function (status) {
                            var parts = status.split('_');
                            var result = '';
                            for (var i = 0; i < parts.length; i++) {
                                result += initCap(parts[i]);
                                if (i < parts.length - 1) {
                                    result += ' ';
                                }
                            }
                            return result;
                        },
                        getButtonClass: function (status) {
                            switch (status) {
                                case 'QUEUED':
                                    return 'btn-primary';
                                case 'ASSIGNED':
                                case 'IN_PROGRESS':
                                case 'COMPLETED':
                                    return 'btn-success';
                                case 'UPDATING':
                                    return 'btn-info';
                                case 'CANCELED':
                                    return 'btn-warning';
                                case 'REFUSED':
                                    return 'btn-danger';
                            }
                            return 'btn-link';
                        }
                    })
                );
                $(".clickable-row").click(function () {
                    window.document.location = $(this).data("href");
                });
                info.html('');
            } else if (status == 'notFound') {
                container.html('');
                info.html('<h2 class="text-center">Items not found</h2>');
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