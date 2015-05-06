var App = (function(){
	var 
		controls = {},
		updateLocationsLists = function(){
			var locationsList = DataTools.getFavLocations()
			if (locationsList.length>0){
				$.each(controls.orderDetails.find('[data-type="address-group"] .input-group'),function(i,group){
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
		createInputsForServiceType = function(holder, serviceDescription, featuresList) {
			var SD = serviceDescription
			//create DOM
			holder.html("")
			//add contacts
			holder.append(Templates.getContacts())
			holder.append(Templates.getTime(SD.timing.indexOf("now")>-1, SD.timing.indexOf("specify")>-1))
			var locationsList = DataTools.getFavLocations()
			//add addresses
			var addresses = $(Templates.getAddressesContainer())
			var addrGroup = $(Templates.getAddressesGroup("Source:", SD.multipleSourceLocations))
			addrGroup.find('[data-type="address-group"]').append(Templates.getAddress(locationsList,false))
			addresses.append(addrGroup)

			if (SD.chain) {
				addrGroup = $(Templates.getAddressesGroup("Intermediate:", true))
				addresses.append(addrGroup)
			}

			if (SD.desinationRequired) {
				addrGroup = $(Templates.getAddressesGroup("Destination:", SD.multipleDestinationLocations))
				addrGroup.find('[data-type="address-group"]').append(Templates.getAddress(locationsList,false))
				addresses.append(addrGroup)
			}

			holder.append(addresses)
			//add cars number input
			if (SD.specifyCarsNumbers) {
				holder.append(Templates.getCarsAmount())
			}

			var features = Templates.getFeaturesContainer()
			features.append()
			for (var i in featuresList) {
				feature = featuresList[i]
				if (typeof feature === "object"){
					features.append(Templates.getFeaturesGroup(feature.categoryName, feature.inputName, feature.items))
				} else {
					features.append(Templates.getFeaturesItem(feature,"features[]"))
				}
			}
			holder.append(features)

			

			//bind events
			addresses.bind("click", function(e){
				var target = $(e.target),
					tagName = target[0].tagName.toLowerCase()
				//select fav location part
				if (tagName == "a" || tagName == "small"){
					var input = target.closest('.input-group').find('input')
					var text = target.html();
					if (tagName == "a") {
						text = target.find("small").html()
					}
					input.val(text)
					return;
				} 
				//remove and add address part
				if (tagName == "button" || tagName == "span"){
					var btn = target
					if (tagName == "span"){
						btn = target.closest("button")
					}
					if (btn.attr('data-action')=="remove"){
						target.closest('.input-group').remove()
						return;
					} else if (btn.attr('data-action')=="add"){
						$(target.parent()).find('[data-type="address-group"]').append(Templates.getAddress(DataTools.getFavLocations(),true))
						return;
					}
				}
			})
		},
		initControls = function(){
			var serviceTypes = DataTools.getServiceTypes()
			//fill service types
			controls.serviceTypesList = $("#serviceTypes")
			$.each(serviceTypes, function (i, item) {
			    controls.serviceTypesList.append($('<option>', { 
			        value: item,
			        text : item 
			    }))
			})
			controls.orderDetails = $("#orderDetails")
			controls.serviceTypesList.bind("change", function(e){
				var newServiceType = $(e.target).val()
				createInputsForServiceType(controls.orderDetails, 
					DataTools.getServiceDescription(newServiceType), 
					DataTools.getFeatureList(newServiceType))
			})
			var newServiceType = controls.serviceTypesList.val()
			createInputsForServiceType(controls.orderDetails, 
				DataTools.getServiceDescription(newServiceType), 
				DataTools.getFeatureList(newServiceType))

			
			updateLocationsLists()
		},
		
		init = function(){
			MapTools.addListener("onGeolocationAllowed",function(pos){
				DataTools.setUserLocation("Your location", pos.latitude+','+pos.longitude, true)
				updateLocationsLists()
			})
			MapTools.init()
			DataTools.init()
			initControls()
			MapTools.putMarkers()
		}

	return public_interface = {
		"init" : init,
	}
})()


$(document).ready(function(){
	App.init()
})
  