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
		showLoadPage = function(){
			blocks.content.html("")
			blocks.content.append(Templates.getLoader("Page is loading, please wait"))
		},
		showBlockedPage = function(){
			blocks.content.html("")
			blocks.content.append(Templates.getBlocked())
		},
		loadPage = function(pageName, addition_data){
			//TODO: add history api support
			showLoadPage()
			current_page_name = pageName
			updateHeader()

			//if logout
			//if about
			var user = DataTools.getUser() 
			if (user.isBlocked){
				showBlockedPage()
				return;
			}
			switch (pageName){
				case "new-order": 
					var loader = new Loader()
					loader.addCallBack(function(){ showOrderPage(blocks.content, addition_data) })
					loader.addThreadNames("services")
					DataTools.loadServices(loader, "services")
					break;
				case "login":
					showLoginPage(blocks.content, addition_data)
					break;
				case "register":
					showRegisterPage(blocks.content, addition_data)
					break;
				case "about": 
					showAboutPage(blocks.content, addition_data)
					break;
				case "account":
					showEditAccountPage(blocks.content, addition_data)
					break;
				case "order":
					showViewOrderPage(blocks.content, addition_data)
					break;
				case "history":
					var loader = new Loader()
					loader.addCallBack(function(){ showHistoryPage(blocks.content, addition_data) })
					loader.addThreadNames("history")
					DataTools.loadHistory(loader, "history")
					break;
				default :
					loadPage("new-order", addition_data)
					break;
			}
		},
		updateHeader = function(key, value){
			var user = DataTools.getUser(),
				items = [],
				actions = [],
				getItem = Templates.getHeaderItem
			
			items.push(getItem({ label : "About", action : "about" }))
			items.push(getItem({ label : "Make order", action : "new-order"}))
			if (user.isLogged){
				//TODO:add username
				items.push(getItem({ label : "View History", action : "history"}))
				items.push(getItem({ label : "Edit Account", action : "account"}))
				actions.push(getItem({ label : "Logout", action : "logout", icon : "log-out"}))
			} else {
				actions.push(getItem({ label : "Register", action : "register", icon : "user"}))
				actions.push(getItem({ label : "Login", action : "login", icon : "log-in"}))
			}

			//fill pages
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
						loadPage(new_page)
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
		}
		updateLocationsLists = function(){
			var locationsList = DataTools.getFavLocations()
			if (locationsList.length>0){
				$.each(blocks.content.find('[data-type="address-group"] .input-group'),function(i,group){
					var list = $(group).find('[data-type="dropdown-address-list"]'),
						isRemovable = list.has('[data-action="remove"]').length > 0
					list.remove()
					$(group).append(Templates.getDropDownAddressHTML(locationsList, isRemovable))
				})
			}
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
						newAddressField = Templates.getAddress(DataTools.getFavLocations(), name, hasCarsAmount, true, startNumber),
						input = newAddressField.find("input")[0]
					$(target.parent()).find('[data-type="address-group"]').append(newAddressField)
					MapTools.modAutocompleteAddressInput(input, updateRoutes)
					return;
				}
			}
		},
		createInputsForServiceType = function(holder, serviceDescription, featuresList) {
			MapTools.clearAllMarker()
			MapTools.markersFitWindow()
			var SD = serviceDescription,
				user = DataTools.getUser()
			//create DOM
			holder.html("")
			//add contacts
			if (!user.isLogged){
				holder.append(Templates.getContacts())
			}
			holder.append(Templates.getTime(SD.timing.indexOf("now")>-1, SD.timing.indexOf("specified")>-1))
			var locationsList = DataTools.getFavLocations()
			//add addresses
			var addresses = $(Templates.getAddressesContainer())
			var mult = SD.multipleSourceLocations
			var addrGroup = $(Templates.getAddressesGroup("Source:","start_addresses", mult, mult))
			addrGroup.find('[data-type="address-group"]').append(Templates.getAddress(locationsList,"start_addresses", mult, false))
			addresses.append(addrGroup)

			//console.log(SD)
			if (SD.chain) {
				addrGroup = $(Templates.getAddressesGroup("Intermediate:","intermediate_addresses", true, false))
				addresses.append(addrGroup)
			}

			if (SD.destinationRequired) {
				var mult = SD.multipleDestinationLocations
				addrGroup = $(Templates.getAddressesGroup("Destination:","destination_addresses", mult, mult))
				addrGroup.find('[data-type="address-group"]').append(Templates.getAddress(locationsList,"destination_addresses", false))
				addresses.append(addrGroup)
			}

			holder.append(addresses)
			//add cars number input
			if (SD.specifyCarsNumbers && !SD.multipleDestinationLocations && !SD.multipleSourceLocations) {
				holder.append(Templates.getCarsAmount(SD.minCarsNumbers))
			}

			var features = Templates.getFeaturesContainer()
			features.append()
			for (var i in featuresList) {
				feature = featuresList[i]
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
		},
		showOrderPage = function(holder){
			var container = Templates.getOrderPage(),
				serviceTypesList = DataTools.getServiceTypes(),
				serviceTypes = container.find("#serviceType"),
				orderDetails = container.find('[data-type="order-details"]'),
				map = container.find('[data-type="map"]'),
				orderForm = container.find('#orderForm'),
				makeOrderBtn = container.find('[data-action="make-order"]')

   			Templates.makeNiceSubmitButton({
   				form : orderForm,
   				button : makeOrderBtn,
   				success : function(response){
					console.log("response is '"+response+"'")
					var watchIt = function(){
						console.log("go to track link")
					}
					BootstrapDialog.show({
						type: BootstrapDialog.TYPE_SUCCESS,
						title: "Order successfully created",
						closable: false,
						message: function(dialog){
							var msg = $("<div>Your order successfully created<br>You can track it via this link </div>"),
								link = $("<a href='/somelink''>some link with tracknumber</a>")
							link.bind("click", function(e){
								e.preventDefault()
								dialog.close()
								watchIt()
							})
							msg.append(link)
							return msg
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
					onQueryEnded("error")
					BootstrapDialog.show({
						type: BootstrapDialog.TYPE_DANGER,
						title: "Server error",
						message: "Server returns error with status '"+response.statusText+"'",
					})
				}
   			})

			//fill service types
			$.each(serviceTypesList, function (i, item) {
			    serviceTypes.append($('<option>', { 
			        value: item.id,
			        text : item.name 
			    }))
			})

			serviceTypes.bind("change", function(e){
				var newServiceType = $(e.target).find('option[value="'+$(e.target).val()+'"]').text()
				//console.log(newServiceType)
				container.find('[data-type="price-holder"]').hide()
				createInputsForServiceType(orderDetails, 
					DataTools.getServiceDescription(newServiceType), 
					DataTools.getFeatureList(newServiceType))
			})

			//fill page
			holder.html("")
			holder.append(container)

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
						var msg = $("<div><p>You picked location on map, chose what to do with it<p></div>"),
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
												newAddressField = Templates.getAddress(DataTools.getFavLocations(), name, hasCarsAmount, true, startNumber)
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
		},
		showLoginPage = function(holder){
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
			holder.html("")
			holder.append(container)
		},
		showRegisterPage = function(holder){
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
			holder.html("")
			holder.append(container)
		},
		showAboutPage = function(holder){
			var container = Templates.getAbout()
			//fill page
			holder.html("")
			holder.append(container)
		},
		showViewOrderPage = function(holder){
			var container = Templates.getViewOrder()
			//fill page
			holder.html("")
			holder.append(container)
		}
		showHistoryPage = function(holder){
			var container = Templates.getViewHistory(DataTools.getHistory())
			//fill page
			holder.html("")
			holder.append(container)
		}
		showEditAccountPage = function(holder){
			var container = Templates.getEditAccount(DataTools.getFavLocations()),
				saveBtns = container.find('[data-action="save"]'),
				addresses = container.find('#addresses')

			addresses.bind('click',addressesClick)
			
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
			holder.html("")
			holder.append(container)			
		}
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
			initBasePage()
			loadPage("new-order")		
		}

	return public_interface = {
		"init" : init,
	}
})()


$(document).ready(function(){
	App.init()
})
  