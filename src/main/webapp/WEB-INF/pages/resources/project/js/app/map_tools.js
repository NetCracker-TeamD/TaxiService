var MapTools = (function(){
	var 
		map,
		defaults = {
			"location" :  {
        		"latitude":  50.4020355,
        		"longitude": 30.5326905,
        	},
    		"zoom" : 6,
    		"mapType" : google.maps.MapTypeId.ROADMAP,
    	},
		userLocation = null,
		listeners = {},
		/*
			listeners : 
				onGeolocationAllowed
		*/
		setMapCenter = function(newCenter){
			map.setCenter(new google.maps.LatLng(newCenter.latitude,
						newCenter.longitude))
		},
		setMapZoom = function(newZoom){
			map.setZoom(newZoom)
		},
		onGeolocationAllowed = function(){
			var oga = listeners.onGeolocationAllowed
			if (oga) {
				for (var i=0; i<oga.length; i++){
					oga[i](userLocation)
				}
			}
			//setMapCenter(userLocation)
		},		
		getUserLocation = function(){
				if (navigator.geolocation) {
			    	navigator.geolocation.getCurrentPosition(function(position) {
			    		var callOnAllowed = userLocation == null;
					    userLocation = {
			        		"latitude":  position.coords.latitude,
			        		"longitude": position.coords.longitude,
			        	}
			        	if (callOnAllowed){
		    				onGeolocationAllowed();
			    		}
					}, function() {})
			} else {
				return userLocation
			}
		    return userLocation
		},
		initialize = function() {
			getUserLocation();
			console.log(defaults.location)
			var
				mapOptions = {
					center: new google.maps.LatLng(defaults.location.latitude,
						defaults.location.longitude),
					zoom: defaults.zoom,
					mapTypeId: defaults.mapType,
				}
			map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions)
		},
		putMarkers = function(){
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
		}
		

	return public_interface = {
		"init" : initialize,
		"putMarkers" : putMarkers,
		"getUserLocation" : getUserLocation,
		"setMapCenter" : setMapCenter,
		"setMapZoom" : setMapZoom,
		"getMap" : function(){return map},
		"addListener" : function(listenerName, callback){
			if (listeners[listenerName]==undefined){
				listeners[listenerName] = []
			}
			listeners[listenerName].push(callback);
		}
	}
})()