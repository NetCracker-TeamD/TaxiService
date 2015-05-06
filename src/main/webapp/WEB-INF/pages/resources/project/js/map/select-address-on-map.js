/**
 * Created by Іван on 29.04.2015.
 */
var directionsDisplay;
var directionsService = new google.maps.DirectionsService();
var geocoder;
var map;
var infowindow = new google.maps.InfoWindow();
var marker;
var routesArray = [];

function initialize() {
    geocoder = new google.maps.Geocoder();
    directionsDisplay = new google.maps.DirectionsRenderer();
    var centerkiev = new google.maps.LatLng(50.582603899999995, 30.490284199999998);
    var mapOptions = {
        zoom: 12,
        center: centerkiev
    }
    map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

    google.maps.event.addListener(map, 'click', function (event) {
        codeLatLng(event.latLng);
    });
    directionsDisplay.setMap(map);
}
//xottabyt

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

function calcRoute() {
    wayp = [];
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