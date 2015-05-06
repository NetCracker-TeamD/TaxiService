var PagingUtils = (function () {
    var dataStoreUrl;
    var paginationId;
    var displayDataCallback;
    var collectDataCallback;

    //оновлення посилань
    function updatePagination(links) {
        var pagination = $('#' + paginationId);
        pagination.empty();
        for (var i = 0; i < links.length; i++) {
            var link = links[i];
            console.log(link);
            var li = $('<li/>');
            if (link.active === true) {
                li.addClass('active');
            }
            if (link.disabled === true) {
                li.addClass('disabled');
            }
            var a = $('<a/>', {
                href: link.href,
                text: link.content
            });
            a.data('page', link.pageNum);
            li.append(a);
            pagination.append(li);
        }
    }


    //підготовка даних до відправки у такі формі, якій їх чекає сервер
    function prepareDataForSending(state) {
        var dataToSend = {};
        dataToSend.page = state.pageNum;
        dataToSend.sort = state.sort;
        //prepare filtration data
        return dataToSend;
    }

    function convertIntoGetParams(sentData) {
        var getParams = [];
        $.each(sentData, function (key, value) {
            getParams.push(key + '=' + encodeURIComponent(value));
        })
        return getParams.join('&');
    }

    function loadNewContent(page, callback) {
        var collectedData = collectDataCallback();
        collectedData.pageNum = page;
        reloadContent(collectedData, callback);
    }

    //головна функція
    function reloadContent(state, callback) {
        var sentData = prepareDataForSending(state);
        $.ajax({
            url: dataStoreUrl,
            data: $.param(sentData, true),
            method: 'POST',
            success: function (data) {
                console.log(data);
                if (data && data.status && data.status === 'OK') {
                    //оновлення UI
                    updatePagination(data.links);
                    displayDataCallback(data.orders, data.pageDetails);
                    //заносимо запит до історії
                }
                if (typeof callback === 'function') {
                    callback(state, sentData);
                }
            },
            error: function (jqXHR, status) {
                alert(status);
            }
        });
    }

    return public_interface = {
        install: function (initParams) {
            dataStoreUrl = initParams.dataStoreUrl
            paginationId = initParams.paginationId;
            collectDataCallback = initParams.collectDataCallback;
            displayDataCallback = initParams.displayDataCallback;

            $('#' + paginationId).on('click', 'a', function (event) {
                event.preventDefault();
                var page = $(event.target).data('page');
                console.log(page);
                loadNewContent(page, function (state, sentData) {
                    window.history.pushState(state, null, location.pathname + '?' + convertIntoGetParams(sentData));
                });
            });

            $(window).on('popstate', function (event) {
                var state = event.originalEvent.state || initState;
                reloadContent(state);
            });

            reloadContent(initState);

            /*$('#sort').on('change', function(){
             reloadContent(1);
             })
             */
        }
    }
}());

$(function () {
    PagingUtils.install({
        paginationId: 'pagination',
        dataStoreUrl: '/user/loadHistory',
        collectDataCallback: function collectData() {
            //all necessary data except page number
            var data = {};
            data.sort = ['registrationDate,ASC']; //має збиратись зі сторінки
            //add data filtration
            return data;
        },
        displayDataCallback: function updateMainContent(orders, details) {
            console.log(orders);
            console.log(details);
            $("#items-container").html(
                $('#orderItemTemplate').render(orders)
            );
            $('.history_node').on('click', function (event) {
                $(this).parent()
                    .children('.history_details')
                    .stop()
                    .slideToggle();
            });
        }
    });
});