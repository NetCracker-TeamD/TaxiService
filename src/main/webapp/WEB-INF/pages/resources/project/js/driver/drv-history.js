var attr = '';
$(document).ready(function () {
    //datepicker
    var nowTemp = new Date();
    var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

    $('.clear_param').on('click', function () {
        window.location = "history";
    });
    var checkin = $('#from_date').datepicker({
        format: 'dd/mm/yyyy',
        onRender: function (date) {
            return date.valueOf() > now.valueOf() ? 'disabled' : '';
        }
    }).on('changeDate', function (ev) {
        if (ev.date.valueOf() > checkout.date.valueOf()) {
            var newDate = new Date(ev.date)
            newDate.setDate(newDate.getDate() + 1);
            checkout.setValue(newDate);
        }
        checkin.hide();
        $('#to_date')[0].focus();
    }).data('datepicker');
    var checkout = $('#to_date').datepicker({
        format: 'dd/mm/yyyy',
        onRender: function (date) {
            return date.valueOf() > now.valueOf() ? 'disabled' : '';
        }
    }).on('changeDate', function (ev) {
        checkout.hide();
    }).data('datepicker');


    //validation get-query
    $('form').submit(function (e) {
        $(this).find('input').filter(function () {
            return !$.trim(this.value).length;  // get all empty fields
        }).prop('disabled', true);
        $(this).find('option').filter(function () {
            return !$.trim(this.value).length;  // get all empty fields
        }).prop('disabled', true);
    });

    //Dropdown History
    $('.history_list .history_node').click(function () {
        $(this).parent()
            .children('.history_details')
            .stop()
            .slideToggle();
    });
    $('#viewType button').click(function () {
        $('#viewType button').removeClass();
        $('#viewType button').addClass("btn btn-info");
        $(this).removeClass();
        $(this).addClass("btn btn-info active");
        if ($(this).val() == "detailed") {
            $('.panel-body .history_details').show();
        }
        if ($(this).val() == "list") {
            $('.panel-body .history_details').hide();
        }
    });
    $('.map-panel .panel-heading').one('click',function () {
        $(this).parent()
            .children('.map')
            .stop()
            .slideToggle();
        initialize($(this).parents().eq(2).find("#map-canvas"));
    });
    //set attribute for href
    setAttr('id');
    setAttr('address');
    setAttr('service_type');
    setAttr('startDate');
    setAttr('endDate');
    $('#type_sort option').each(function () {
        var _href = $(this).attr("value");
        $(this).attr("value", _href + attr);
    });
    $('.pagination li a').each(function () {
        var _href = $(this).attr("href");
        $(this).attr("href", _href + attr);
    });
});

function setAttr(name) {
    var param = getUrlParameter(name);
    if (!(typeof param === 'undefined')) {
        attr += '&' + name + '=' + param;
    }
}

function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            return sParameterName[1];
        }
    }
}

//map

var directionsService = new google.maps.DirectionsService();
var geocoder;
var map;
var directionsDisplay;
var infowindow = new google.maps.InfoWindow();
var marker;
var routesArray = [];
function initialize(mapcanvas) {
    geocoder = new google.maps.Geocoder();
    directionsDisplay = new google.maps.DirectionsRenderer();
    var centerkiev = new google.maps.LatLng(50.582603899999995, 30.490284199999998);
    var mapOptions = {
        zoom: 14,
        center: centerkiev
    }
    map = new google.maps.Map($(mapcanvas).get(0), mapOptions);
    var center = map.getCenter();
    google.maps.event.trigger(map, "resize");
    map.setCenter(center);
    var $row = $(mapcanvas).parents().eq(3);
    directionsDisplay.setMap(map);
    calcRoute($row);
}
function calcRoute(rowClass) {
    wayp = [];
    var start = $(rowClass).find('.source_add').first().html();
    $(rowClass).find('.source_add').each(function () {
        if ($(this).parent().is(':first-child') == false) {
            wayp.push({
                location: $(this).html(),
                stopover: true
            });
        }
    });
    var end = $(rowClass).find('.dest_add').last().html();
    var request = {
        origin: start,
        destination: end,
        waypoints: wayp,
        optimizeWaypoints: true,
        travelMode: google.maps.TravelMode.DRIVING
    };
    directionsService.route(request, function (response, status) {
        console.log(status);
        if (status == google.maps.DirectionsStatus.OK) {
            directionsDisplay.setDirections(response);
        }
    });
}