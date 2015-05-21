var App = (function(){
	var
	//TODO: add validators
	//TODO: add more pages
		blocks = {
			"body" : null,
			"header" : null,
			"header_items" : null,
			"header_actinos" : null,
			"content" : null,
		},
		current_page_name = "",
		controls = {},
		tmpStorage = {
			user : null,
		},
		showLoadPage = function(){
			blocks.content.html("")
			blocks.content.append(Templates.getLoader("Page is loading, please wait"))
		},
		showBlockedPage = function(){
			blocks.content.html("")
			blocks.content.append(Templates.getBlocked())
		},
		showLogoutPage = function(){window.location = "/logout";},
		showOrderPage = function(orderId, secretKey){
			console.log(orderId, secretKey)
			showLoadPage()
			current_page_name = "make-order"
			var loadOrder = $.isSet(orderId)
			if (loadOrder) {
				current_page_name = "view-order"
			}
			updateHeader()
			console.log('loadOrder '+loadOrder)
			var createDOM = function(){	
				//create page in memory		
				var orderInfo = null
				var isOrderEditable = true
				if ($.isSet(tmpStorage.currentOrder) && loadOrder) {
					orderInfo = tmpStorage.currentOrder
					orderInfo.orderId = orderId
				}
				var 
					user = tmpStorage.user,
					container = Templates.getOrderPage(orderInfo),
					serviceTypes = container.find("#serviceType"),
					orderDetails = container.find('[data-type="order-details"]'),
					map = container.find('[data-type="map"]'),
					orderForm = container.find('#orderForm'),
					enableEditing = (loadOrder) ? (user.isLogged && !user.isBlocked) : false
				//bind order status specific actions
				if (loadOrder) {
					switch (orderInfo.status) {
						case "queued":
							var editBtn = container.find('[data-action="edit"]'),
								cancelBtn = container.find('[data-action="cancel"]')
							Templates.makeNiceSubmitButton({
								form: orderForm,
								button: editBtn,
								method : "get",
								url : "/setUpdating?id="+orderInfo.orderId,
								dataFormater : function(){
									return ""
								},
								success: function(response){
									console.log(response)
									if (response.status=="OK"){
										tmpStorage.currentOrder.updating = true
										DataTools.calcOrderStatus(tmpStorage.currentOrder)
										createDOM()
									} else {
										BootstrapDialog.show({
											type: BootstrapDialog.TYPE_DANGER, closable: true,
											title: "Server returns bad status",
											message: "Server returns bad status '"+response.status+"'",
										})
										//craeteDOM()
									}
								}, 
								error : function(response){
									console.log(response)
									BootstrapDialog.show({
										type: BootstrapDialog.TYPE_DANGER, closable: true,
										title: "Server error",
										message: "Server returns error with status '"+response.statusText+"'",
									})
									//craeteDOM()
								}
							})
							break
					}

				} else {
					var makeOrderBtn = container.find('[data-action="make-order"]')
		   			Templates.makeNiceSubmitButton({
		   				form : orderForm,
		   				button : makeOrderBtn,
		   				useJSON : true,
		   				success : function(response){
		   					console.log("response")
		   					console.log(response)
		   					var trackLink = response.trackLink,
		   					//: "/viewOrder?trackNum=10651&secretKey=null"
		   						trackNumber = $.getURLParam(trackLink, "trackNum"),
		   						secretKey = $.getURLParam(trackLink, "secretKey")
		   					if (secretKey == "null") {
		   						secretKey = null
		   					}
		   					console.log(trackNumber, secretKey)
							var watchIt = function(){
								console.log("go to track link")
								showOrderPage(trackNumber, secretKey)
							}
							BootstrapDialog.show({
								type: BootstrapDialog.TYPE_SUCCESS, closable: false,
								title: "Order successfully created",
								message: function(dialog){
									return $("<div>Your order successfully created<br>You can track it via this link </div>")
										.append( $("<a href='/somelink''>some link with tracknumber</a>")
											.bind("click", function(e){
												e.preventDefault()
												dialog.close()
												watchIt()
											}))
								},
								buttons: [{
									label : "Watch it",
									action: function(dialog){
										dialog.close()
										watchIt()
					                }
								}],
							})
						},
						error : function(response){
		   					console.log("response err")

							console.log(response)
							BootstrapDialog.show({
								type: BootstrapDialog.TYPE_DANGER,
								title: "Server error",
								message: "Server returns error with status '"+response.statusText+"'",
							})
						}
		   			})
				}

				//fill service types
				$.each(tmpStorage.serviceTypes, function (i, item) {
				    serviceTypes.append($('<option>', { value: item.id, text : item.name }))
				})
				if (!loadOrder){
					serviceTypes.bind("change", function(e){
						var newServiceType = $(e.target).find('option[value="'+$(e.target).val()+'"]').text()
						container.find('[data-type="price-holder"]').hide()
						createInputsForServiceType(orderDetails, newServiceType)
					})
				} else {
					serviceTypes.val(orderInfo.serviceType)
					Templates.lockAllControls(serviceTypes)
					var newServiceType = serviceTypes.find('option[value="'+orderInfo.serviceType+'"]').text()
					createInputsForServiceType(orderDetails, newServiceType, orderInfo)
					if (orderInfo.status!="updating"){
						Templates.lockAllControls(orderDetails)
					}
				}

				//fill page
				blocks.content.html("")
				blocks.content.append(container)

				//[0] coz new google.maps.Map accepts clear html element, not wraped by jquery
				MapTools.init(map[0])
				if (!loadOrder || (loadOrder && orderInfo.status=="updating")) {
					MapTools.enableDraggableMarkers(true)
				} else {
					MapTools.enableDraggableMarkers(false)
				}
				MapTools.removeListenerChain("onMarkerMoved")
				MapTools.removeListenerChain("onPlacePicked")
				MapTools.removeListenerChain("onDistanceChange")
				MapTools.addListener("onMarkerMoved", function(marker_id, newLocationName){
					$('[data-number="'+marker_id+'"').val(newLocationName)
					MapTools.calcAndDrawRoute()
				})

				MapTools.addListener("onPlacePicked", function(lat, lgn){
					//prevent from piking order
					if (loadOrder && orderInfo.status!="updating") {
						return;
					}
					BootstrapDialog.show({
						title: 'Address picking',
						message : function(dialog){
							var msg = $("<div><p>You picked location on map, choose what to do with it<p></div>"),
								getButton= function(label_postfix, context, inputs_selector, add_btn_selector){
									var inputs = context.find(inputs_selector),
										addBtn = context.find(add_btn_selector),
										canBeAdded = addBtn.length>0,
										canBeSetted = false,
										emptyInput = null

									if (inputs.length==1){//if we heve only 1 input we can set in anyway
										canBeSetted = true
										emptyInput = inputs
									} else {
										inputs.each(function(i,input){
											if ($(input).val().length<1) {
												canBeSetted = true
												emptyInput = $(input)
												return false
											}
										})
									}
									if ((!canBeSetted && !canBeAdded) || (inputs.length==0 && !canBeAdded)) {
										return null;
									}
									var label = '',
										action = ''
									if (canBeAdded) {
										label = "Add as "
										action = "add"
									} else {
										label = "Set as "
										action = "set"
									}
									label += label_postfix
									var btn = $('<button type="button" class="btn btn-default" data-action="'+action
										+'" data-target="'+label_postfix+'">'+label+'</button>')
									btn.bind("click", function(e){
										var jqDialog = $(dialog.$modalBody)
										jqDialog.html("")
										jqDialog.append(Templates.getWhiteLoader())
										MapTools.getNameForLocation(lat, lgn, function(locationName){
											if (action=="add"){
												var startNumber = addBtn.attr('data-start-number'),
													hasCarsAmount = (addBtn.attr('data-mod')=="addCarsAmount"),
													name = addBtn.attr("data-name"),
													newAddressField = Templates.getAddress(tmpStorage.favouriteLocations, name, hasCarsAmount, true, startNumber)
												emptyInput = newAddressField.find("input")[0]
												$(addBtn.parent()).find('[data-type="address-group"]').append(newAddressField)
												MapTools.modAutocompleteAddressInput(emptyInput, updateRoutes)
												emptyInput = $(emptyInput)
											}
											emptyInput.val(locationName).trigger('change')
											dialog.close()
										})
									})
									return btn
								}
							var context = blocks.content.find('#addressesContainer'),
								btn = getButton("source", context, 'input[name="start_addresses"]', 'button[data-name="start_addresses"]')
							if (btn != null){ msg.append(btn) }
							btn = getButton("intermediate", context, 'input[name="intermediate_addresses"]', 'button[data-name="intermediate_addresses"]')
							if (btn != null){ msg.append(btn) }
							btn = getButton("destination", context, 'input[name="destination_addresses"]', 'button[data-name="destination_addresses"]')
							if (btn != null){ msg.append(btn) }
							return msg
						},
						buttons: [{
							label: 'Cancel',
							action: function(dialog){ dialog.close(); }
						}]
					})
				})

				MapTools.addListener("onDistanceChanged", function(newDistance){
					var priceHolder = container.find('[data-type="price-holder"]'),
						priceValue = priceHolder.find('[data-type="price-value"]')
					//console.log(newDistance)
					priceValue.html("&lt;price for "+(Math.round(newDistance/100)/10)+" km and selected features&gt;")
					priceHolder.show()
				})

				serviceTypes.trigger("change")
							
				updateLocationsLists()
			}

			//init loader and bind callback when all necessary datas will be loaded
			var loader = new Loader()
			loader.addCallBack(function(){ createDOM() })
			//data loading
			var ids = loader.getArrayUniqId(3)
			DataTools.getUser(loader, ids[0], function(status, response, userInfo){
				tmpStorage.user = userInfo
			})
			DataTools.getServiceTypes(loader, ids[1], function(status, response, serviceTypes){
				tmpStorage.serviceTypes = serviceTypes
			})
			if (loadOrder) {
				DataTools.getOrderInfo(loader, ids[2], function(status, response, orderInfo){
					tmpStorage.currentOrder = orderInfo

				}, orderId, secretKey)
			} else {
				loader.setStatus(ids[2], 'no reason for load this')
			}

		},
		updateHeader = function(){
			var items = [],
				actions = [],
				getItem = Templates.getHeaderItem

			for (var i in pages){
				var page = pages[i]
				if ($.isSet(page.action)){
					if (!$.isSet(page.action)) continue
					if (page.hideLogged && tmpStorage.user.isLogged) continue
					if (page.hideNonLogged && !tmpStorage.user.isLogged) continue

					if (page.pullRight){
						actions.push(getItem(page))
					} else {
						items.push(getItem(page))
					}
				}
			}
			
			//TODO:add username

			blocks.header_items.html("")
			blocks.header_actions.html("")
			var place_in = function(items, target){
				var item;
				for(var i=0, item = items[i]; i<items.length; i++, item = items[i]){
					target.append(item)
				}
				target.find('[data-action="'+current_page_name+'"]').closest('li').addClass("active")
			}
			place_in(items, blocks.header_items)
			place_in(actions, blocks.header_actions)
		},
		initHeader = function(){
			blocks.header = Templates.getHeaderContainer()
			blocks.header_items = blocks.header.find('[data-type="menu-list"]')
			blocks.header_actions = blocks.header.find('[data-type="action-list"]')
			var toggleBtn = blocks.header.find(".navbar-toggle"),
				menuOnClick = function(e){
					e.preventDefault() 
					var target = $(e.target)
					if (target.prop("tagName")=="SPAN"){
						target = target.parent()
					}
					var	new_page = target.attr("data-action")
					if (new_page != current_page_name){
						if (toggleBtn.css("display")!="none"){
							toggleBtn.click()
						}
						pages[new_page].show()
						updateHeader()
					}
				}
			blocks.header_items.bind("click", menuOnClick)
			blocks.header_actions.bind("click", menuOnClick)
			updateHeader()
		},
		updateRoutes = function(invoker, place){
			invoker = $(invoker)
			var id = invoker.attr("data-number"),
				location = place.geometry.location,
				type = invoker.attr('name').split('_')[0]
			MapTools.addMarker(id, location, type)
			MapTools.markersFitWindow()
		},
		updateLocationsLists = function(){
			var createDOM = function(){
				var FL = tmpStorage.favouriteLocations
				if (FL.length>0){
					$.each(blocks.content.find('[data-type="address-group"] .input-group'),function(i,group){
						var list = $(group).find('[data-type="dropdown-address-list"]'),
						holder = list.closest(".input-group")
							isRemovable = list.has('[data-action="remove"]').length > 0
						if (list.length>0){
							list.remove()
							holder.append(Templates.getDropDownAddress(FL, isRemovable))
						}
					})
				}
			}
			//init loader and bind callback when all necessary datas will be loaded
			var loader = new Loader()
			loader.addCallBack(function(){ createDOM() })
			//binding data loaders
			DataTools.getFavLocations(loader, "fav_locations", function(status, response, favouriteLocations){
				tmpStorage.favouriteLocations = favouriteLocations
			})
		},
		addressesClick = function(e){
			var target = $(e.target),
				tagName = target[0].tagName.toLowerCase()
			//select fav location part
			if (tagName == "a" || tagName == "small"){
				var input = target.closest('.input-group').find('[data-type="address"]'),
					text_container = target;
				if (tagName == "a") {
					text_container = target.find("small")
				}
				if ( text_container.closest('li').attr('data-action')=="none" ) {
					return;
				}			
				//.trigger('change'); is important whe you change value programly onChange event doesn`t calls
				input.val(text_container.html()).trigger('change')
				return;
			} 
			//remove and add address part
			if (tagName == "button" || tagName == "span"){
				var btn = target
				if (tagName == "span"){
					btn = target.closest("button")
				}
				if (btn.attr('data-action')=="remove"){
					var container = target.closest('.input-group'),
						input = container.find('input'),
						markerId = input.attr("data-number")
					MapTools.removeMarker(markerId)
					MapTools.markersFitWindow()
					container.remove()
					return;
				} else if (btn.attr('data-action')=="add"){
					var startNumber = btn.attr('data-start-number'),
						hasCarsAmount = (btn.attr('data-mod')=="addCarsAmount"),
						name = btn.attr("data-name"),
						newAddressField = Templates.getAddress(tmpStorage.favouriteLocations, name, hasCarsAmount, true, startNumber),
						input = newAddressField.find('[data-type="address"]')[0]
					$(target.parent()).find('[data-type="address-group"]').append(newAddressField)
					MapTools.modAutocompleteAddressInput(input, updateRoutes)
					return;
				}
			}
		},
		favAddressesClick = function(e){
			var target = $(e.target),
				tagName = target[0].tagName.toLowerCase()
			//select fav location part
			if (tagName == "a" || tagName == "small"){
				var input = target.closest('.input-group').find('input'),
					text_container = target;
				if (tagName == "a") {
					text_container = target.find("small")
				}
				if ( text_container.closest('li').attr('data-action')=="none" ) {
					return;
				}			
				//.trigger('change'); is important whe you change value programly onChange event doesn`t calls
				input.val(text_container.html()).trigger('change')
				return;
			} 
			//remove and add address part
			if (tagName == "button" || tagName == "span"){
				var btn = target
				if (tagName == "span"){
					btn = target.closest("button")
				}
				if (btn.attr('data-action')=="remove"){
					var container = target.closest('.input-group.fav-address'),
						input = $(container.find('input')[0]),
						markerId = input.attr("data-number")
					MapTools.removeMarker(markerId)
					MapTools.markersFitWindow()
					container.remove()
					return;
				} else if (btn.attr('data-action')=="add"){
					var startNumber = btn.attr('data-start-number'),
						name = btn.attr("data-name"),
						newAddressBlock = Templates.getFavAddress(tmpStorage.favouriteLocations, name, startNumber),
						input = newAddressBlock.find('[data-type="address"]')[0]
					var holder = $(target.parent()).find('[data-type="address-group"]')
					console.log(holder)
					holder.append(newAddressBlock)
					MapTools.modAutocompleteAddressInput(input, updateRoutes)
					return;
				}
			}
		},
		createInputsForServiceType = function(holder, newServiceType, orderInfo) {
			MapTools.clearAllMarker()
			MapTools.markersFitWindow()
			holder.html("")
			holder.append(Templates.getWhiteLoader)
			var loadOrder = $.isSet(orderInfo)
			//create DOM))
			var createDOM = function(){
				var SD = tmpStorage.serviceDescription,
					SF = tmpStorage.serviceFeatures,
					FL = tmpStorage.favouriteLocations,
					user = tmpStorage.user
				holder.html("")
				//add contacts
				if (!user.isLogged){
					holder.append(Templates.getContacts())
				}
				console.log(SD)
				var timeBlock = Templates.getTime(SD.timing.indexOf("now")>-1, SD.timing.indexOf("specified")>-1)
				holder.append(timeBlock)
				if (loadOrder) {
					if (SD.timing.indexOf("specified")>-1) {
						var time = timeBlock.find('#time_specified')
						if (time.length>0) {
							time.val(orderInfo.executionDate)
							timeBlock.find('[value="specified"]').prop('checked', true)
						} else {
							timeBlock.find('[value="now"]').prop('checked', true)
						}
					}
				}
				//add addresses
				var addresses = $(Templates.getAddressesContainer())
				var mult = SD.multipleSourceLocations
				var addrGroup = $(Templates.getAddressesGroup("Source:","start_addresses", mult, mult)),
					addrHolder = addrGroup.find('[data-type="address-group"]')
				if (loadOrder) {
					var sa = orderInfo.startAddresses
					for (var i=0;i<sa.length;i++){
						var address = Templates.getAddress(FL,"start_addresses", mult, false)
						address.find('[data-type="address"]').val(sa[i])
						addrHolder.append(address)
					}
				} else {
					addrHolder.append(Templates.getAddress(FL,"start_addresses", mult, false))
				}
				addresses.append(addrGroup)

				//console.log(SD)
				if (SD.chain) {
					addrGroup = $(Templates.getAddressesGroup("Intermediate:","intermediate_addresses", true, false))

					if (loadOrder) {
						var ia = orderInfo.intermediateAddresses
						for (var i=0;i<ia.length;i++){
							var address = Templates.getAddress(FL,"intermediate_addresses", true, false)
							address.find('[data-type="address"]').val(ia[i])
							addrHolder.append(address)
						}
					}
					addresses.append(addrGroup)
				}

				if (SD.destinationRequired) {
					var mult = SD.multipleDestinationLocations
					addrGroup = $(Templates.getAddressesGroup("Destination:","destination_addresses", mult, mult))
					//addrGroup.find('[data-type="address-group"]').append(Templates.getAddress(FL,"destination_addresses", false))
					//addresses.append(addrGroup)


					addrHolder = addrGroup.find('[data-type="address-group"]')
					if (loadOrder) {
						var da = orderInfo.destinationAddresses
						for (var i=0;i<da.length;i++){
							var address = Templates.getAddress(FL,"destination_addresses", false)
							address.find('[data-type="address"]').val(da[i])
							addrHolder.append(address)
						}
					} else {
						addrHolder.append(Templates.getAddress(FL,"destination_addresses", false))
					}
					addresses.append(addrGroup)
				}

				holder.append(addresses)
				//add cars number input
				if (SD.specifyCarsNumbers && !SD.multipleDestinationLocations && !SD.multipleSourceLocations) {
					holder.append(Templates.getCarsAmount(SD.minCarsNumbers))
				}

				if (loadOrder) {
					var cars = holder.find('[data-type="cars-amount"]')
					for (var i=0;i<cars.length;i++){
						var input = $(cars[i])
						input.val(orderInfo.carsAmount[i].length)
					}
				}
				console.log("----------------------------------")
				console.log(orderInfo)
				var features = Templates.getFeaturesContainer()
				features.append()
				for (var i in SF) {
					feature = SF[i]
					console.log(feature)
					if (feature.isCategory==true){
						var featureBlock = Templates.getFeaturesGroup(feature,"features", !loadOrder)
						features.append(featureBlock)
						if (loadOrder) {
							switch (feature.featureSpecialName) {
								case "car_class" : 
									if ($.isSet(orderInfo.carClassId)) {
										featureBlock.find('[value="'+orderInfo.carClassId+'"]').prop('checked', true)
									}
									break
								case "payment_type" : 
									if ($.isSet(orderInfo.paymentType)) {
										featureBlock.find('[value="'+orderInfo.paymentType.toLowerCase()+'"]').prop('checked', true)
									}
									break
								case "driver_sex" : 
									if ($.isSet(orderInfo.driverSex)) {
										featureBlock.find('[value="'+orderInfo.driverSex.toLowerCase()+'"]').prop('checked', true)
									} else {
										featureBlock.find('[value="any"]').prop('checked', true)
									}
									break
								case "features" : 
									for (var j=0;j<orderInfo.features.length;j++){
										var val = orderInfo.features[j]
										featureBlock.find('[value="'+val+'"]').prop('checked', true)
									}
									break

							}
						}
					} else {
						features.append(Templates.getFeaturesItem(feature,"features"))
					} 
				}
				holder.append(features)

				holder.find('input[data-type="address"]').each(function(i,input){
					MapTools.modAutocompleteAddressInput(input, updateRoutes)
				})

				if (loadOrder && orderInfo.status!="updating"){
					Templates.lockAllControls($(orderDetails))
				}
				//bind events
				addresses.bind("click", addressesClick)
			}

			//init loader and bind callback when all necessary datas will be loaded
			
			var loader = new Loader()
			loader.addCallBack(function(){ createDOM() })
			//binding data loaders
			var ids = loader.getArrayUniqId(3)
			DataTools.getServiceDescription(loader, ids[0], function(status, response, serviceDescription){
				tmpStorage.serviceDescription = serviceDescription
			}, newServiceType)
			DataTools.getServiceFeatures(loader, ids[1], function(status, response, serviceFeatures){
				tmpStorage.serviceFeatures = serviceFeatures
			}, newServiceType)
			DataTools.getFavLocations(loader, ids[2], function(status, response, favouriteLocations){
				tmpStorage.favouriteLocations = favouriteLocations
			}, newServiceType)
		},
		showLoginPage = function(){
			current_page_name = "login"
			showLoadPage()
			var container = Templates.getLogin(),
				loginForm = container.find("#login-form"),
				loginBtn = container.find('[data-action="login"]')
			
    		Templates.makeNiceSubmitButton({
    			form : loginForm,
            	button : loginBtn,
    			success : function(response){
					//loadPage("new-order")
					console.log("success")
					console.log(response)
					if (!response.authenticationStatus){
						BootstrapDialog.show({
							type: BootstrapDialog.TYPE_DANGER,
							title: "Invalid login credentioals",
							message: "Invalid login credentioals",
						})
					} else {
						
						var loader = new Loader()
						loader.addCallBack(function(){ 
							console.log(tmpStorage.user)
							switch (tmpStorage.user.role) {
								case "ROLE_CUSTOMER" : 
									pages["make-order"].show()
									break;
								case "ROLE_DRIVER" : 
									window.location = "/driver";
									break;
								case "ROLE_ADMINISTRATOR" : 
									window.location = "/admin";
									break;
								default:
									console.log("UNKNOW ROLE NAME");
									break;
							}
							
						})
						//data loading
						DataTools.getUser(loader, loader.getUniqId(), function(status, response, userInfo){
							tmpStorage.user = userInfo
						})
					}
				},
				error : function(response){
					console.log("error")
					console.log(response)
					BootstrapDialog.show({
						type: BootstrapDialog.TYPE_DANGER,
						title: "Server error",
						message: "Server returns error with status '"+response.statusText+"'",
					})
				}
    		}/*, DataTools.tryLogin*/)

			//fill page
			blocks.content.html("")
			blocks.content.append(container)
		},
		showRegisterPage = function(){
			showLoadPage()
			current_page_name = "register"
			var container = Templates.getRegistration(),
				form = container.find("#reg-form"),
				submBtn = container.find('[data-action="reg"]')
			
				Templates.makeNiceSubmitButton({
					form : form,
					button : submBtn,
					success : function(response){
						console.log(response)
						BootstrapDialog.show({
							type: BootstrapDialog.TYPE_SUCCESS,
							title: "Congradulations!",
							message: "You are succesfully registered. Plese, check your mail for confirmation link",
						})
					},
					error : function(response){
						console.log(response)
						BootstrapDialog.show({
							type: BootstrapDialog.TYPE_DANGER,
							title: "Server error",
							message: "Server returns error with status '"+response.statusText+"'",
						})
					}
				})

			//fill page
			blocks.content.html("")
			blocks.content.append(container)
		},
		showAboutPage = function(){
			showLoadPage()
			current_page_name = "about"
			var container = Templates.getAbout()
			//fill page
			blocks.content.html("")
			blocks.content.append(container)
		},
		showViewOrderPage = function(){
			showLoadPage()
			current_page_name = "view-order"
			var container = Templates.getAbout()
			//fill page
			blocks.content.html("")
			blocks.content.append(container)
		},
		showHistoryPage = function(holder){
			current_page_name = "history"
			showLoadPage()

			var allowedSortProperties = []
			var user = null
			var history = null
			var pagerInfo = null
			var createDOM = function(){
				var container = Templates.getHistoryContainer(),
					filters = container.find('[data-type="filters"]'),
					orders = container.find('[data-type="orders"]')
					pager = container.find('[data-type="pager"]')
				console.log(allowedSortProperties)
				console.log(history)
				console.log(pagerInfo)
				filters.append(Templates.getHistoryFilters(allowedSortProperties))
				orders.append(Templates.getHistoryOrders(history))
				pager.append(Templates.getPager(pagerInfo))


				Templates.makeNiceSubmitButton({
						form : container.find('form'),
						button : container.find('[data-action="apply"]')
					})

				orders.on('click', '[data-order-id]', function(e){
					var orderId = $(e.target).attr('data-order-id')
					showOrderPage(orderId)
				})
				pager.on('click', 'a', function(e){
					var currentPage = pager.find('.active [data-page]').attr('data-page')
					orders.html('')
					orders.append(Templates.getWhiteLoader())
					var createDOM = function(){

					}

					var loader = new Loader()
					loader.addCallBack(function(){ createDOM() })
					DataTools.getUserHistory(loader, loader.getUniqId(), function(status, response, historyInfo){
						var pd = historyInfo.pageDetails
						pagerInfo = {
							currentPage : pd.pageNumber+1,
							pagesAmount : pd.totalPage,
							pagesAtOnce : 5
						}
						history = historyInfo.orders;
						pager.html('')
						pager.append(Templates.getPager(pagerInfo))
						orders.html('')
						orders.append(Templates.getHistoryOrders(history))
					}, user.userId, currentPage)

				})
				//fill page
				blocks.content.html("")
				blocks.content.append(container)
			}

			var loader = new Loader()
			loader.addCallBack(function(){ createDOM() })
			var ids = loader.getArrayUniqId(3)
			DataTools.getAllowedSortProperties(loader, ids[0], function(status, response, sortProperties){
				allowedSortProperties = sortProperties
			})

			DataTools.getUser(loader, ids[1], function(status, response, userInfo){
				user = userInfo
				//don`t do this (one loader in other)
				DataTools.getUserHistory(loader, ids[2], function(status, response, historyInfo){
					var pd = historyInfo.pageDetails
					pagerInfo = {
						currentPage : pd.pageNumber+1,
						pagesAmount : pd.totalPage,
						pagesAtOnce : 5
					}
					history = historyInfo.orders;
				}, user.userId)
			})


		},
		showEditAccountPage = function(){
			current_page_name = "account"
			showLoadPage()
			var createDOM = function(){
				console.log(tmpStorage.favouriteLocations)
				var locations = []
				var fav_locations = tmpStorage.favouriteLocations
				for (var i=0;i<fav_locations.length;i++){
					var location = fav_locations[i]
					console.log(location)
					if ($.isSet(location) && !location.isUserLocation) {
						console.log("pushed")
						locations.push({
							name : location.name,
							address : location.address,
							id : location.id,
							action : "none",
						})
					}
				}
				console.log(locations)
				var container = Templates.getEditAccount(tmpStorage.favouriteLocations),
				saveBtns = container.find('[data-action="save"]'),
				addresses = container.find('#addresses')
				addresses.bind('click',favAddressesClick)
				
				$.each(saveBtns, function(i,btn){
					btn = $(btn)
					var formId = btn.attr("data-form-id")
					if (formId == "addresses") {
						Templates.makeNiceSubmitButton({
							form : container.find('#'+formId),
							button : btn,
							dataFormater : function(){
								var data = [],
									addresses = container.find('[data-type="address-group"]').find('.input-group.fav-address')
								for (var j=0;j<locations.length;j++){
									locations[j].finded = false
								}
								for (var i = 0;i<addresses.length;i++){
									var addressBlock = $(addresses[i]),
										address = addressBlock.find('[data-type="address"]').val(),
										name = addressBlock.find('[data-type="address-name"]'),
										locationId = name.attr('data-id'),
										name = name.val()
									if (!$.isSet(locationId)) {
										data.push({
											name : name,
											address : address,
											action : "add"
										})
										continue;
									}
									//search current location in array
									for (var j=0;j<locations.length;j++){
										var location = locations[j]
										if (location.id == locationId ){
											location.finded = true
											console.log("finded",location.id)
											if (location.name != name || location.address != address) {
												data.push({
													name : name,
													address : address,
													id : locationId,
													action : "update"
												})
												console.log("updated",location.id)
											}
											break
										}
									}
								}
								//remove unexists locations
								for (var i=0;i<locations.length;i++){
									var location = locations[i]
									if (location.finded !== true ){
										data.push({
											id : location.id,
											action : "remove"
										})
										console.log("removed",location.id)
									}
								}
								console.log(data)
								return JSON.stringify(data)
								//return []

							},
							success : function(response){
								console.log("success")
								console.log(response)
								//loadPage("new-order")
							},
							error : function(response){
								console.log("error")
								console.log(response)
								BootstrapDialog.show({
									type: BootstrapDialog.TYPE_DANGER,
									title: "Server error",
									message: "Server returns error with status '"+response.statusText+"'",
								})
							}
						})
					} else {
						Templates.makeNiceSubmitButton({
							form : container.find('#'+formId),
							button : btn,
							success : function(response){
								//loadPage("new-order")
							},
							error : function(response){
								BootstrapDialog.show({
									type: BootstrapDialog.TYPE_DANGER,
									title: "Server error",
									message: "Server returns error with status '"+response.statusText+"'",
								})
							}
						})
					}
				})

				//locationID = container.find('[data-id]').attr('data-id')
				
				//fill page
				blocks.content.html("")
				blocks.content.append(container)		
			}
			//init loader and bind callback when all necessary datas will be loaded
			var loader = new Loader()
			loader.addCallBack(function(){ 
				createDOM()
			})
			//binding data loaders
			var ids = loader.getArrayUniqId(1)
			DataTools.getFavLocations(loader, ids[0], function(status, response, favouriteLocations){
				tmpStorage.favouriteLocations = favouriteLocations
			})
		},
		initBasePage = function(){
			//binding elements
			initHeader()
			blocks.content = Templates.getContentContainer()
			blocks.body = $("body")
			//fill page
			blocks.body.html("")
				.append(blocks.header)
				.append(blocks.content)
		},
		init = function(){
			MapTools.addListener("onGeolocationAllowed",function(pos){
				MapTools.getNameForLocation(pos.latitude, pos.longitude, function(name){
					DataTools.setUserLocation("Your location", name, true)
					updateLocationsLists()
				})
			})

			var loader = new Loader()
			loader.addCallBack(function(){ 
				console.log("hi")
				initBasePage()
				console.log("show default page")
				pages["default"].show()
			})
			loader.addThreadNames("user")
			DataTools.getUser(loader, "user", function(status, response, userInfo){
				tmpStorage.user = userInfo
				console.log(tmpStorage.user)
			})
		},
		pages = {
			"default" : {
				show : showOrderPage
			},
			"load" : {
				show : showLoadPage
			},
			"blocked" : {
				show : showBlockedPage
			},
			"login" : {
				label : "Sign In",
				action : "login",
				show : showLoginPage,
				pullRight : true,
				icon : "log-in",
				hideLogged : true
			},
			"register" : {
				label : "Sign Up",
				action : "register",
				show : showRegisterPage,
				pullRight : true,
				icon : "user",
				hideLogged : true
			},
			"logout" : {
				label : "Logout",
				action : "logout",
				show : showLogoutPage,
				pullRight : true,
				icon : "log-out",
				hideNonLogged : true
			},
			"about" : {
				label : "About",
				action : "about",
				show : showAboutPage
			},
			"make-order" : {
				label : "Make order",
				action : "make-order",
				show : showOrderPage
			},
			"history" : {
				label : "View order history",
				action : "history",
				show : showHistoryPage,
				hideNonLogged : true
			},
			"account" : {
				label : "Edit account",
				action : "account",
				show : showEditAccountPage,
				hideNonLogged : true
			},
		}

	return public_interface = {
		"init" : init,
	}
})()


$(document).ready(function(){
	App.init()
})
  