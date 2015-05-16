var MapTools = (function () {
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

    /*
     listeners :
     onGeolocationAllowed
     */
        getMarkersAmount = function () {
            return Object.keys(markers).length;
        },
        removeMarker = function (key) {
            if (markers[key] != null && markers[key] != undefined) {
                markers[key].setMap(null)
            }
            delete markers[key]
        },
        addMarker = function (key, location) {
            var marker = new google.maps.Marker({
                'position': location,
                'map': map
            })
            if (markers[key] != null && markers[key] != undefined) {
                removeMarker(key)
            }
            markers[key] = marker
        },
        updateMarker = function (key, location) {
            addMarker(key, location)
        },
        clearAllMarker = function () {
            for (var key in markers) {
                if (markers.hasOwnProperty(key)) {
                    markers[key].setMap(null)
                    delete markers[key];
                }
            }
        },
        clearRoutes = function () {
            if (directionsDisplay != null && directionsDisplay != undefined) {
                //remove routes from map
                directionsDisplay.setMap(null);
                directionsDisplay = null;
                //create new display
                directionsDisplay = new google.maps.DirectionsRenderer()
                //set map for display
                directionsDisplay.setMap(map);
            }
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
                //if we hame less than 2 point clear routes on map
                clearRoutes()
            }
        },
        markersFitWindow = function () {
            var bounds = new google.maps.LatLngBounds(),
            // markers & place each one on the map
                markersAmount = getMarkersAmount()

            if (markersAmount > 1) {
                for (var i in markers) {
                    bounds.extend(markers[i].position)
                }
                map.fitBounds(bounds)
            } else if (markersAmount == 1) {
                for (var i in markers) {
                    setMapCenter(markers[i].position)
                    break;
                }
                setMapZoom(15)
            }
            calcAndDrawChainRoute()
        },
        setMapCenter = function (newCenter) {
            console.log(newCenter)
            if ((newCenter.k == undefined || newCenter.k == null) &&
                (newCenter.D == undefined || newCenter.D == null)) {
                map.setCenter(new google.maps.LatLng(newCenter.latitude,
                    newCenter.longitude))
            } else {
                map.setCenter(newCenter)
            }
        },
        setMapZoom = function (newZoom) {
            map.setZoom(newZoom)
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
                    putMarkers();
                    //console(userLocation);
                }, function () {
                })
            } else {
                return userLocation
            }
            return userLocation
        },
        initialize = function (holder) {
            getUserLocation();
            console.log(defaults.location)
            var
                mapOptions = {
                    center: new google.maps.LatLng(defaults.location.latitude,
                        defaults.location.longitude),
                    zoom: defaults.zoom,
                    mapTypeId: defaults.mapType
                }
            geocoder = new google.maps.Geocoder()
            map = new google.maps.Map(holder, mapOptions)
            directionsService = new google.maps.DirectionsService()
            directionsDisplay = new google.maps.DirectionsRenderer()

            directionsDisplay.setMap(map);
        },
    //use only for elemets that alreade are on page
        modAutocompleteAddressInput = function (input, callback) {
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
        },
        putMarkers = function () {
            var markers = [
                    ['London Eye, London', 51.503454, -0.119562],
                    ['Palace of Westminster, London', 51.499633, -0.124755]
                ],
                bounds = new google.maps.LatLngBounds()
            // markers & place each one on the map
            for (i = 0; i < markers.length; i++) {
                var position = new google.maps.LatLng(markers[i][1], markers[i][2])
                bounds.extend(position)
                marker = new google.maps.Marker({
                    position: position,
                    map: map,
                    title: markers[i][0]
                })

                // Automatically center the map fitting all markers on the screen
                //map.fitBounds(bounds)
            }
        },
        calcRoute = function (dots) {
            var start = dots[0];
            alert("start = " + start);
            var end = dots[dots.length - 1];
            alert("end = " + end);
            var waypts = [];

            for (var i = 0; i < dots.length; i++) {
                alert(" dots[i]=" + dots[i]);
                waypts.push({
                    location: dots[i],
                    stopover: true
                });
            }
            var request = {
                origin: start,
                destination: end,
                waypoints: waypts,
                optimizeWaypoints: true,
                travelMode: google.maps.TravelMode.DRIVING
            };
            directionsService.route(request, function (response, status) {
                if (status == google.maps.DirectionsStatus.OK) {
                    directionsDisplay.setDirections(response);
                    route = response.routes[0];
                }
            });
        }


    return public_interface = {
        "init": initialize,
        "calcRoute": calcRoute,
        "putMarkers": putMarkers,
        "getUserLocation": getUserLocation,
        "setMapCenter": setMapCenter,
        "setMapZoom": setMapZoom,
        "modAutocompleteAddressInput": modAutocompleteAddressInput,
        "removeMarker": removeMarker,
        "addMarker": addMarker,
        "getMarkersAmount": getMarkersAmount,
        "markersFitWindow": markersFitWindow,
        "clearAllMarker": clearAllMarker,
        "calcAndDrawChainRoute": calcAndDrawChainRoute,
        "clearRoutes": clearRoutes,
        "getMap": function () {
            return map
        },
        "addListener": function (listenerName, callback) {
            if (listeners[listenerName] == undefined) {
                listeners[listenerName] = []
            }
            listeners[listenerName].push(callback);
        }
    }
})()


$(document).ready(function () {

    MapTools.init(document.getElementById("map-canvas"));
    MapTools.modAutocompleteAddressInput(document.getElementById("newAddress"), function () {
    });
    var dots = [];


    $("#curLoc").click(function () {
        MapTools.calcRoute(dots);
        MapTools.getUserLocation();
    });
    $('#inPlace').click(function () {
        //start timer
        isPaused = false;
        isPausedDownTimer = true;

        $('#controlPanel').removeClass('hidden');
        $(this).remove('#inPlace')
    });

    //timer for refuse
    var refuseTime = 10;
    var output = $('#refuseTimer');
    var isPaused = true;

    var time = 0;
    var refuseTimer = window.setInterval(function () {
        if (!isPaused) {
            time++;
            if (time > refuseTime) {
                $("#refusePanel").removeClass("hidden");
            }
        }
    }, 1000);
    var outputDownTimer = $('#downTimer');
    var isPausedDownTimer = true;
    var downTime = 0;
    var downTimer = window.setInterval(function () {
        if (!isPausedDownTimer) {
            downTime++;
            outputDownTimer.text("Seconds: " + downTime);
        }
    }, 1000);

    $("#start").click(function () {
        // stop refuse timer
        time = 0;
        isPaused = true;
        $("#refusePanel").addClass("hidden");
        // stop down timer
        isPausedDownTimer = true;

        var status = 'inProgress';
        changeStatus(status);

        $(this).addClass("disabled");
        $("#completeBtn").removeClass("disabled");
    });

    $("#completeBtn").click(function () {
        //start refuse timer
        isPaused = false;
        // start down timer
        isPausedDownTimer = false;

        var status = 'complete';
        changeStatus(status);

        $(this).addClass("disabled");
        $("#start").removeClass("disabled");
    });
    $('#refuseBtn').click(function () {

        var status = 'refuse';
        changeStatus(status);
    });


    $('#newRouteBtn').click(function () {
        $("#newAddress").addClass("disabled");
        var toAdd = $('input[name=dest]').val();
        var dest = $('#newAddress').val();

        loadAddress(function (dots) {
            if (dots != null) {
                alert(dots[dots.length - 1]);
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
                            '<span id="' + response.id + '" class="label label-info glyphicon glyphicon-ok">' +
                            response.routeStatus + '</span></div>'));
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
            callback(dots);
        },
        error: function (e) {
            alert('Error: ' + e);
            callback(null);
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
            $("#" + response.id).html(response.routeStatus)
            if(response.status == 'stop') {
                alert('Total price : 25 $ ');
            }
        },
        error: function (e) {
            alert('Error: ' + e);
        }
    });

}

