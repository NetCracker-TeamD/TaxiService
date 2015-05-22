/*
 This object works with any user data
 Structure of any get,set function is :
 function(loader, threadName, callback, data)
 loader = new Loader() //handles many defered queries
 threadName = "someString"
 callback : function(status, response, [args...]) //status = "success"|"error",
 response = unmodified server response
 [args...] = function specific arguments (ex.: for services args is services and contains formatedServices)
 data = json data of query //optional parameter

 Any load function is private

 Changing of loader state heppens only after calling callback
 */
var DataTools = (function () {
    var
        serverAddress = "",//http://localhost:8000",
        userLocation = null,
        caches = { //stores caches for queries
            services: null,
            history: null,
            order: null,
            user: null,
            favLocations: null,
        },
        formatedData = {//stores formated data of caches
        },
        callStacks = {},
        initCallStack = function (stackName) {
            if (!$.isSet(callStacks[stackName])) {
                callStacks[stackName] = [] //contains callbacks that was called stack[i](status, response, null)
            }
        },
        addCallToStack = function (stackName, loader, threadName, callback) {
            if (!$.isSet(callStacks[stackName])) {
                initCallStack(stackName)
            }
            callStacks[stackName].push({call: callback, thread: threadName, loader: loader})
        },
        isCallStackBusy = function (stackName) {
            var stack = callStacks[stackName]
            return $.isSet(stack) ? stack.length > 0 : false
        },
        realiseCallStack = function (stackName, status, response, data) {
            //console.log("realising " + stackName)
            var stack = callStacks[stackName]
            callStacks[stackName] = []
            if (!$.isSet(stack)) {
                //console.log("attemp to realise alredy empty stack " + stackName)
                return;
            }
            for (var i = 0; i < stack.length; i++) {
                var callback = stack[i]
                if ($.isFunc(callback.call)) callback.call(status, response, data)

            }
            //console.log("realised " + stackName)

            //VERY IMPORTANT, change load state only after calling all callback for data
            for (var i = 0; i < stack.length; i++) {
                var callback = stack[i]
                //console.log(callback)
                //console.log(callback.loader)
                callback.loader.setStatus(callback.thread, status)

            }
        },
        isServerResponseGood = function (response) {//checks server response for errors (true if no errors)
            return true//dummy
        },
        getCurTime = function () {
            new Date().getTime()
        },
        updateTime = function (cacheName) {
            times[cacheName] = getCurTime()
        },
        isCacheFresh = function (cacheName) {//deside do query to server or not (if false app makes new query to server and update data)
            if (!$.isSet(caches[cacheName])) {
                return false //if cache isn`t set make query
            }
            switch (cacheName) {
                case "services":
                    return true;
                    break;
                case "user":
                    return true;
                    break;
            }

            return false//dummy
        },
        getServiceTypes = function (loader, threadName, callback) {
            loadServices(loader, threadName, function (status, serverResponse, data) {
                if ($.isFunc(callback)) callback(status, serverResponse, formatedData.services.types)
            })
        },
        getServiceDescription = function (loader, threadName, callback, serviceType) {
            loadServices(loader, threadName, function (status, serverResponse, data) {
                if ($.isFunc(callback)) callback(status, serverResponse, formatedData.services.descriptions[serviceType])
            })
        }
	    getServiceFeatures = function (loader, threadName, callback, serviceType) {
	        loadServices(loader, threadName, function (status, serverResponse, data) {
	            if ($.isFunc(callback)) callback(status, serverResponse, formatedData.services.features[serviceType])
	        })
	    },
        loadServices = function (loader, threadName, callback) {
            if (isCallStackBusy('services')) {
                addCallToStack('services', loader, threadName, callback)
                return;
            }
            addCallToStack('services', loader, threadName, callback)
            if (isCacheFresh('services')) {
                realiseCallStack('services', 'success', caches.services, null)
                return;
            }

            //query
            $.ajax(serverAddress + "/services", {
                'success': function (response) {
                    caches.services = response
                    var services = response.services
                    services.types = []
                    services.descriptions = {}
                    services.features = {}
                    ////console.log(response)
                    ////console.log(services)
                    $.each(response.services, function (key, service) {
                        ////console.log(service)
                        if (service.multipleDestinationLocations && service.chain) {
                            service.multipleDestinationLocations = false
                        }
                        services.types.push({
                            'name': service.name,
                            'id': service.serviceId
                        })
                        services.descriptions[service.name] = service
                        services.features[service.name] = []
                        var featuresList = services.features[service.name]
                        //add id = [class|feature]Id (for templates)
                        //add name = [class|feature]Name (for templates)
                        $.each(service.allowedCarClasses, function (key, value) {
                            value.id = value.classId;
                            value.name = value.className;
                        })
                        $.each(service.allowedFeatures, function (key, value) {
                            value.id = value.featureId;
                            value.name = value.featureName;
                        })

                        var carClasses = service.allowedCarClasses,
                            driverSex = [],
                            payments = [],
                            otherOptions = $.grep(service.allowedFeatures, function (value, key) {
                                var name = value.name.toLowerCase()
                                return !(name == "male" || name == "female")
                            })
                        if ($.isSet(carClasses[0])) {
                            carClasses[0].checked = true
                        }
                        //car class
                        featuresList.push({
                            "isCategory": true,
                            "categoryName": "Car type",
                            "featureSpecialName": "car_class",
                            "items": carClasses,
                        })
                        //payments
                        payments.push({
                            "id": "cash",
                            "name": "Cash",
                            "checked": true
                        })
                        payments.push({
                            "id": "card",
                            "name": "Card",
                        })
                        featuresList.push({
                            "isCategory": true,
                            "categoryName": "Payment type",
                            "featureSpecialName": "payment_type",
                            "items": payments
                        })
                        //driver sex
                        driverSex.push({
                            "id": "male",
                            "name": "Male",
                        })
                        driverSex.push({
                            "id": "female",
                            "name": "Female",
                        })
                        driverSex.push({
                            "id": "any",
                            "name": "Any",
                            "checked": true
                        })

                        featuresList.push({
                            "isCategory": true,
                            "categoryName": "Driver`s sex",
                            "featureSpecialName": "driver_sex",
                            "items": driverSex
                        })
                        //other options
                        featuresList.push({
                            "isCategory": true,
                            "categoryName": "Other options",
                            "featureSpecialName": "features",
                            "items": otherOptions,
                            "multiSelect": true
                        })
                    })
                    formatedData.services = services
                    realiseCallStack('services', 'success', response, null)
                },
                'error': function (response) {
                    //console.log("Can`t retrieve services information")
                    realiseCallStack('services', 'error', response, null)
                }
            })
        },
        loadHistory = function (page) {
            var postfix = ""
            if (page != undefined && page != null) {
                postfix += "?page=" + page
            }
            $.ajax(serverAddress + "/user/loadHistory" + postfix, {
                'success': function (response) {
                    //console.log(response)
                    history.cache = response.orders,
                        history.pageNumber = response.pageDetails.pageNumber,
                        history.pagesAmount = response.totalPage
                    //console.log(history.cache)

                    ////console.log(services.types)
                    
                    loader.setStatus(threadName, 'success')
                },
                'error': function (obj) {
                    //console.log("Can`t retrieve history information")
                    //console.log(obj)
                    loader.setStatus(threadName, 'error')
                }
            })
        },
        getUserHistory = function(loader, threadName, callback, userId, pageNumber){
        	if (!$.isSet(pageNumber)){
        		pageNumber = 1
        	}
        	//console.log(userId, pageNumber)
			var stackName = 'user_history'
            if (isCallStackBusy(stackName)) {
                addCallToStack(stackName, loader, threadName, callback)
                return;
            }
            addCallToStack(stackName, loader, threadName, callback)
            /*if (isCacheFresh('fav_locations')){
             realiseCallStack('fav_locations', 'success', caches.fav_locations, formatedData.fav_locations)
             return;
             }*/

            $.ajax(serverAddress + "/user/loadHistory?userId="+userId+'&page='+pageNumber, {
                'success': function (response) {
                    //console.log(response)
                    caches.userHistory = response
                    formatedData.userHistory = response
                    realiseCallStack(stackName, 'success', caches.userHistory, formatedData.userHistory)
                    //loader.setStatus(threadName, 'success')
                },
                'error': function (response) {
                    //console.log("Can`t retrieve user`s history information")
                    realiseCallStack(stackName, 'error', response, formatedData.userHistory)
                    //loader.setStatus(threadName, 'error')
                }
            })
        },
        getUser = function (loader, threadName, callback) {
            if (isCallStackBusy('user')) {
                addCallToStack('user', loader, threadName, callback)
                return;
            }
            addCallToStack('user', loader, threadName, callback)
            /*if (isCacheFresh('user')){
             realiseCallStack('user', 'success', caches.user, formatedData.user)
             return;
             }*/

            $.ajax(serverAddress + "/isUserLogged", {
                'success': function (response) {
                    //console.log(response)
                    caches.user = {
                        "isLogged": response.isAuthenticated,
                        "userId" : response.userId,
                        "isBlocked": false,
                        "role": response.role,
                    }
                    formatedData.user = caches.user
                    realiseCallStack('user', 'success', caches.user, formatedData.user)
                    //loader.setStatus(threadName, 'success')
                },
                'error': function (response) {
                    //console.log(response)
                    caches.user = {
                        "isLogged": false,
                        "isBlocked": false,
                    }
                    formatedData.user = caches.user

                    //console.log("Can`t retrieve user information")
                    realiseCallStack('user', 'error', response, formatedData.user)
                    //loader.setStatus(threadName, 'error')
                }
            })
            //realiseCallStack('user', 'success', caches.user, formatedData.user)
        },
        calcOrderStatus = function(order){
			orderInfo.status = "queued"
    		if (orderInfo.updating) {
    			orderInfo.status = "updating"
    		} else if (orderInfo.complete) {
    			orderInfo.status = "completed"
    		} else {
    			var cars = orderInfo.carsAmount
    			var inprog = false,
    				canceled = false,
    				refused = false,
    				completed = false,
    				assigned = false
    			for (var i=0;i<cars.length;i++) {
    				for (var j=0;j<cars[i].length;j++) {
    					var state = cars[i][j].status
    					switch (state){
    						case "COMPLETED":
    							completed = true;
    							break;
    						case "CANCELED":
    							canceled = true;
    							break;
    						case "REFUSED":
    							refused = true;
    							break;
    						case "ASSIGNED":
    							assigned = true;
    							break;
    						case "IN_PROGRESS":
    							inprog = true;
    							break;
    					}
    				}
    			}

    			if ( inprog || ( assigned && (completed || refused || canceled) ) ) {
    				orderInfo.status = "in progress"
    			} else if (assigned) {
					orderInfo.status = "assigned"
    			} else if (completed) {
					orderInfo.status = "completed"
    			} else if (refused) {
					orderInfo.status = "refused"
    			} else if (canceled) {
					orderInfo.status = "canceled"
    			}
    		}
        },
        getOrderInfo = function (loader, threadName, callback, orderId, secretKey) {
            var stackName = 'order_' + orderId
            /*if (isCallStackBusy(stackName)) {
                addCallToStack(stackName, loader, threadName, callback)
                return;
            }*/
            addCallToStack(stackName, loader, threadName, callback)
            /*if (isCacheFresh(stackName)) {
                realiseCallStack(stackName, 'success', caches.user, formatedData.user)
                return;
            }*/
            
            $.ajax(serverAddress+"/getOrder?id="+orderId+(($.isSet(secretKey) ? "&secret="+secretKey : "")), {
            	success : function(response) {
            		//console.log('order information loaded')
            		//console.log(response)
            		orderInfo = response
            		calcOrderStatus(orderInfo)
            		realiseCallStack(stackName, 'success', response, orderInfo)
            		//loader.setStatus(threadName, 'success')
            	},
            	error : function(response){
            		//console.log("Can`t retrieve order information")
            		realiseCallStack(stackName, 'error', caches.user, null)
            		//loader.setStatus(threadName, 'error')
            	}
            })
            /*
            caches[stackName] = {
                "status": "queued",
                "serviceType": "5",
                "time": "specified",
                "time_specified": "05/20/2015 1:53 PM",
                "start_addresses": ["вулиця Академіка Вільямса, 9, Київ, Украина"],
                "destination_addresses": ["улица Авиаконструктора Антонова, 4/1, Київ, Украина"],
                "cars_amount": "1",
                "payment_type": "cash",
                "driver_sex": "any",
                "features": "5",
                "orderId": 123123,
                "secret": "asdasd"
            }
            formatedData[stackName] = caches[stackName]
            realiseCallStack(stackName, 'success', caches[stackName], formatedData[stackName])
            */
        },
        tryLogin = function (data, callback) {
            $.ajax({
                type: "post",
                url: "/checkLogin",
                data: data,
                cache: false,
                processData: false,
                success: function (response) {
                    if (response.authenticationStatus) {
                        //console.log("USER LOGGED-----------")
                        caches.user = {
                            "isLogged": true,
                            "isBlocked": false,
                        }
                        formatedData.user = caches.user
                        //console.log(caches.user)
                    }
                    if ($.isSet(callback)) {
                        callback("success", response)
                    }
                },
                error: function (response) {
                    if ($.isSet(callback)) {
                        callback("error", response)
                    }
                }
            })
        },
        getAllowedSortProperties = function(loader, threadName, callback){
        	var stackName = 'allowedSortProperties'
        	if (isCallStackBusy(stackName)) {
                addCallToStack(stackName, loader, threadName, callback)
                return;
            }
            addCallToStack(stackName, loader, threadName, callback)

        	caches[stackName] = [
        		{ value : "registrationDate", name : "Registration Date", isSelected: true },
        		{ value : "serviceType.name", name : "Service" },
        		{ value : "paymentType", name : "Payment Type" },
        		{ value : "driverSex", name : "Driver Sex" }
    		]

    		formatedData[stackName] = caches[stackName]

    		realiseCallStack(stackName, 'success', caches[stackName], formatedData[stackName])
    		//loader.setStatus(threadName, 'success')
        },
        getFavLocations = function (loader, threadName, callback) {
        	var stackName = 'fav_locations'
            if (isCallStackBusy(stackName)) {
                addCallToStack(stackName, loader, threadName, callback)
                return;
            }
            addCallToStack(stackName, loader, threadName, callback)
            /*if (isCacheFresh('fav_locations')){
             realiseCallStack('fav_locations', 'success', caches.fav_locations, formatedData.fav_locations)
             return;
             }*/

            $.ajax(serverAddress + "/user/addresses", {
                'success': function (response) {
                    //console.log(response)
                    caches.fav_locations = response
                    formatedData.fav_locations = response
                    if (userLocation!=null) {
						formatedData.fav_locations = [].concat(userLocation, caches.fav_locations)
					} else {
						formatedData.fav_locations = caches.fav_locations
					}
                    realiseCallStack(stackName, 'success', caches.fav_locations, formatedData.fav_locations)
                    //loader.setStatus(threadName, 'success')
                },
                'error': function (response) {
                    //console.log("Can`t retrieve services information")
                    realiseCallStack(stackName, 'error', caches.fav_locations, formatedData.fav_locations)
                    //loader.setStatus(threadName, 'error')
                }
            })
            /*caches.fav_locations = [
             {
             "name" : "Institute",
             "address" : "Borshchahivska St, 126 Kyiv",
             },
             ]
             if (userLocation!=null) {
             formatedData.fav_locations = [].concat(userLocation, caches.fav_locations)
             } else {
             formatedData.fav_locations = caches.fav_locations
             }
             realiseCallStack('fav_locations', 'success', caches.user, formatedData.fav_locations)
             */
        },
        /*
         locations_list = [
         {
         name : "Location name",
         address : "Some address, street, city ...",
         id: N,
         action: "none" | "add" | "update" | "remove"
         }
         ]
         */
        updateFavLocations = function (loader, threadName, callback, locations_list) {
            var stackName = 'fav_locations_save'
            if (isCallStackBusy(stackName)) {
                addCallToStack(stackName, loader, threadName, callback)
                return;
            }
            addCallToStack(stackName, loader, threadName, callback)
            /*if (isCacheFresh('fav_locations')){
             realiseCallStack('fav_locations', 'success', caches.fav_locations, formatedData.fav_locations)
             return;
             }*/

            $.ajax(serverAddress + "/user/saveAddresses", {
                'success': function (response) {
                    //console.log(response)
                    caches.fav_locations = response
                    formatedData.fav_locations = response
                    realiseCallStack('fav_locations', 'success', caches.fav_locations, formatedData.fav_locations)
                    //loader.setStatus(threadName, 'success')
                },
                'error': function (response) {
                    //console.log("Can`t retrieve services information")
                    realiseCallStack('fav_locations', 'error', caches.fav_locations, formatedData.fav_locations)
                    //loader.setStatus(threadName, 'error')
                }
            })
            /*caches.fav_locations = [
             {
             "name" : "Institute",
             "address" : "Borshchahivska St, 126 Kyiv",
             },
             ]
             if (userLocation!=null) {
             formatedData.fav_locations = [].concat(userLocation, caches.fav_locations)
             } else {
             formatedData.fav_locations = caches.fav_locations
             }
             realiseCallStack('fav_locations', 'success', caches.user, formatedData.fav_locations)
             */
        },
        setUserLocation = function (name, address) {
            userLocation = {
                "name": name,
                "address": address,
                "isUserLocation": true,
            }
            formatedData.fav_locations = [].concat(userLocation, caches.fav_locations)
        },
        verifyEmail = function () {
            return true;
        }


    return public_interface = {
        "getFavLocations": getFavLocations,
        "getServiceTypes": getServiceTypes,
        "getServiceDescription": getServiceDescription,
        "getServiceFeatures": getServiceFeatures,
        "getOrderInfo": getOrderInfo,
        "setUserLocation": setUserLocation,
        "getUser": getUser,
        "getUserHistory": getUserHistory,
        "loadServices": loadServices,
        "loadHistory": loadHistory,
        "getAllowedSortProperties" : getAllowedSortProperties,
        "calcOrderStatus" : calcOrderStatus
        //"tryLogin" : tryLogin
    }
})()