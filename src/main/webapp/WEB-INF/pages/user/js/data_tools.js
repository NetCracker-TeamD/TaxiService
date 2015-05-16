
var DataTools = (function(){
	var 
		serverAddress = "http://localhost:8000",
		userLocation = null,
		services = {
			"cache" : null,
		},
		history = {},
		favLocations = [
				{
					"name" : "Institute",
					"address" : "Borshchahivska St, 126 Kyiv",
				},
			],
		loadServices = function(loader, threadName){
			$.ajax(serverAddress+"/services", {
				'success' : function(responseText) {
					services.cache = responseText.services //JSON.parse(responseText)
					console.log(services.cache)
					services.types = []
					services.descriptions = {}
					services.features = {}
					$.each(services.cache, function(key, service){
						if (service.multipleDestinationLocations && service.chain){
							service.multipleDestinationLocations = false
						}
						services.types.push({
							'name' : service.name,
							'id' : service.serviceId
						})
						services.descriptions[service.name] = service
						services.features[service.name] = []
						var featuresList = services.features[service.name]
						//add id = [class|feature]Id (for templates)
						//add name = [class|feature]Name (for templates)
						$.each(service.allowedCarClasses, function(key, value){value.id = value.classId; value.name = value.className;})
						$.each(service.allowedFeatures, function(key, value){value.id = value.featureId; value.name = value.featureName;})

						var carClasses = service.allowedCarClasses,
							driverSex = [],
							payments = [],
							otherOptions = $.grep(service.allowedFeatures, function(value, key){
								//console.log(value)
								var name = value.name.toLowerCase()
								if (name == "male" || name == "female"){
									return false
								}
								return true
							})

						//car class
						featuresList.push({
							"isCategory" : true,
							"categoryName" : "Car type",
							"featureSpecialName" : "car_class",
							"items" : carClasses,
							"deselectable" : true
						})
						//payments
						payments.push({
							"id" : "cash",
							"name" : "Cash",
							"checked" : true
						})
						payments.push({
							"id" : "card",
							"name" : "Card",
						})
						featuresList.push({
							"isCategory" : true,
							"categoryName" : "Payment type",
							"featureSpecialName" : "payment_type",
							"items" : payments
						})

						//driver sex
						driverSex.push({
							"id" : "male",
							"name" : "Male",
						})
						driverSex.push({
							"id" : "female",
							"name" : "Female",
						})
						driverSex.push({
							"id" : "any",
							"name" : "Any",
							"checked" : true
						})

						featuresList.push({
							"isCategory" : true,
							"categoryName" : "Driver`s sex",
							"featureSpecialName" : "driver_sex",
							"items" : driverSex
						})
						//other options
						featuresList.push({
							"isCategory" : true,
							"categoryName" : "Other options",
							"featureSpecialName" : "features",
							"items" : otherOptions,
							"multiSelect" : true
						})						
					})

					//console.log(services.types)
					loader.setStatus(threadName, 'success')
				},
				'error' : function(obj){
					console.log("Can`t retrieve services information")
					console.log(obj)
					loader.setStatus(threadName, 'error')
				}
			})
		},
		loadHistory = function(loader, threadName, page){
			var postfix = ""
			if (page!=undefined && page!=null){
				postfix+="?page="+page
			}
			$.ajax(serverAddress+"/user/loadHistory"+postfix, {
				'success' : function(response) {
					console.log(response)
					history.cache = response.orders,
					history.pageNumber = response.pageDetails.pageNumber,
					history.pagesAmount = response.totalPage
					console.log(history.cache)

					//console.log(services.types)
					loader.setStatus(threadName, 'success')
				},
				'error' : function(obj){
					console.log("Can`t retrieve history information")
					console.log(obj)
					loader.setStatus(threadName, 'error')
				}
			})
		
		},
		getHistory = function(){
			return history
		},
		getUser = function(){
			return {
				"isLogged": true,
				"name":"Vasja",
				"isBlocked": false,
				"email":"Vasja.the.best@gmail.com",

			}
		},
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
				"address" : address,
				"isUserLocation" : true,
			}
		},
		getServiceTypes = function(){
			
			return services.types;
			// return ["Regular drive", 
			// 	"Long term drive", 
			// 	"Guest delivery",
			// 	"Convey corporation employees",
			// 	"Celebration taxi",
			// 	"Sober driver",
			// 	"Meet my guest",
			// 	"Food delivery",
			// 	"Cargo Taxi",
			// ]
		},
		getServiceDescription = function(serviceType){
			return services.descriptions[serviceType]
			// return {
			// 	"name": "Regular drive", //Ім'я сервісу
			// 	"multipleSourceLocations": false, //Чи передбачено введення кількох початкових точок
			// 	"multipleDestinationLocations": false, //Чи передбачено введення кількох точок призначення
			// 	"chain": true, //Чи являє собою сукупність точок призначення ланцюг чи ні, тобто є незалежними
			// 	"desinationRequired": true, //Чи потрібно введення точки призначення чи ні
			// 	"timing": ["specify"], //"now", способи визначення часу на яке виконується замовлення
			// 	"specifyCarsNumbers": true, //чи можна вказати кількість машин на кожен незалежний маршрут
			// }
		}
		getFeatureList = function(serviceType){
			return services.features[serviceType]
			// return [{"categoryName":"Car type",
			// 		"items": [
			// 			"Econom", "SUV", "Busyness", "Truck",
			// 		],
			// 		"inputName" : "car_type"
			// 	}, {"categoryName":"Driver`s sex",
			// 		"items": [
			// 			"Male", "Famale",
			// 		],
			// 		"inputName" : "driver_sex",
			// 	},
			//  	"WiFi",
			//  	"Animal Transport",
			//  	"Smoking"]
		},
		verifyEmail = function(){
			return true;
		}



	return public_interface = {
		"getFavLocations" : getFavLocations,
		"addFavLocation" : addFavLocation,
		"getServiceTypes" : getServiceTypes,
		"getFeatureList" : getFeatureList,
		"setUserLocation" : setUserLocation,
		"getServiceDescription" : getServiceDescription,
		"getFeatureList" : getFeatureList,
		"getUser" : getUser,
		"getHistory" : getHistory,
		"loadServices" : loadServices,
		"loadHistory" : loadHistory
	}
})()