var MapTools = (function () {
    var
        map,
        geocoder,
        directionsDisplay,
        directionsService,
        defaults = {
            "location": {
                "latitude": 50.4020355,
                "longitude": 30.5326905,
            },
            "zoom": 6,
            "mapType": google.maps.MapTypeId.ROADMAP,
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
                        "longitude": position.coords.longitude,
                    }
                    if (callOnAllowed) {
                        onGeolocationAllowed();
                    }
                }, function () {
                })
            } else {
                return userLocation
            }
            return userLocation
        },
        initialize = function (holder) {
            getUserLocation();
            var
                mapOptions = {
                    center: new google.maps.LatLng(defaults.location.latitude,
                        defaults.location.longitude),
                    zoom: defaults.zoom,
                    mapTypeId: defaults.mapType,
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
                    title: markers[i][0],
                })
                // Automatically center the map fitting all markers on the screen
                //map.fitBounds(bounds)
            }
        }


    return public_interface = {
        "init": initialize,
        "putMarkers": putMarkers,
        "getUserLocation": getUserLocation,
        "setMapCenter": setMapCenter,
        "setMapZoom": setMapZoom,
        "modAutocompleteAddressInput": modAutocompleteAddressInput,
        "removeMarker": removeMarker,
        "addMarker": addMarker,
        "getMarkersAmount": getMarkersAmount,
        "removeMarker": removeMarker,
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