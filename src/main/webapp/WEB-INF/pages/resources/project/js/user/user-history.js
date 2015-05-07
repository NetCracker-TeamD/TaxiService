var PagingUtils = (function () {
    var dataStoreUrl;
    var paginationId;
    var displayDataCallback;
    var collectDataCallback;
    var initState;

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
        if (state.sort) {
            dataToSend.sort = state.sort;
        }
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

    //вызывается при наличии только странички (при переходе по страничкам)
    function loadNewContent(page, isFirstLoad) {
        var collectedData = collectDataCallback();
        collectedData.pageNum = page;
        reloadContent(collectedData, isFirstLoad);
    }

    //вызывается для загрузки данных соотв. переданному состоянию
    function reloadContent(state, isFirstLoad) {
        var sentData = prepareDataForSending(state);
        console.log('newState');
        console.log(state);
        console.log('data for sending');
        console.log(sentData);

        $.ajax({
            url: dataStoreUrl,
            data: $.param(sentData, true),
            method: 'POST',
            success: function (data) {
                console.log('Received data: ' + data);
                if (data && data.status && data.status === 'OK') {
                    //оновлення UI
                    updatePagination(data.links);
                    displayDataCallback(data.orders, data.pageDetails);
                    //заносимо запит до історії
                }
                if (isFirstLoad === true) {
                    updateHistory(state, sentData);
                }
            },
            error: function (jqXHR, status) {
                alert('Error status: ' + status);
            }
        });
    }

    function updateHistory(state, sentData) {
        window.history.pushState(state, null, location.pathname + '?' + convertIntoGetParams(sentData));
    }

    return public_interface = {
        install: function (initParams) {
            dataStoreUrl = initParams.dataStoreUrl;
            paginationId = initParams.paginationId;
            collectDataCallback = initParams.collectDataCallback;
            displayDataCallback = initParams.displayDataCallback;
            initState = initParams.initState;

            $('#' + paginationId).on('click', 'a', function (event) {
                event.preventDefault();
                var page = $(event.target).data('page');
                console.log('Load page number: ' + page);
                loadNewContent(page, updateHistory);
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
        },
        update: function () { //обновление любых данных, загружаем с первой странички
            loadNewContent(1, false);
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
            data.sort = $('#sort-menu a.selected-property').data('property');
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
});