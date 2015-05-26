/**
 * Created by anton on 5/25/15.
 */

$(document).ready(function() {
    var locations = []
    var fav_locations = userAddresses
    for (var i=0;i<fav_locations.length;i++){
        var location = fav_locations[i]
        if ($.isSet(location) && !location.isUserLocation) {
            locations.push({
                name : location.name,
                address : location.address,
                id : location.id,
                action : "none"
            })
        }
    }

    var locInputName = "fav_locations",
        groupAddresses = getAddressesGroup("Your favourite locations :", locInputName, true, false, 1),
        addressesContainer = groupAddresses.find('[data-type="address-group"]'),
        formAddresses = $('#addresses')
    for (var key in fav_locations) {
        var location = fav_locations[key]
        if (!location.isUserLocation) {
            var addressBlock = getFavAddress(fav_locations, locInputName, false, true, 1),
                address = addressBlock.find('[data-type="address"]'),
                name = addressBlock.find('[data-type="address-name"]')
            address.val(location.address)
            name.val(location.name)
            name.attr("data-id", location.id)
            addressesContainer.append(addressBlock)
        }
    }
    //console.log(groupAddresses)
    formAddresses.find('div').append(groupAddresses)
    formAddresses.append($('<button class="btn btn-primary pull-right" data-action="save"\
                data-form-id="addresses" type="submit">Save</button>'))

    var favAddressesClick = function(e){
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
                    newAddressBlock = Templates.getFavAddress(fav_locations, name, startNumber),
                    input = newAddressBlock.find('[data-type="address"]')[0]
                var holder = $(target.parent()).find('[data-type="address-group"]')
                //console.log(holder)
                holder.append(newAddressBlock)
                MapTools.modAutocompleteAddressInput(input, function(){}/*updateRoutes*/)
                return;
            }
        }
    }

    var container = $('.container.content'),
        saveBtns = container.find('[data-action="save"]')
    formAddresses.bind('click',favAddressesClick)

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
                    updateLocationsLists()
                    //loadPage("new-order")
                },
                error : function(response){
                    console.log("error")
                    console.log(response)
                    BootstrapDialog.show({
                        type: BootstrapDialog.TYPE_DANGER,
                        title: "Server error",
                        message: "Server returns error with status '"+response.statusText+"'"
                    })
                }
            })
        } else {
            Templates.makeNiceSubmitButton({
                form : container.find('#'+formId),
                button : btn,
                success : function(response){
                    //contact info saved
                },
                error : function(response){
                    BootstrapDialog.show({
                        type: BootstrapDialog.TYPE_DANGER,
                        title: "Server error",
                        message: "Server returns error with status '"+response.statusText+"'"
                    })
                }
            })
        }
    })

    var updateLocationsLists = function(){
        var FL = fav_locations
        if (FL.length>0){
            $.each(container.find('[data-type="address-group"] .input-group'),function(i,group){
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

    MapTools.addListener("onGeolocationAllowed",function(pos){
        MapTools.getNameForLocation(pos.latitude, pos.longitude, function(address){
            DataTools.setUserLocation("Your location", address, true)
            fav_locations.unshift({
                name : "Your location",
                address : address,
                isUserLocation : true
            })
            updateLocationsLists()
        })
    })
    MapTools.init()
    Templates.unlockAllControls(saveBtns)
})