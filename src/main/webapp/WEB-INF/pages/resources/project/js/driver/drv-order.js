var
    map,
    geocoder,
    directionsDisplay,
    directionsService,
    defaults = {
        "location": {
            "latitude": 50.4020355,
            "longitude": 30.5326905
        },
        "zoom": 9,
        "mapType": google.maps.MapTypeId.ROADMAP
    },
    markers = {},
    userLocation = null,
    listeners = {},

    getMarkersAmount = function () {
        return Object.keys(markers).length;
    },
//draw route on map
    calcAndDrawChainRoute = function () {
        //if we have more then 1 point then we can draw
        if (getMarkersAmount() > 1) {
            //order markers
            console.log("markers")
            //trace to console markers
            console.log(markers)
            //markers are asociative array, to direct access we need to store somewhere key
            //so in "order" i store sorted "names" of each marker
            var order = Object.keys(markers).sort(),
                size = order.length
            //size is amount of points, same as getMarkersAmount()
            //trace to console print order
            console.log(order)
            //save start, end and intermediate points
            //start and end points are mandatory, intermediate points are optional
            var start = markers[order[0]].position, //start (first) point
                end = markers[order[size - 1]].position, //end (last) point
                waypoints = [] //now intermediate points are empty
            for (var i = 1; i < size - 1; i++) {//push all intermidiate points 2..n-1 to waypoints array
                waypoints.push({
                    location: markers[order[i]].position,
                    stopover: true //see google javascript map api for more details about this param
                })
            }
            //ser request parameters
            request = {
                origin: start,//set start point
                destination: end,//set end point
                waypoints: waypoints,//set intermediate points
                travelMode: google.maps.TravelMode.DRIVING //set trevel type
            }
            //create request to server for obtaining routes
            directionsService.route(request, function (result, status) {
                //if status is good, we can draw routes(result) on our map
                if (status == google.maps.DirectionsStatus.OK) {
                    console.log(result)
                    console.log("DriverRequest status '" + status + "'")
                    directionsDisplay.setDirections(result)
                }
            })
        } else {

        }
    },
    onGeolocationAllowed = function () {
        var oga = listeners.onGeolocationAllowed
        if (oga) {
            for (var i = 0; i < oga.length; i++) {
                oga[i](userLocation)
            }
        }
        //setMapCenter(userLocation)
    },
    getUserLocation = function () {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {
                var callOnAllowed = userLocation == null;
                userLocation = {
                    "latitude": position.coords.latitude,
                    "longitude": position.coords.longitude
                }
                marker = new google.maps.Marker({
                    position: userLocation,
                    map: map,
                    title: "Your locato"
                })
                //console(userLocation);
            }, function () {
            })
        } else {
            return userLocation
        }
        return userLocation
    };

function initialize(holder) {
    getUserLocation();
    console.log(defaults.location)
    var
        mapOptions = {
            center: new google.maps.LatLng(defaults.location.latitude,
                defaults.location.longitude),
            zoom: 10,
            mapTypeId: defaults.mapType
        }
    geocoder = new google.maps.Geocoder()
    map = new google.maps.Map(holder, mapOptions)
    directionsService = new google.maps.DirectionsService()
    directionsDisplay = new google.maps.DirectionsRenderer()

    directionsDisplay.setMap(map);
};
//use only for elemets that alreade are on page
function modAutocompleteAddressInput(input, callback) {
    var autocomplete = new google.maps.places.Autocomplete(
        input,
        {types: []}
        //{ types: ['geocode'] }
    )
    google.maps.event.addListener(autocomplete, 'place_changed', function () {
        if (autocomplete.getPlace().geometry != undefined) {
            callback(input, autocomplete.getPlace())
        }
    })
    //fix for correct work with coordinates like 50.447644999, 30.4559055
    var jqinput = $(input)
    jqinput.bind("change", function (e) {
        //costil
        var pat = /[-+]?\d*\.?\d+[,\. ]+[-+]?\d*\.?\d+/
        var address = jqinput.val()
        if (address == address.match(pat)) {
            geocoder.geocode({'address': address}, function (results, status) {
                if (status == google.maps.GeocoderStatus.OK) {
                    console.log("location field change event")
                    var place = results[0]
                    if (place.geometry != undefined) {
                        callback(input, place)
                    }
                } else {
                    console.log('Geocode was not successful for the following reason: ' + status);
                }
            })
        }
    })
};


function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(codeLatLng);
    } else {
        alert("Geolocation is not supported by this browser.");
    }
}

function showPosition(position) {
    alert("Latitude:" + position.coords.latitude +
    " Longitude: " + position.coords.longitude);
}

var infowindowGeo = new google.maps.InfoWindow();
var markerGeo;
function codeLatLng(location) {
    var lat = location.coords.latitude;
    var lng = location.coords.longitude;
    var latlng = new google.maps.LatLng(lat, lng);

    geocoder.geocode({'latLng': latlng}, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            if (results[1]) {
                map.setZoom(12);
                map.setCenter(latlng);
                console.log(latlng);
                alert(latlng);
                markerGeo = new google.maps.Marker({
                    position: latlng,
                    map: map
                });

                infowindowGeo.setContent(results[1].formatted_address);
                $('#currentLocation').val(results[1].formatted_address);
                console.log(results[1].formatted_address);
                infowindowGeo.open(map, markerGeo);
            } else {
                alert('No results found');
            }
        } else {
            alert('Geocoder failed due to: ' + status);
        }
    });
};

function calcRoute(routesArray) {
    wayp = [];
    var start = routesArray[0];
    var end = routesArray[routesArray.length - 1];

    for (var i = 0; i < routesArray.length; i++) {
        alert("Route point : "+routesArray[i]);
        wayp.push({location: routesArray[i], stopover: true});
    }
    var request = {
        origin: start,
        destination: end,
        waypoints: wayp,
        optimizeWaypoints: true,
        travelMode: google.maps.TravelMode.DRIVING
    };
    directionsService.route(request, function (response, status) {
        if (status == google.maps.DirectionsStatus.OK) {
            directionsDisplay.setDirections(response);
            var route = response.routes[0];
        }
    });
}


$(document).ready(function () {
    var execTime = new Date();
    var idleFreeTime = new Date();
    var lastCompletionRoute = new Date();
    var isDriverGoesToClient = true;
    var isPaused = true;
    $.ajax({
        method: "get",
        url: "/driver/loadCurrentState",
        success: function (response) {
            switch (response.currentOrderState){
                case "driverGoesToClient":
                    isDriverGoesToClient = true;
                    isPaused = false;
                    console.log("driverGoesToClient");
                    break;
                case "driverInProgress":
                    isDriverGoesToClient = false;
                    isPaused = true;
                    console.log("driverInProgress");
                    break;
                case "driverWaytForClient":
                    isDriverGoesToClient = false;
                    isPaused = false;
                    lastCompletionRoute = new Date(response.lastCompletionRoute);
                    //alert(lastCompletionRoute.toLocaleTimeString());
                    console.log("driverWaytForClient");
                    break;
            }
            execTime = new Date(response.executeOrderDate);
            idleFreeTime = new Date(response.idleFreeTime);
            $('#executionTime').html(" "+execTime.toLocaleDateString()+", "+ execTime.toLocaleTimeString());
        },
        error: function (e) {
            alert('Error: Load Current State ' + e);
        }
    });

    function CurrentTime() {
        var currentTime = new Date();
        document.getElementById("currentTime").innerHTML = " "+currentTime.toLocaleTimeString();//.toLocaleTimeString();
        if(isDriverGoesToClient){
            console.log("execTime = "+lastCompletionRoute.toLocaleDateString());
            if ((currentTime.getTime() - execTime.getTime()) > idleFreeTime.getTime()) {
                $("#refusePanel").removeClass("hidden");
            }
        }
        if (!isPaused) {
            //alert(idleFreeTime.toLocaleTimeString());
            console.log(currentTime.toLocaleDateString());
            console.log("last completion route  date = "+lastCompletionRoute.toLocaleDateString());
            console.log(idleFreeTime.toLocaleDateString());
            if ((currentTime.getTime() - lastCompletionRoute.getTime()) > idleFreeTime.getTime()) {
                $("#refusePanel").removeClass("hidden");
            }
        }
    }
    var myVar = setInterval(function () {CurrentTime()}, 1000);


    initialize(document.getElementById("map-canvas"));
    modAutocompleteAddressInput(document.getElementById("newAddress"), function () {});

    $("#curLoc").click(function () {
        getLocation();
    });
    $('#paintWay').click(function(){
        routesArray =[];
        loadAddress(function(routesArray){
            calcRoute(routesArray);
            });
    });

    $(".start").click(function () {
        // stop refuse timer
        isDriverGoesToClient = false;
        isPaused = true;
        $("#refusePanel").addClass("hidden");

        var status = 'inProgress';
        changeStatus(status);

        $(".completeBtn").removeClass("disabled");
        $(this).addClass("disabled");
    });

    $(".completeBtn").click(function () {
        //start refuse timer
        lastCompletionRoute = new Date();
        isPaused = false;

        var status = 'complete';
        changeStatus(status);

        $(this).addClass("disabled");
        $(".start").removeClass("disabled");
    });
    $('#refuseBtn').click(function () {

        var status = 'refuse';
        changeStatus(status);
    });


    $('#newRouteBtn').click(function () {
        var dest = $('#newAddress').val();
        $('#newAddress').val("");

        loadAddress(function (dots) {
            if (dots != null) {
                $.ajax({
                    method: "get",
                    data: {
                        source: dots[dots.length - 1],
                        destination: dest
                    },
                    url: "/driver/setNewRoute",
                    success: function (response) {
                        if (response.status = 'ok') {
                            $('#newRoute').before($('<div ><input type="text" style ="margin-top: 5px" ' +
                            'class="form-control"  value="' + response.source + '" name="source" readonly>' +
                            '<input type="text" style ="margin-top: 5px" class="form-control" ' +
                            'value="' + response.destination + '" name="dest" readonly>' +
                            '</div>' +
                            '<div style="padding-top: 5px; padding-bottom: 10px;">' +
                            '<span id="' + response.id + '" class="label label-info glyphicon glyphicon-ok findForRefuse">' +
                            response.routeStatus + '</span></div>'));
                        }else{
                            alert("Set New Route : "+response.status);
                        }
                    },
                    error: function (e) {
                        alert('Error: NewRoute ' + e);
                    }
                });
            } else {
                alert('Error: dots null');
            }
        });
    });

});
function loadAddress(callback) {
    var dots = [];
    $.ajax({
        method: "get",
        dataType: "json",
        data: {},
        url: "/driver/loadAddress",
        success: function (response) {
            $.each(response, function (index, value) {
                dots.push(value);
            });
            alert(dots[dots.length - 1]);
            callback(dots);
        },
        error: function (e) {
            alert('Error: ' + e);
            callback(null);
        }
    });
}

function loadTime(){
    $.ajax({
        method: "get",
        data:{},
        url: "/driver/loadExecuteDate",
        success: function (response) {
            var execTime = new Date(response.executeDate);
            //execTime = response.executeDate;
            alert(parseInt(response.executeDate));
            document.getElementById("currentTime").innerHTML = execTime.toLocaleTimeString();
            //$('#executionTime').val();
        },
        error: function (e) {
            alert('Error: NewRoute ' + e);
        }
    });
}

function changeStatus(status) {
    $.ajax({
        method: "get",
        data: {
            status: status
        },
        url: "/driver/lifeCircleOrder",
        success: function (response) {
            console.log(response.status)
            if (response.status == 'IN PROGRESS') {
                $("#" + response.id).removeClass("findForRefuse");
                $("#" + response.id).removeClass("label-info");
                $("#" + response.id).removeClass("glyphicon-list");
                $("#" + response.id).addClass("label-primary");
                $("#" + response.id).addClass("glyphicon-hourglass");
            } else if (response.status == 'COMPLETED') {
                $("#" + response.id).removeClass("findForRefuse");
                $("#" + response.id).removeClass("label-primary");
                $("#" + response.id).removeClass("glyphicon-hourglass");
                $("#" + response.id).addClass("label-success");
                $("#" + response.id).addClass("glyphicon-ok");
            } else if (response.status == 'REFUSED') {
                $('.findForRefuse').removeClass("glyphicon-list");
                $('.findForRefuse').addClass("label-danger");
                $('.findForRefuse').addClass("glyphicon glyphicon-remove");
                $('.findForRefuse').html(" " + response.status);
            }

            $("#" + response.id).html(" " + response.status);

            if (response.orderStatus != 'continue') {
                $(".completeBtn").addClass('disabled');
                $("#refuseBtn").addClass("hidden");
                $(".start").addClass("disabled");
                $("#newAddress").prop('disabled', true);
                $('#newRouteBtn').addClass("disabled");
                $('#paintWay').addClass("disabled");
                if (response.orderStatus == 'complete') {
                    alert('Total price : 25 $ (заглушка на 382 строчці drv-order.js)');

                } else if (response.orderStatus == 'refused') {
                    alert('Order refused ');
                }
            }
        },
        error: function (e) {
            alert('Error: ' + e);
        }
    });
}




