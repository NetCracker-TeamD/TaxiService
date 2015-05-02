/**
 * Created by Іван on 01.05.2015.
 */
var directionsDisplay;
var directionsService = new google.maps.DirectionsService();
var map;
var x = document.getElementById("demo");
var location = []


function initialize() {
    directionsDisplay = new google.maps.DirectionsRenderer();
    var chicago = new google.maps.LatLng(50.582603899999995, 30.490284199999998);

    var mapOptions = {
        zoom: 6,
        center: chicago
    }

    map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
    directionsDisplay.setMap(map);
}

function calcRoute() {
    var start = document.getElementById('autocomplete').value;
    var end = document.getElementById('destLocation').value;
    var waypts = [];

    var checkboxArray = document.getElementById('waypoints');
    for (var i = 0; i < checkboxArray.length; i++) {
        if (checkboxArray.options[i].selected == true) {
            waypts.push({location: checkboxArray[i].value, stopover: true});
        }
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
            var route = response.routes[0];
        }
    });
}



function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
        alert("bred");
        //alert("Latitude:" + position.coords.latitude +
        //"Longitude: " + position.coords.longitude);
    } else {
        x.innerHTML = "Geolocation is not supported by this browser.";
    }
}

google.maps.event.addDomListener(window, 'load', initialize);