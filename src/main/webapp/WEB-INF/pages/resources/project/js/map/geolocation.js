/**
 * Created by Іван on 29.04.2015.
 */

function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    } else {
        alert("Geolocation is not supported by this browser.");
    }
}

function showPosition(position) {
    alert("Latitude:" + position.coords.latitude +
    " Longitude: " + position.coords.longitude);
}