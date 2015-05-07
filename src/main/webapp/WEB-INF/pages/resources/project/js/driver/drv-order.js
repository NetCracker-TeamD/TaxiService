/**
 * Created by Іван on 07.05.2015.
 */
var directionsDisplay;
var directionsService = new google.maps.DirectionsService();
var geocoder;
var map;
var infowindow = new google.maps.InfoWindow();
var marker;
var routesArray = [];
var geolocLatLng;
var currentLocation;

function initialize(canvas) {
    geocoder = new google.maps.Geocoder();
    directionsDisplay = new google.maps.DirectionsRenderer();
    infowindow = new google.maps.InfoWindow();
    var centerkiev = new google.maps.LatLng(50.582603899999995, 30.490284199999998);
    var mapOptions = {
        zoom: 12,
        center: centerkiev
    }

    // When the user selects an address from the dropdown,
    // populate the address fields in the form.
    destinationLoc = new google.maps.places.Autocomplete(
        /** @type {HTMLInputElement} */
        (document.getElementById('destinationLoc')),
        { types: ['geocode'] });
    google.maps.event.addListener(destinationLoc, 'place_changed', function() {fillInAddress(); });

//            'map-canvas'
    map = new google.maps.Map(document.getElementById(canvas), mapOptions);

    google.maps.event.addListener(map, 'click', function (event) {
        codeLatLng(event.latLng);
    });
    directionsDisplay.setMap(map);
}


function codeLatLng(location) {
    var lat = location.lat();
    var lng = location.lng();
    var latlng = new google.maps.LatLng(lat, lng);

    geocoder.geocode({'latLng': latlng}, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            if (results[1]) {
                map.setZoom(12);
                map.setCenter(location);
                marker = new google.maps.Marker({
                    position: latlng,
                    map: map
                });
                routesArray.push(latlng);

                infowindow.setContent(results[1].formatted_address);
                infowindow.open(map, marker);
            } else {
                alert('No results found');
            }
        } else {
            alert('Geocoder failed due to: ' + status);
        }
    });
}

function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    } else {
        alert("Geolocation is not supported by this browser.");
    }
}

function showPosition(position) {
    geolocLatLng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
    geocoder.geocode({'latLng': geolocLatLng}, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            if (results[1]) {
                map.setZoom(12);
                marker = new google.maps.Marker({
                    position: geolocLatLng,
                    map: map
                });
                currentLocation =results[1].formatted_address;
                infowindow.setContent(results[1].formatted_address);
                infowindow.open(map, marker);
                return currentLocation;
            } else {
                alert('No results found');
                currentLocation = "No results found";
            }
        } else {
            alert('Geocoder failed due to: ' + status);
            currentLocation = "Geocoder failed due to:";
        }
    });

}

function calcRoute() {
    wayp = [];
    alert(routesArray[0]);
    var start = routesArray[0];
    var end = routesArray[routesArray.length - 1];
    for (var i = 0; i < routesArray.length; i++) {
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
google.maps.event.addDomListener(window, 'load', initialize);

//timer
var countdownTimer;
var seconds = 60;
function secondPassed() {
    var minutes = Math.round((seconds - 30)/60);
    var remainingSeconds = seconds % 60;
    if (remainingSeconds < 10) {
        remainingSeconds = "0" + remainingSeconds;
    }
    document.getElementById('countdown').innerHTML = minutes + ":" + remainingSeconds;
    if (seconds == 0) {
        clearInterval(countdownTimer);
        document.getElementById('countdown').innerHTML = "You can refuse order!";
    } else {
        seconds--;
    }
}

$(document).ready(function () {

    $("#curLoc").click(function(){
        getLocation();
    });

    $("#newRoute").click(function(){
        $("#newRoute").addClass("disabled");
        var toAdd =  $('input[name=destination]').val();
        alert(toAdd);
        $("#sourceLoc").val(toAdd);
        $('#acceptNewRoute').removeClass("disabled");
        $('#destinationLoc').attr('readonly', false);
    });
    $("#acceptNewRoute").click(function() {
        $("#acceptNewRoute").addClass("disabled");


        $("#newRoute").removeClass("disabled");
        $('#destinationLoc').attr('readonly', true);
    });
    $("#inPlace").click(function(){
        countdownTimer = setInterval('secondPassed()', 1000);
    });
    $("#start").click(function(){
        $("#timer").remove();
    });
});



