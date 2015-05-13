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
        if (!links || links.length === 0) {
            return;
        }
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
        if (state.additional) {
            for (var prop in state.additional) {
                dataToSend[prop] = state.additional[prop];
            }
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
                console.log('Received data:');
                console.log(data);
                if (data && data.status) {
                    //оновлення UI
                    updatePagination(data.links);
                    displayDataCallback(data.status, data.orders, data.pageDetails);
                }
                console.log('isFirtLoad: ' + isFirstLoad);
                //заносимо запит до історії
                if (!isFirstLoad) {
                    console.log('Update history');
                    updateHistory(state, sentData);
                }
            },
            error: function (jqXHR, status) {
                alert('Error status: ' + status);
            }
        });
    }

    function updateHistory(state, sentData) {
        var getParams = convertIntoGetParams(sentData);
        console.log('Resulting get params');
        console.log(getParams);
        window.history.pushState(state, null, location.pathname + '?' + getParams);
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
                loadNewContent(page, false);
            });

            $(window).on('popstate', function (event) {
                var state = event.originalEvent.state || initState;
                reloadContent(state, false);
            });

            reloadContent(initState, true);
        },
        update: function () { //обновление любых данных, загружаем с первой странички
            loadNewContent(1, false);
        }
    }
}());
