var MapTools = (function () {
    var
        map,
        geocoder,
        directionsService,
        defaults = {
            "location": {
                "latitude": 50.4020355,
                "longitude": 30.5326905,
            },
            "zoom": 10,
            "mapType": google.maps.MapTypeId.ROADMAP,
        },
        markers = {},// key:{ key:1234, type: <start|intermediate|destination>, gmm:google.Maps.Marker }
        isMarkersDraggable = false,
        userLocation = null,
        listeners = {},
        acceptableAddressLevel = "street_number", //limit when getNameForLocation returns full address or just location(lat, long)
        renders = [],
        inputUpdateRateLimit = 500, //time between updates of the same input for autocomplete in ms
    /*
     listeners :
     onGeolocationAllowed(userLocation)
     onMarkerMoved(markerKey,newLocation)
     onPlacePicked(latitude, longtitude)
     onDistanceChanged(newDistance)
     */
        onGeolocationAllowed = function () {
            var lis = listeners.onGeolocationAllowed
            if ($.isSet(lis)) {
                for (var i = 0; i < lis.length; i++) {
                    lis[i](userLocation)
                }
            }
        },
        onMarkerMoved = function (marker_id) {
            var lis = listeners.onMarkerMoved
            if ($.isSet(lis)) {
                getNameForLocation(markers[marker_id].gmm.position.k, markers[marker_id].gmm.position.D, function (locationName) {
                    for (var i = 0; i < lis.length; i++) {
                        lis[i](marker_id, locationName)
                    }
                })
            }
        },
        onPlacePicked = function (lat, lng) {
            var lis = listeners.onPlacePicked
            if ($.isSet(lis)) {
                for (var i = 0; i < lis.length; i++) {
                    lis[i](lat, lng)
                }
            }
        },
        onDistanceChanged = function (newDistance) {
            var lis = listeners.onDistanceChanged
            if ($.isSet(lis)) {
                for (var i = 0; i < lis.length; i++) {
                    lis[i](newDistance)
                }
            }
        },
        getMarkersAmount = function () {
            return Object.keys(markers).length;
        },
        renderRoute = function (result) {
            var directionsRenderer = new google.maps.DirectionsRenderer
            directionsRenderer.setOptions({preserveViewport: true})
            directionsRenderer.setMap(map)
            directionsRenderer.setDirections(result)
            renders.push(directionsRenderer)
        },
        clearRoutes = function () {
            for (var i = 0; i < renders.length; i++) {
                var directionsDisplay = renders[i]
                if (directionsDisplay != null && directionsDisplay != undefined) {
                    //remove routes from map
                    directionsDisplay.setMap(null)
                    directionsDisplay = null

                }
            }
        },
        removeMarker = function (key) {
            if (markers[key] != null && markers[key] != undefined) {
                markers[key].gmm.setMap(null)
            }
            delete markers[key]
        },
        addMarker = function (key, location, type) {
            var gmm = new google.maps.Marker({
                    position: location,
                    map: map,
                    draggable: isMarkersDraggable
                }),
                marker = {
                    key: key,
                    type: type,
                    gmm: gmm
                }

            google.maps.event.addListener(gmm, 'dragend', function () {
                onMarkerMoved(key);
            });
            if (markers[key] != null && markers[key] != undefined) {
                removeMarker(key)
            }
            markers[key] = marker
        },
        updateMarker = function (key, location, type) {
            addMarker(key, location, type)
        },
        clearAllMarker = function () {
            for (var key in markers) {
                removeMarker(key)
            }
        },
        enableDraggableMarkers = function (enabled) {
            //filter not boolean values
            if (enabled !== true) {
                enabled = false
            }
            isMarkersDraggable = enabled
            for (var i in markers) {
                markers[i].gmm.setOptions({draggable: isMarkersDraggable})
            }
        },
    //draw route on map
        calcAndDrawRoute = function () {
            clearRoutes()
            //build "tree"
            var chains = [],
                startMarkers = [],
                interMarkers = [],
                endMarkers = [],
                summaryDistance = 0

            for (var i in markers) {
                var marker = markers[i]
                switch (marker.type) {
                    case "start":
                        startMarkers.push(marker.key)
                        break;
                    case "intermediate":
                        interMarkers.push(marker.key)
                        break;
                    case "destination":
                        endMarkers.push(marker.key)
                        break;
                }
            }

            var chainsAmount = Math.max(startMarkers.length, endMarkers.length)

            if (getMarkersAmount() > 1) {
                for (var i = 0; i < chainsAmount; i++) {
                    var startI = Math.min(startMarkers.length, i + 1),
                        endI = Math.min(endMarkers.length, i + 1)

                    chains.push([].concat(
                        (startI > 0) ? startMarkers[startI - 1] : [],
                        (interMarkers.length > 0) ? interMarkers : [],
                        (endI > 0) ? endMarkers[endI - 1] : []
                    ))
                }

                for (var i = 0; i < chainsAmount; i++) {
                    var chain = chains[i],
                        size = chain.length

                    if (size > 1) {
                        console.log(chain)
                        var start = markers[chain[0]].gmm.position, //start (first) point
                            end = markers[chain[size - 1]].gmm.position, //end (last) point
                            waypoints = [] //now intermediate points are empty
                        for (var j = 1; j < size - 1; j++) {//push all intermidiate points 2..n-1 to waypoints array
                            waypoints.push({
                                location: markers[chain[j]].gmm.position,
                                stopover: true //see google javascript map api for more details about this param
                            })
                        }
                        //set request parameters
                        request = {
                            origin: start,//set start point
                            destination: end,//set end point
                            waypoints: waypoints,//set intermediate points
                            travelMode: google.maps.TravelMode.DRIVING //set trevel type
                        }
                        //create request to server for obtaining routes
                        directionsService.route(request, function (result, status) {
                            //if status is good, we can draw routes(result) on our map
                            console.log(result)
                            if (status == google.maps.DirectionsStatus.OK) {
                                summaryDistance += result.routes[0].legs[0].distance.value
                                onDistanceChanged(summaryDistance)
                                renderRoute(result)
                            }
                        })
                    }
                }
            }
        },
        markersFitWindow = function () {
            var bounds = new google.maps.LatLngBounds(),
            // markers & place each one on the map
                markersAmount = getMarkersAmount()

            if (markersAmount > 1) {
                for (var i in markers) {
                    bounds.extend(markers[i].gmm.position)
                }
                map.fitBounds(bounds)
            } else if (markersAmount == 1) {
                for (var i in markers) {
                    setMapCenter(markers[i].gmm.position)
                    break;
                }
                setMapZoom(15)
            }
            calcAndDrawRoute()
        },
        setMapCenter = function (newCenter) {
//			console.log(newCenter)
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
        getNameForLocation = function (lat, lgn, callback) {
            console.log("Getting name for location lat : " + lat + " lgn : " + lgn)
            geocoder.geocode({'latLng': new google.maps.LatLng(lat, lgn)}, function (results, status) {
                console.log(results)
                if (status == google.maps.GeocoderStatus.OK) {
                    var place = results[0]
                    if (place.geometry != undefined) {
                        //console.log(place)
                        for (var i = 0; i < place.address_components.length; i++) {
                            var types = place.address_components[i].types
                            for (var j = 0; j < types.length; j++) {
                                if (types[j] == acceptableAddressLevel) {
                                    callback(place.formatted_address)
                                    return;
                                }
                            }
                        }
                        //if address isn`t well detailed
                        callback(results[0].geometry.location.k + ', ' + results[0].geometry.location.D)
                    } else {
                        console.log('No results found');
                    }
                } else {
                    console.log('Geocoder failed due to: ' + status);
                }
            });
        },
        getLocationForName = function (locationName, callback) {
            console.log("Getting location for name '" + locationName + "'")
            geocoder.geocode({'address': locationName}, function (results, status) {
                console.log(results)
                if (status == google.maps.GeocoderStatus.OK) {
                    var result = results[0]
                    if (result.geometry != undefined) {
                        callback(result)
                    } else {
                        console.log('No results found');
                    }
                } else {
                    console.log('Geocoder failed due to: ' + status);
                }
            });
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
            isMarkersDraggable = false
            geocoder = new google.maps.Geocoder()
            map = new google.maps.Map(holder, mapOptions)
            directionsService = new google.maps.DirectionsService()
            google.maps.event.addListener(map, "click", function (event) {
                var latitude = event.latLng.lat()
                var longitude = event.latLng.lng()
                onPlacePicked(latitude, longitude)
            })
        },
    //use only for elemets that alreade are on page
        modAutocompleteAddressInput = function (input, callback) {
            var autocomplete = new google.maps.places.Autocomplete(
                    input,
                    {types: []}
                    //{ types: ['geocode'] }
                ),
                lastUpdateTime = new Date().getTime() - inputUpdateRateLimit,//allow call change event, immediatly after creation
                checkTime = function () {
                    oldTime = lastUpdateTime
                    lastUpdateTime = new Date().getTime()
                    if (lastUpdateTime - oldTime > inputUpdateRateLimit) {
                        return true;
                    }
                    return false;
                }
            google.maps.event.addListener(autocomplete, 'place_changed', function () {
                if (autocomplete.getPlace().geometry != undefined) {
                    checkTime()
                    if ($.isSet(callback)) {
                        callback(input, autocomplete.getPlace())
                    }
                }
            })
            //fix for correct work with coordinates like 50.447644999, 30.4559055
            var jqinput = $(input)
            jqinput.bind("change", function (e) {
                console.log("field changed, doing name resolving")
                var address = jqinput.val()
                getLocationForName(address, function (geometry) {
                    if (checkTime()) {
                        if ($.isSet(callback)) {
                            callback(input, geometry)
                        }
                    } else {
                        console.log("abouting duo time limit")
                    }
                })
            })
        },
        addListener = function (listenerName, callback) {
            if (listeners[listenerName] == undefined) {
                listeners[listenerName] = []
            }
            listeners[listenerName].push(callback);
        },
        removeListenerChain = function (listenerName) {
            listeners[listenerName] = undefined
        }
    /*putMarkers = function(){
     var markers = [
     ['London Eye, London', 51.503454,-0.119562],
     ['Palace of Westminster, London', 51.499633,-0.124755]
     ],
     bounds = new google.maps.LatLngBounds()
     // markers & place each one on the map
     for( i = 0; i < markers.length; i++ ) {
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
     }*/


    return public_interface = {
        "init": initialize,
        //"putMarkers" : putMarkers,
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
        "calcAndDrawRoute": calcAndDrawRoute,
        "clearRoutes": clearRoutes,
        "enableDraggableMarkers": enableDraggableMarkers,
        "getMap": function () {
            return map
        },
        "addListener": addListener,
        "removeListenerChain": removeListenerChain,
        "getNameForLocation": getNameForLocation
    }
})()