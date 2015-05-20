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
        //основні дані
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
        //приховані дані, тобто ті, яких не має
        //бути видно у рядку URL
        var hiddenData = {};
        if (state.hidden) {
            for (var prop in state.hidden) {
                hiddenData[prop] = state.hidden[prop];
            }
        }
        //prepare filtration data
        return {main: dataToSend, hidden: hiddenData};
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

    function joinObjects(object1, object2) {
        var result = {};
        for (var i in object1) {
            result[i] = object1[i];
        }
        for (var i in object2) {
            result[i] = object2[i];
        }
        return result;
    }

    //вызывается для загрузки данных соотв. переданному состоянию
    function reloadContent(state, isFirstLoad) {
        var sentData = prepareDataForSending(state);
        console.log('newState');
        console.log(state);
        console.log('data for sending');
        console.log(sentData);
        var data = $.param(joinObjects(sentData.main, sentData.hidden), true);
        console.log('data');
        console.log(data);
        $.ajax({
            url: dataStoreUrl,
            data: data,
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
        var getParams = convertIntoGetParams(sentData.main);
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
