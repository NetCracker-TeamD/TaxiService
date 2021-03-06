/**
 * Created by anton on 5/25/15.
 */

$(document).ready(function() {

    var updateRoutes = function(status, invoker, place) {
        var invoker = $(invoker)
        console.log('updated')
        var triggerValidation = function () {
            invoker.parent().validator('validate')
        }

        var id = invoker.attr("data-number")
        if (status != 'success') {
            console.log('bad location')
            //MapTools.removeMarker(id)
            invoker.removeAttr('valid')
            triggerValidation()
            return;
        }
        var location = place.geometry.location,
            type = invoker.attr('name').split('_')[0]
        invoker.val(place.formatted_address)
        console.log(location, type)
        invoker.attr('valid', '')
        triggerValidation()
        //MapTools.addMarker(id, location, type)
        //MapTools.markersFitWindow()
    }

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
            var addressBlock = Templates.getFavAddress(fav_locations, locInputName, false, true, 1),
                address = addressBlock.find('[data-type="address"]'),
                name = addressBlock.find('[data-type="address-name"]')
            address.val(location.address)
            name.val(location.name)
            name.attr("data-id", location.id)

            addressesContainer.append(addressBlock)
        }
    }
    //console.log(groupAddresses)
    var addresses = addressesContainer.find('input[data-type="address"]')
    addresses.each(function (i, input) {
        MapTools.modAutocompleteAddressInput(input, updateRoutes)
        //$(input).trigger('change')
    })
    formAddresses.find('div').append(groupAddresses)
    formAddresses.append($('<button class="btn btn-primary pull-right" data-action="save"\
                data-form-id="addresses" type="submit">Save</button>'))
    formAddresses.validator()
    formContacts = $('#contacts')
    formContacts.find('[name="firstName"]').val(userInfo.firstName)
    formContacts.find('[name="lastName"]').val(userInfo.lastName)
    formContacts.find('[name="phoneNumber"]').val(userInfo.phoneNumber)
    formContacts.find('[name="phoneNumber"]').mask("(999) 999-9999")
    formContacts.find('[name="email"]').val(userInfo.email)
    formContacts.validator()

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
                //MapTools.removeMarker(markerId)
                //MapTools.markersFitWindow()
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
                MapTools.modAutocompleteAddressInput(input, updateRoutes)
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
                validator : function(){
                    var from = container.find('#'+formId)
                    from.validator('validate');
                    return !(from.has('.has-error').length>0)
                },
                success : function(response){
                    //contact info saved
                    console.log(response)
                    if ($.isSet(response.errors)){
                        for (var key in response.errors){
                            var error = response.errors[key],
                                input = formContacts.find('[name="'+key+'"]')
                            if (input.length>0){
                                var holder = input.closest('.form-group').addClass('has-error')
                                holder.find('.help-block.with-errors')
                                    .html('<ul class="list-unstyled"><li>'+error+'</li></ul>')
                            }
                        }
                    } else {

                    }
                },
                error : function(response){
                    console.log(response)
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
        MapTools.getNameForLocation(pos.latitude, pos.longitude, function(status, address){
            if (status == 'success') {
                fav_locations.unshift({
                    name: "Your location",
                    address: address,
                    isUserLocation: true
                })
                updateLocationsLists()
            }
        })
    })
    MapTools.init()
    Templates.unlockAllControls(saveBtns)
})