//оновлення посилань
function updatePagination(links) {
    var pagination = $('#pagination');
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

function updateMainContent(orders, details) {
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

//підготовка даних до відправки у такі формі, якій їх чекає сервер
function prepareDataForSending(state) {
    var dataToSend = {};
    dataToSend.page = state.paging.pageNum;
    dataToSend.size = state.paging.pageSize;
    if (state.sort.length !== 0) {
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

function loadNewContent(page, callback) {
    var collectedData = collectData();
    collectedData.paging.pageNum = page;
    reloadContent(collectedData, callback);
}

//головна функція
function reloadContent(state, callback) {
    //0. Зчитать із відп. полів дані про тип сортування, фільтри. Із ссилки зчитать сторінку
    //1. Получить нові дані.
    //2. прибрать старі і помістить нові на екран. Повісить на ссилки обробники подій (або на батьківський ul...)
    //3. Додать в історію запис
    /***********1************/
    var sentData = prepareDataForSending(state);
    /***********2************/
    $.ajax({
        url: '/user/loadHistory',
        data: sentData,
        method: 'POST',
        success: function (data) {
            console.log(data);
            if (data.status === 'OK') {
                //оновлення UI
                updatePagination(data.links);
                updateMainContent(data.orders, data.pageDetails);
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

//Збір даних із всіх потрібних полів
function collectData() {
    //all necessary data except page number
    var data = {paging: {}};
    data.paging.pageSize = 10;
    data.sort = ['id,ASC']; //має збиратись зі сторінки
    //add data filtration
    return data;
}

$(document).ready(function () {
    $('#pagination').on('click', 'a', function (event) {
        event.preventDefault();
        var page = $(event.target).data('page');
        console.log(page);
        loadNewContent(page, function (state, sentData) {
            console.log(state);
            console.log(sentData);
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
});