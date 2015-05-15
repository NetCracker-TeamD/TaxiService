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
		lockAllControls = function(holder){
			holder.find(":input").prop({'readonly': true, 'disabled': true});
		},
		unlockAllControls = function(holder){
			holder.find(":input").prop({'readonly': false, 'disabled': false});
		},
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
					loader.addCallBack(function(){ showOrderPage(blocks.content) })
					DataTools.init(null, loader)
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
					showHistoryPage(blocks.content, addition_data)
					break;
				default :
					loadPage("new-order", addition_data)
					break;
			}
		},
		updateHeader = function(key, value){
			var user = DataTools.getUser(),
				items = [],
				actions = []
			
			items.push(Templates.getHeaderItem("About","/about", "about"))
			items.push(Templates.getHeaderItem("Make order","/new-order", "new-order"))
			if (user.isLogged){
				//TODO:add username
				items.push(Templates.getHeaderItem("View History","/history", "history"))
				items.push(Templates.getHeaderItem("Edit Account","/edit-account", "account"))
				actions.push(Templates.getHeaderItemIcon("Logout","/logout", "logout", "log-out"))
			} else {
				actions.push(Templates.getHeaderItemIcon("Register","/register", "register", "user"))
				actions.push(Templates.getHeaderItemIcon("Login","/login", "login", "log-in"))
			}
			//fill pages
			blocks.header_items.html("")
			for(var i in items){
				var item = items[i]
				blocks.header_items.append(item)
				//TODO: make better selector
				if (item.attr("data-action")==current_page_name ||
					item.find('[data-action="'+current_page_name+'"]').length>0){
					item.addClass("active")
				}
			}
			//fill action
			blocks.header_actions.html("")
			for(var i in actions){
				var action = actions[i]
				blocks.header_actions.append(action)
				if (action.attr("data-action")==current_page_name ||
					action.find('[data-action="'+current_page_name+'"]').length>0){
					action.addClass("active")
				}
			}
			console.log(current_page_name)
		},
		initHeader = function(){
			blocks.header = Templates.getHeaderContainer()
			blocks.header_items = blocks.header.find('[data-type="menu-list"]')
			blocks.header_actions = blocks.header.find('[data-type="action-list"]')
			var toggleBtn = blocks.header.find(".navbar-toggle"),
				menuOnClick = function(e){
					e.preventDefault() 
					var target = $(e.target),
						new_page = target.attr("data-action")
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
			var id = $(invoker).attr("data-number"),
				location = place.geometry.location
			MapTools.addMarker(id, location)
			MapTools.markersFitWindow()
		}
		updateLocationsLists = function(){
			var locationsList = DataTools.getFavLocations()
			if (locationsList.length>0){
				$.each(blocks.content.find('[data-type="address-group"] .input-group'),function(i,group){
					var igb = $(group).find(".input-group-btn"),
						isRemovable = false
					if (igb.has('[data-action="remove"]').length>0) {
						isRemovable = true
					}
					igb.remove()
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
			var addrGroup = $(Templates.getAddressesGroup("Source:","start_addresses", mult, mult, 0))
			addrGroup.find('[data-type="address-group"]').append(Templates.getAddress(locationsList,"start_addresses", mult, false))
			addresses.append(addrGroup)

			//console.log(SD)
			if (SD.chain) {
				addrGroup = $(Templates.getAddressesGroup("Intermediate:","intermediate_addresses", true, false, 100))
				addresses.append(addrGroup)
			}

			if (SD.destinationRequired) {
				var mult = SD.multipleDestinationLocations
				addrGroup = $(Templates.getAddressesGroup("Destination:","destination_addresses", mult, mult, 1000))
				addrGroup.find('[data-type="address-group"]').append(Templates.getAddress(locationsList,"destination_addresses", false, 1000))
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
			
			makeOrderBtn.bind("click", function(e){
				//TODO : add validation
				if (makeOrderBtn.attr("disabled") !== undefined) return;
				var method = orderForm.attr("method").toLowerCase(),
					url = orderForm.attr("action"),
					data = JSON.stringify(orderForm.serializeObject()),
					onQueryEnded = function(status){
						//enable form
						unlockAllControls(orderForm);
						makeOrderBtn.removeClass("active")
						makeOrderBtn.removeAttr("disabled")
					}
				//lock form
				lockAllControls(orderForm);
				makeOrderBtn.addClass("active")
				makeOrderBtn.attr("disabled","")

				method = (method != "get" && method != "post") ? "post" : method
				console.log(data)
				$.ajax({
					type: method,
					url: url,
					contentType: "application/json; charset=utf-8",
					data: data,
					cache: false,
            		processData:false,
					success : function(response){
						console.log("response is '"+response+"'")
						onQueryEnded("success")
						var watchIt = function(){
							console.log("go to track link")
						}
						BootstrapDialog.show({
							type: BootstrapDialog.TYPE_SUCCESS,
							title: "Order successfully created",
							closable: false,
							message: function(dialog){
								var msg = $("<div>Your order successfully created<br>You can track it via this link </div>"),
									link = $("<a href='#''>some link with tracknumber</a>")
								link.bind("click", function(e){
									dialog.close()
									watchIt()
								})
								msg.append(link)
								return msg
							},
							buttons: [
								{
									label : "Watch it",
									action: function(dialog){
										dialog.close()
										watchIt()
					                }
								}
							],							
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
				createInputsForServiceType(orderDetails, 
					DataTools.getServiceDescription(newServiceType), 
					DataTools.getFeatureList(newServiceType))
			})

			//fill page
			holder.html("")
			holder.append(container)

			//[0] coz new google.maps.Map accepts clear html element, not wraped by jquery
			MapTools.init(map[0])

			serviceTypes.trigger("change")
						
			updateLocationsLists()
		},
		showLoginPage = function(holder){
			var container = Templates.getLogin(),
				loginForm = container.find("#login-form"),
				loginBtn = container.find('[data-action="login"]')
			
			loginBtn.bind("click", function(e){
				//TODO : add validation
				if (loginBtn.attr("disabled") !== undefined) return;
				var method = loginForm.attr("method").toLowerCase(),
					url = loginForm.attr("action"),
					data = JSON.stringify(loginForm.serializeObject()),
					onQueryEnded = function(status){
						//enable form
						unlockAllControls(loginForm);
						loginBtn.removeClass("active")
						loginBtn.removeAttr("disabled")
					}
				//lock form
				lockAllControls(loginForm);
				loginBtn.addClass("active")
				loginBtn.attr("disabled","")

				method = (method != "get" && method != "post") ? "post" : method
				console.log(data)
				$.ajax({
					type: method,
					url: url,
					contentType: "application/json; charset=utf-8",
					data: data,
					cache: false,
            		processData:false,
					success : function(response){
						console.log("response is '"+response+"'")
						//TODO: check response
						onQueryEnded("success")
						//loadPage("new-order")
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
			})

			//fill page
			holder.html("")
			holder.append(container)
		},
		showRegisterPage = function(holder){
			var container = Templates.getRegistration(),
				form = container.find("#reg-form"),
				submBtn = container.find('[data-action="reg"]')
			
			submBtn.bind("click", function(e){
				//TODO : add validation
				if (submBtn.attr("disabled") !== undefined) return;
				var method = form.attr("method").toLowerCase(),
					url = form.attr("action"),
					data = JSON.stringify(form.serializeObject()),
					onQueryEnded = function(status){
						//enable form
						unlockAllControls(form);
						submBtn.removeClass("active")
						submBtn.removeAttr("disabled")
					}
				//lock form
				lockAllControls(form);
				submBtn.addClass("active")
				submBtn.attr("disabled","")

				method = (method != "get" && method != "post") ? "post" : method
				console.log(data)
				$.ajax({
					type: method,
					url: url,
					contentType: "application/json; charset=utf-8",
					data: data,
					cache: false,
            		processData:false,
					success : function(response){
						console.log("response is '"+response+"'")
						//TODO: check response
						onQueryEnded("success")
						//loadPage("new-order")
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
			var container = Templates.getViewHistory()
			//fill page
			holder.html("")
			holder.append(container)
		}
		showEditAccountPage = function(holder){
			var container = Templates.getEditAccount()
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
				DataTools.setUserLocation("Your location", pos.latitude+', '+pos.longitude, true)
				updateLocationsLists()
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
  