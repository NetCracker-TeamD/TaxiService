/**
 * Created by anton on 5/25/15.
 */
var getOrderPage = function (orderInfo, isUserLogged) {
    console.log(orderInfo)
    var container = $('<div class="col-sm-6">\
                    <form class="form-horizontal" id="orderForm" method="POST" action="/makeOrder">\
                        <div class="form-group">\
                            <label for="serviceType">Choose service type</label>\
                            <select id="serviceType" name="serviceType" class="form-control">\
                            </select>\
                        </div>\
                        <div id="orderDetails" data-type="order-details">\
                        </div>\
                    </form>\
                </div>\
                <div class="col-sm-6 map">\
                    <div class="google-map-canvas" data-type="map"></div>\
                </div>\
                <div class="clearfix"></div>\
                <div class="row text-center">\
                    <div class="col-sm-6 center">\
                        <h4 data-type="price-holder">The approximate cost of the trip is : <span data-type="price-value"></span>$</h4>\
                        <div data-type="button-holder">\
                        </div>\
                    </div>\
                </div>'),
        label = $("<h2></h2>"),
        buttonsHolder = container.find('[data-type="button-holder"]'),
        form = container.find("form")

    var status = null
    if ($.isSet(orderInfo)) {
        status = orderInfo.status
    }

    $(container.find('div')[0]).prepend(label)
    if (!$.isSet(status)) {
        label.text("Create order")
        buttonsHolder.append('<button type="button" class="btn btn-success btn-lg" data-action="make-order">Make Order</button>')
    } else {
        form.prepend('<input type="hidden" name="orderId" value="' + orderInfo.orderId + '">')
        if ($.isSet(orderInfo.secret)) {
            form.prepend('<input type="hidden" name="secret" value="' + orderInfo.secret + '">')
        }
        //var statusBlock = $("<div>Status : </div>")
        label.text("Order review")
        statusBlock = label
        switch (status.toLowerCase()) {
            case "queued" :
                statusBlock.append('<button type="button" class="btn btn-primary">Queued</button>')
                if (isUserLogged){
                    buttonsHolder.append('<button type="button" class="btn btn-primary btn-lg" data-action="edit">Edit</button>')
                    buttonsHolder.append('<span>&nbsp;</span>')
                    buttonsHolder.append('<button type="button" class="btn btn-warning btn-lg" data-action="cancel">Cancel order</button>')
                } else {
                    buttonsHolder.append('<h2 class="small"> Unlogged users cant change or cancel their order</h2>')
                }
                //console.log(statusBlock)
                //console.log(buttonsHolder)
                break;
            case "updating" :
                statusBlock.append('<button type="button" class="btn btn-info">Updating</button>')
                label.text("Order editing")
                buttonsHolder.append('<button type="button" class="btn btn-primary btn-lg" data-action="save">Save changes</button>')
                buttonsHolder.append('<span>&nbsp;</span>')
                buttonsHolder.append('<button type="button" class="btn btn-info btn-lg" data-action="cancel-changes">Cancel changes</button>')
                break;
            case "assigned" :
                statusBlock.append('<button type="button" class="btn btn-success">Assigned</button>')
                break;
            case "in progress" :
                statusBlock.append('<button type="button" class="btn btn-success">In progess</button>')
                break;
            case "completed" :
                statusBlock.append('<button type="button" class="btn btn-success">Completed</button>')
                //leave feedback
                break;
            case "canceled" :
                statusBlock.append('<button type="button" class="btn btn-warning">Canceled</button>')
                break;
            case "refused" :
                statusBlock.append('<button type="button" class="btn btn-danger">Refused</button>')
                //leave feedback
                break;
        }
    }
    return container
}

var showOrderPage = function(){
    var locations = []
    var fav_locations = userAddresses


    var addressesClick = function(e){
            console.log('addressClick')
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
                    var container = target.closest('.form-group'),
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
                        newAddressField = Templates.getAddress(fav_locations, name, hasCarsAmount, true, startNumber),
                        input = newAddressField.find('[data-type="address"]')[0]
                    $(target.parent()).find('[data-type="address-group"]').append(newAddressField)
                    MapTools.modAutocompleteAddressInput(input, updateRoutes)
                    return;
                }
            }
        },
        updateRoutes = function(status, invoker, place){
            var invoker = $(invoker)
            console.log('updated')
            var triggerValidation = function(){
                invoker.parent().validator('validate')
            }

            var id = invoker.attr("data-number")
            if (status!='success'){
                console.log('bad location')
                MapTools.removeMarker(id)
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
            MapTools.addMarker(id, location, type)
            MapTools.markersFitWindow()
        },
        updateLocationsLists = function(){
            var FL = fav_locations
            if (FL.length>0){
                $.each(container.find('[data-type="address-group"] .input-group'),function(i,group){
                    var list = $(group).find('[data-type="dropdown-address-list"]'),
                        holder = list.closest(".input-group")
                    isRemovable = list.has('[data-action="remove"]').length > 0
                    if (list.length>0){
                        list.remove()
                        list = Templates.getDropDownAddress(FL, isRemovable)
                        list.bind("click", addressesClick)
                        holder.append(list)
                    }
                })
            }
        },
        formatServices = function(servicesInfo){
            var services = servicesInfo
            services.types = []
            services.descriptions = {}
            services.features = {}
            $.each(servicesInfo.services, function (key, service) {
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
                featuresList.push({ "isCategory": true, "categoryName": "Car type",
                    "featureSpecialName": "car_class", "items": carClasses})
                //payments
                payments.push({ "id": "cash", "name": "Cash", "checked": true })
                payments.push({ "id": "card", "name": "Card" })
                featuresList.push({ "isCategory": true, "categoryName": "Payment type",
                    "featureSpecialName": "payment_type", "items": payments })
                //driver sex
                driverSex.push({ "id": "male", "name": "Male" })
                driverSex.push({ "id": "female", "name": "Female" })
                driverSex.push({ "id": "any", "name": "Any", "checked": true })

                featuresList.push({ "isCategory": true, "categoryName": "Driver`s sex",
                    "featureSpecialName": "driver_sex", "items": driverSex })
                //other options
                featuresList.push({ "isCategory": true, "categoryName": "Other options",
                    "featureSpecialName": "features", "items": otherOptions,
                    "multiSelect": true })
            })

            return services
        },
        formatOrder = function(orderInfo){
            var order = $.extend(true, {}, orderInfo)//cloning object
            order.status = "queued"
            if (order.updating) {
                order.status = "updating"
            } else if (order.complete) {
                order.status = "completed"
            } else {
                var cars = order.carsAmount
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
                    order.status = "in progress"
                } else if (assigned) {
                    order.status = "assigned"
                } else if (completed) {
                    order.status = "completed"
                } else if (refused) {
                    order.status = "refused"
                } else if (canceled) {
                    order.status = "canceled"
                }
            }

            return order
        }

    var services = formatServices(servicesInfo),
        isUserLogged = servicesInfo.userAuthenticated,
        order = null,
        loadOrder = $.isSet(orderInfo)
    console.log(services)
    if (loadOrder) {
        order = formatOrder(orderInfo)
        //orderInfo.orderId = orderId
    }

    var createInputsForServiceType = function(holder, newServiceType, orderInfo) {
        MapTools.clearAllMarkers()
        MapTools.markersFitWindow()
        holder.html("")
        holder.append(Templates.getWhiteLoader)



        var loadOrder = $.isSet(orderInfo)
        //create DOM))
        console.log("CreateDom called")
        var SD = services.descriptions[newServiceType],
            SF = services.features[newServiceType],
            FL = fav_locations
        holder.html("")
        //add contacts
        if (!isUserLogged){
            holder.append(Templates.getContacts())
        }
        //console.log(SD)
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
                var address = Templates.getAddress(FL,"start_addresses", mult, i>1)
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
            addrHolder = addrGroup.find('[data-type="address-group"]')

            if (loadOrder) {
                var ia = orderInfo.intermediateAddresses
                for (var i=0;i<ia.length;i++){
                    var address = Templates.getAddress(FL,"intermediate_addresses", false, true)
                    address.find('[data-type="address"]').val(ia[i])
                    addrHolder.append(address)
                }
            }
            addresses.append(addrGroup)
        }

        if (SD.destinationRequired) {
            var mult = SD.multipleDestinationLocations
            addrGroup = $(Templates.getAddressesGroup("Destination:","destination_addresses", mult, mult))
            addrHolder = addrGroup.find('[data-type="address-group"]')

            if (loadOrder) {
                var da = orderInfo.destinationAddresses
                for (var i=0;i<da.length;i++){
                    var address = Templates.getAddress(FL,"destination_addresses", mult, i>1)
                    address.find('[data-type="address"]').val(da[i])
                    addrHolder.append(address)
                }
            } else {
                addrHolder.append(Templates.getAddress(FL,"destination_addresses", mult, false))
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

        var features = Templates.getFeaturesContainer()
        features.append()
        for (var i in SF) {
            feature = SF[i]
            //console.log(feature)
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

        //TODO:add music style selector
        var musicStyle = ('<div class="form-group">\
            <div class="input-group"><span class="input-group-addon glyphicon glyphicon-music"></span>\
              <input type="text" class="form-control" name="musicStyle" placeholder="Enter music style">\
            </div>\
            <div class="help-block with-errors"></div>\
          </div>')
        holder.append(musicStyle)
        MapTools.clearAllMarkers()
        //console.log(holder.find('input[data-type="address"]'))
        var addresses = holder.find('input[data-type="address"]')
        var m = addresses.length;
            t = 1,
            updateRoutesFix = function(status, invoker, place){
                t++;
                console.log(t+" "+m)
                if (t>m) {
                    MapTools.enableDrawRoutes(true)
                }
                updateRoutes(status, invoker, place)

            }
        if (loadOrder) {
            MapTools.enableDrawRoutes(false)
            addresses.each(function (i, input) {
                MapTools.modAutocompleteAddressInput(input, updateRoutesFix)
                $(input).trigger('change')
            })
        } else {
            addresses.each(function (i, input) {
                MapTools.modAutocompleteAddressInput(input, updateRoutes)
            })
        }
        if (loadOrder && orderInfo.status!="updating"){
            Templates.lockAllControls($(orderDetails))
        }
        //bind events
        $('#addressesContainer').bind("click", addressesClick)
    }

    var
        page = $('.container.content'),
        container = getOrderPage(order, isUserLogged),
        serviceTypes = container.find("#serviceType"),
        orderDetails = container.find('[data-type="order-details"]'),
        map = container.find('[data-type="map"]'),
        orderForm = container.find('#orderForm')
    //bind order status specific actions
    console.log(order)
    if (loadOrder) {
        switch (order.status) {
            //add cancel button event handler
            case "queued":
                var editBtn = container.find('[data-action="edit"]'),
                    cancelBtn = container.find('[data-action="cancel"]')
                //edit
                Templates.makeNiceSubmitButton({
                    form: orderForm,
                    button: editBtn,
                    method : "get",
                    url : "/setUpdating?id="+order.orderId,
                    dataFormater : function(){
                        return ""
                    },
                    success: function(response){
                        console.log(response)
                        if (response.status=="OK"){
                            orderInfo.updating = true
                            showOrderPage()
                        } else {
                            BootstrapDialog.show({
                                type: BootstrapDialog.TYPE_DANGER, closable: true,
                                title: "Server returns bad status",
                                message: "Server returns bad status '"+response.status+"'"
                            })
                            //craeteDOM()
                        }
                    },
                    error : function(response){
                        console.log(response)
                        BootstrapDialog.show({
                            type: BootstrapDialog.TYPE_DANGER, closable: true,
                            title: "Server error",
                            message: "Server returns error with status '"+response.statusText+"'"
                        })
                        //craeteDOM()
                    }
                })
                //cancel
                Templates.makeNiceSubmitButton({
                    form: orderForm,
                    button: cancelBtn,
                    method : "get",
                    url : "/cancelOrder?id="+order.orderId,
                    dataFormater : function(){
                        return ""
                    },
                    success: function(response){
                        console.log(response)
                        if (response.status=="OK"){
                            orderInfo.updating = true
                            BootstrapDialog.show({
                                type: BootstrapDialog.TYPE_INFO, closable: true,
                                title: "Order canceling",
                                message: "Our order successfully canceled",
                                buttons: [{
                                    label : "Create new order",
                                    action: function(dialog){
                                        dialog.close()
                                        window.location = "/order"
                                    }
                                }]
                            })
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
            case "updating":
                var saveBtn = container.find('[data-action="save"]'),
                    cancelBtn = container.find('[data-action="cancel-changes"]')
                //save
                Templates.makeNiceSubmitButton({
                    form: orderForm,
                    button: saveBtn,
                    method : "post",
                    useJSON : true,
                    url : "/updateOrder",
                    success: function(response){
                        console.log(response)
                        if (response.status=="OK"){
                            location.reload()
                        } else {
                            Templates.lockAllControls(serviceTypes)
                            BootstrapDialog.show({
                                type: BootstrapDialog.TYPE_DANGER, closable: true,
                                title: "Server returns bad status",
                                message: "Server returns bad status '"+response.status+"'"
                            })
                            //craeteDOM()
                        }
                    },
                    error : function(response){
                        console.log(response)
                        Templates.lockAllControls(serviceTypes)
                        BootstrapDialog.show({
                            type: BootstrapDialog.TYPE_DANGER, closable: true,
                            title: "Server error",
                            message: "Server returns error with status '"+response.statusText+"'",
                        })
                        //craeteDOM()
                    }
                })
                //cancel
                Templates.makeNiceSubmitButton({
                    form: orderForm,
                    button: cancelBtn,
                    method : "get",
                    url : "/cancelUpdating?id="+order.orderId,
                    dataFormater : function(){
                        return ""
                    },
                    success: function(response){
                        console.log(response)
                        if (response.status=="OK"){
                            location.reload()
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
                            message: "Server returns error with status '"+response.statusText+"'"
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
            validator : function(){
                orderForm.validator('validate');
                return !(orderForm.validator('validate').has('.has-error').length>0)
            },

            success : function(response){
                console.log("response")
                console.log(response)
                var trackLink = response.trackLink

                var watchIt = function(){
                    console.log("go to track link")
                    window.location = trackLink
                }
                BootstrapDialog.show({
                    type: BootstrapDialog.TYPE_SUCCESS, closable: false,
                    title: "Order successfully created",
                    message: function(dialog){
                        return $("<div>Your order successfully created<br>You can track it via this link </div>")
                            .append( $("<a href='"+trackLink+"'>"+trackLink+"</a>")
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
                    }]
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
    $.each(services.types, function (i, item) {
        serviceTypes.append($('<option>', { value: item.id, text : item.name }))
    })

    //fill page
    page.html('')
    page.append(container)

    var calcPrice = function(){
        var data = JSON.stringify(orderForm.serializeObject())
        console.log(data)
        $.ajax({
            url : '/countPrice',
            method : 'POST',
            data : data,
            contentType : "application/json; charset=utf-8",
            success : function(response){
                console.log('success')
                console.log(response)
            },
            error : function(response){
                console.log('error')
                console.log(response)
            }
        })
        //priceValue.html("&lt;price for "+(Math.round(newDistance/100)/10)+" km and selected features&gt;")
        //priceHolder.show()
    }

    if (!loadOrder || (loadOrder && order.status=="updating")) {
        MapTools.enableDraggableMarkers(true)
    } else {
        MapTools.enableDraggableMarkers(false)
    }
    MapTools.removeListenerChain("onMarkerMoved")
    MapTools.removeListenerChain("onPlacePicked")
    MapTools.removeListenerChain("onDistanceChange")
    MapTools.addListener("onMarkerMoved", function(status, marker_id, newLocationName){
        var input =  $('[data-number="' + marker_id + '"')
        input.val(newLocationName)
        console.log(status)
        if (status=='success') {
            input.val(newLocationName).trigger('change')
            //$('[data-number="' + marker_id + '"').val(newLocationName)
            //MapTools.calcAndDrawRoute()
        } else {
            input.removeAttr('valid')
            input.parent().validator('validate')
        }
        MapTools.calcAndDrawRoute()
    })

    MapTools.addListener("onPlacePicked", function(lat, lgn){
        //prevent from piking order
        if (loadOrder && order.status!="updating") {
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
                            MapTools.getNameForLocation(lat, lgn, function(status, locationName){
                                if (status=='success') {
                                    if (action == "add") {
                                        var startNumber = addBtn.attr('data-start-number'),
                                            hasCarsAmount = (addBtn.attr('data-mod') == "addCarsAmount"),
                                            name = addBtn.attr("data-name"),
                                            newAddressField = Templates.getAddress(fav_locations, name, hasCarsAmount, true, startNumber)
                                        emptyInput = newAddressField.find("input")[0]
                                        $(addBtn.parent()).find('[data-type="address-group"]').append(newAddressField)
                                        MapTools.modAutocompleteAddressInput(emptyInput, updateRoutes)
                                        emptyInput = $(emptyInput)
                                    }
                                    emptyInput.val(locationName).trigger('change')
                                    dialog.close()
                                } else {
                                    dialog.close()
                                    BootstrapDialog.show({
                                        type: BootstrapDialog.TYPE_DANGER,
                                        title: 'Address picking',
                                        message: 'Bad location'
                                    });
                                }
                            })
                        })
                        return btn
                    }
                var context = container.find('#addressesContainer'),
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

    MapTools.addListener("onDistanceChanged", function(status, newDistance, markerIds){
        var priceHolder = container.find('[data-type="price-holder"]'),
            priceValue = priceHolder.find('[data-type="price-value"]')
        //console.log(newDistance)
        //priceValue.html("&lt;price for "+(Math.round(newDistance/100)/10)+" km and selected features&gt;")
        //priceHolder.show()
        calcPrice();
        if (status == "error"){
            for (var i = 1; i <markerIds.length; i++) {
                var
                    markerId = markerIds[i],
                    input = container.find('[data-number="'+markerId+'"]')
                input.removeAttr('valid')
                input.parent().validator('validate')
                console.log('valid attr removed')
                //input.trigger('change')
            }
        } else {
            for (var i = 1; i <markerIds.length; i++) {
                var
                    markerId = markerIds[i],
                    input = container.find('[data-number="'+markerId+'"]')
                input.attr('valid','')
                input.parent().validator('validate')
                //input.trigger('change')
            }
        }
    })

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

    //[0] coz new google.maps.Map accepts clear html element, not wraped by jquery
    MapTools.init(map[0])


    if (!loadOrder){
        serviceTypes.bind("change", function(e){
            var newServiceType = $(e.target).find('option[value="'+$(e.target).val()+'"]').text()
            container.find('[data-type="price-holder"]').hide()
            console.log("createInputsForServiceType !loadOrder")
            createInputsForServiceType(orderDetails, newServiceType)
        })
    } else {
        serviceTypes.val(order.serviceType)
        Templates.lockAllControls(serviceTypes)
        console.log(order)
        var newServiceType = serviceTypes.find('option[value="'+order.serviceType+'"]').text()
        createInputsForServiceType(orderDetails, newServiceType, order)
        console.log("createInputsForServiceType loadOrder++++")
        if (order.status!="updating"){
            Templates.lockAllControls(orderDetails)
        }
    }

    serviceTypes.trigger("change")
    $("#orderForm").validator()
    updateLocationsLists()
}

$(document).ready(function(){
    showOrderPage()
})