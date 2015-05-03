var DataTools = (function(){
	var 
		userLocation = null,
		init = function(){
			
		},
		favLocations = [
				{
					"name" : "Institute",
					"address" : "Borshchahivska St, 126 Kyiv",
				},
			],
		getFavLocations = function(){
			if (userLocation!=null) {
				return [].concat(userLocation, favLocations)
			}
			return favLocations
		},
		addFavLocation = function(name, address, atBegining){
			var newItem = {
					"name" : name,
					"address" : address,
				}
			if (atBegining) {
				favLocations.unshift(newItem)
			} else {
				favLocations.push(newItem)
			}
		},
		setUserLocation = function(name, address){
			userLocation = {"name" : name,
				"address" : address}
		},
		getServiceTypes = function(){
			return ["Regular drive", 
				"Long term drive", 
				"Guest delivery",
				"Convey corporation employees",
				"Celebration taxi",
				"Sober driver",
				"Meet my guest",
				"Food delivery",
				"Cargo Taxi",
			]
		},
		getServiceDescription = function(serviceType){
			return {
				"name": "Regular drive", //Ім'я сервісу
				"multipleSourceLocations": false, //Чи передбачено введення кількох початкових точок
				"multipleDestinationLocations": false, //Чи передбачено введення кількох точок призначення
				"chain": true, //Чи являє собою сукупність точок призначення ланцюг чи ні, тобто є незалежними
				"desinationRequired": true, //Чи потрібно введення точки призначення чи ні
				"timing": ["specify"], //"now", способи визначення часу на яке виконується замовлення
				"specifyCarsNumbers": true, //чи можна вказати кількість машин на кожен незалежний маршрут
			}
		}
		getFeatureList = function(serviceType){
			//if (serviceType == "Long term driver") bla bla bla...
			return [{"categoryName":"Car type",
					"items": [
						"Econom", "SUV", "Busyness", "Truck",
					],
					"inputName" : "car_type"
				}, {"categoryName":"Driver`s sex",
					"items": [
						"Male", "Famale",
					],
					"inputName" : "driver_sex",
				},
			 	"WiFi",
			 	"Animal Transport",
			 	"Smoking"]
		},
		verifyEmail = function(){
			return true;
		}



	return public_interface = {
		"init" : init,
		"getFavLocations" : getFavLocations,
		"addFavLocation" : addFavLocation,
		"getServiceTypes" : getServiceTypes,
		"getFeatureList" : getFeatureList,
		"setUserLocation" : setUserLocation,
		"getServiceDescription" : getServiceDescription,
		"getFeatureList" : getFeatureList
	}
})()