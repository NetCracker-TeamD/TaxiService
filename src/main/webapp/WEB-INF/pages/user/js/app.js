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
		showLogoutPage = function(){showOrderPage()},
		showOrderPage = function(orderId, secretKey){
			showLoadPage()
			current_page_name = "make-order"
			updateHeader()
			var loadOrder = $.isSet(orderId)
			var createDOM = function(){
				//create page in memory		
				var orderInfo = null
				if ($.isSet(tmpStorage.currentOrder)) {
					orderInfo = tmpStorage.currentOrder
				}
				var 
					user = tmpStorage.user,
					container = Templates.getOrderPage(orderInfo),
					serviceTypes = container.find("#serviceType"),
					orderDetails = container.find('[data-type="order-details"]'),
					map = container.find('[data-type="map"]'),
					orderForm = container.find('#orderForm'),
					enableEditing = (loadOrder) ? (user.isLogged && !user.isBlocked) : false
				if (loadOrder) {
				} else {
					var makeOrderBtn = container.find('[data-action="make-order"]')
		   			Templates.makeNiceSubmitButton({
		   				form : orderForm,
		   				button : makeOrderBtn,
		   				success : function(response){
							var watchIt = function(){
								console.log("go to track link")
								showOrderPage("id","secret")
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

				serviceTypes.bind("change", function(e){
					var newServiceType = $(e.target).find('option[value="'+$(e.target).val()+'"]').text()
					container.find('[data-type="price-holder"]').hide()
					createInputsForServiceType(orderDetails, newServiceType)
				})

				//fill page
				blocks.content.html("")
				blocks.content.append(container)

				//[0] coz new google.maps.Map accepts clear html element, not wraped by jquery
				MapTools.init(map[0])
				MapTools.enableDraggableMarkers(true)
				MapTools.removeListenerChain("onMarkerMoved")
				MapTools.removeListenerChain("onPlacePicked")
				MapTools.removeListenerChain("onDistanceChange")
				MapTools.addListener("onMarkerMoved", function(marker_id, newLocationName){
					$('[data-number="'+marker_id+'"').val(newLocationName)
					MapTools.calcAndDrawRoute()
				})

				MapTools.addListener("onPlacePicked", function(lat, lgn){
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
							holder.append(Templates.getDropDownAddressHTML(FL, isRemovable))
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
						input = newAddressField.find("input")[0]
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
		createInputsForServiceType = function(holder, newServiceType, loader) {
			MapTools.clearAllMarker()
			MapTools.markersFitWindow()
			holder.html("")
			holder.append(Templates.getWhiteLoader)
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
				holder.append(Templates.getTime(SD.timing.indexOf("now")>-1, SD.timing.indexOf("specified")>-1))
				//add addresses
				var addresses = $(Templates.getAddressesContainer())
				var mult = SD.multipleSourceLocations
				var addrGroup = $(Templates.getAddressesGroup("Source:","start_addresses", mult, mult))
				addrGroup.find('[data-type="address-group"]').append(Templates.getAddress(FL,"start_addresses", mult, false))
				addresses.append(addrGroup)

				//console.log(SD)
				if (SD.chain) {
					addrGroup = $(Templates.getAddressesGroup("Intermediate:","intermediate_addresses", true, false))
					addresses.append(addrGroup)
				}

				if (SD.destinationRequired) {
					var mult = SD.multipleDestinationLocations
					addrGroup = $(Templates.getAddressesGroup("Destination:","destination_addresses", mult, mult))
					addrGroup.find('[data-type="address-group"]').append(Templates.getAddress(FL,"destination_addresses", false))
					addresses.append(addrGroup)
				}

				holder.append(addresses)
				//add cars number input
				if (SD.specifyCarsNumbers && !SD.multipleDestinationLocations && !SD.multipleSourceLocations) {
					holder.append(Templates.getCarsAmount(SD.minCarsNumbers))
				}

				var features = Templates.getFeaturesContainer()
				features.append()
				for (var i in SF) {
					feature = SF[i]
					if (feature.isCategory==true){
						features.append(Templates.getFeaturesGroup(feature,"features"))
					} else {
						features.append(Templates.getFeaturesItem(feature,"features"))
					}
				}
				holder.append(features)

				holder.find('input[data-type="address"]').each(function(i,input){
					MapTools.modAutocompleteAddressInput(input, updateRoutes)
				})

				//bind events
				addresses.bind("click", addressesClick)
			}

			//init loader and bind callback when all necessary datas will be loaded
			if (!$.isSet(loader)) {
				loader = new Loader()
			}
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
				},
				error : function(response){
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
			var container = Templates.getViewHistory(DataTools.getHistory())
			//fill page
			blocks.content.html("")
			blocks.content.append(container)
		},
		showEditAccountPage = function(){
			current_page_name = "account"
			showLoadPage()
			var createDOM = function(){
				console.log(tmpStorage.favouriteLocations)
				var container = Templates.getEditAccount(tmpStorage.favouriteLocations),
				saveBtns = container.find('[data-action="save"]'),
				addresses = container.find('#addresses')
				addresses.bind('click',favAddressesClick)
				
				$.each(saveBtns, function(i,btn){
					btn = $(btn)
					var formId = btn.attr("data-form-id")
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

				})
				
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
			"view-orders" : {
				label : "View order history",
				action : "view-orders",
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
  