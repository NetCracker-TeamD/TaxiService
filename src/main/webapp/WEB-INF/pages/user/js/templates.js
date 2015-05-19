var Templates = (function () {
    var
    lockAllControls = function(holder){
        holder.find(":input").prop({'readonly': true, 'disabled': true});
    },
    unlockAllControls = function(holder){
        holder.find(":input").prop({'readonly': false, 'disabled': false});
    },
    getHeaderContainer = function () {
        var container = $('<nav class="navbar navbar-inverse navbar-fixed-top">\
				<div class="container">\
					<div class="navbar-header">\
						<button type="button" class="navbar-toggle collapsed" data-toggle="collapse"\
							data-target="#navbar" aria-expanded="false" aria-controls="navbar">\
							<span class="sr-only">Toggle navigation</span>\
							<span class="icon-bar"></span>\
							<span class="icon-bar"></span>\
							<span class="icon-bar"></span>\
						</button>\
						<a class="navbar-brand" href="#">TaxiService</a>\
					</div>\
					<div id="navbar" class="collapse navbar-collapse">\
						<ul class="nav navbar-nav" data-type="menu-list">\
						</ul>\
                        <ul class="nav navbar-nav navbar-right" data-type="action-list">\
                        </ul>\
					</div>\
				</div>\
			</nav>')
        return container
    },
    /*
        info = {
            label : "Item text",
            url : "some/link/to/other/page.html",
            action : "some action stored in 'data-action' attribute",
            icon : "icon-name"//bootsrap glyph-icon,
            right : true //place icon in right of label
        }
        if url == null, then url = "/"+action
    */
    getHeaderItem = function (info) {
        var label = info.label,
            url = info.url,
            action = info.action,
            icon = info.icon,
            right = info.right
        var str = '<li><a'
        if (!$.isSet(url) && $.isSet(action)) {
            url = '/'+action
        }
        str += ($.isSet(url) ? ' href="'+url+'"' : '')
        str += ($.isSet(action) ? ' data-action="'+action+'"' : '')
        str += '>'
        if ($.isSet(icon)){
            var icon_html = '<span class="glyphicon glyphicon-'+icon+'"></span>'
            str += (right ? label+'&nbsp;'+icon_html : icon_html+'&nbsp;'+label)
        } else {
            str += label
        }
        str +='</a></li>' 
        var container = $(str)
        return container
    },
    getContentContainer = function () {
        var container = $('<div class="container content"></div>')
        return container
    },
    getLoader = function (text) {
        var container = $('<div id="loader">\
			<div>\
		        <div class="anim">\
					<div class="rect1"></div>\
					<div class="rect2"></div>\
					<div class="rect3"></div>\
					<div class="rect4"></div>\
					<div class="rect5"></div>\
		        </div>\
		        <p class="text">' +($.isSet(text) ? text : "")+ '</p>\
		    </div>\
  		</div>')
        return container
    },
    getWhiteLoader = function (text) {
        var container = getLoader()
        container.addClass("white")
        return container
    },
    /*
        info = {
            form : $("formSelector"),
            button : $("buttonSelector"),
            success : function(),//callback called after response with success status,
            error : function(),//callback called after response with error status,
            validator : function(),//validate data befor sending, if return not true cansels request
            right : true //place spinner in right
        }
    */
    makeNiceSubmitButton = function(info){
        var form = info.form,
            submitBtn = info.button,
            onSuccess = info.success,
            onError = info.error,
            validator = info.validator,
            right = info.right
        if (!submitBtn.hasClass("has-spinner")){
            submitBtn.addClass("has-spinner")
            var spinnerHtml = '<span class="spinner"><i class="glyphicon glyphicon-refresh glyphicon-spin"></i></span>'
            if (right) {
                submitBtn.append('&nbsp;')
                submitBtn.append(spinnerHtml)
            } else {
                submitBtn.prepend('&nbsp;')
                submitBtn.prepend(spinnerHtml)
            }
        }
        submitBtn.bind("click", function(e){
            e.preventDefault()

            if ($.isSet(validator)){
                if (!validator()) {
                    return;
                }
            }

            if ($.isSet(submitBtn.attr("disabled"))) return;
            var method = form.attr("method").toLowerCase(),
                url = form.attr("action"),
                data = JSON.stringify(form.serializeObject()),
                onQueryEnded = function(){
                    //enable form
                    unlockAllControls(form)
                    submitBtn.removeClass("active")
                    submitBtn.removeAttr("disabled")
                }
            method = (method != "get" && method != "post") ? "post" : method
            //lock form
            lockAllControls(form);
            submitBtn.addClass("active")
            submitBtn.attr("disabled","")
            
            console.log(data)
            $.ajax({
                type: method,
                url: url,
                contentType: "application/json; charset=utf-8",
                data: data,
                cache: false,
                processData:false,
                success : function(response){ 
                    onQueryEnded(); 
                    if ($.isSet(onSuccess)) { onSuccess(response) }
                },
                error : function(response){ 
                    onQueryEnded(); 
                    if ($.isSet(onError)) { onError(response)  }
                }
            })
        })
    },
    getOrderPage = function (orderInfo) {//button
                var container = $('<div class="col-sm-5 col-sm-offset-1">\
                    <form class="form-horizontal" id="orderForm" method="POST" action="/test/wait/3000">\
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
            form.prepend('<input type="hidden" name="orderId" value="'+orderInfo.orderId+'">')
            if ($.isSet(orderInfo.secret)) {
                form.prepend('<input type="hidden" name="secret" value="'+orderInfo.secret+'">')
            }
            var statusBlock = $("<div></div>")
            label.text("Order review")
            switch (status.toLowerCase()){
                case "queued" : 
                    statusBlock.append('<button type="button" class="btn btn-primary">Queued</button>')
                    buttonsHolder.append('<button type="button" class="btn btn-primary btn-lg" data-action="edit">Edit</button>')
                    buttonsHolder.append('<span>&nbsp;</span>')
                    buttonsHolder.append('<button type="button" class="btn btn-danger btn-lg" data-action="cancel">Cancel order</button>')
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
    },
    getDropDownAddressHTML = function (items, isRemovable) {
        var str = '<div data-type="dropdown-address-list" class="input-group-btn">\
				<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false"><span class="caret"></span></button>\
				<ul class="dropdown-menu dropdown-menu-right" role="menu">'
        if ($.isSet(items) && items.length > 0) {
            $.each(items, function (i, item) {
                str += '<li><a>' + item.name + ' <small>' + item.address + '</small></a></li>'
            }) 
        } else {
            str += '<li data-action="none"><small>List is empty</small></li>'
        }
        str += '</ul>'
        if (isRemovable == true) {
            str += '<button class="btn btn-danger" type="button" data-action="remove">\
				<span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span>\
				</button>'
        }
        str += '</div>'
        return str
    },
    getAddressesContainer = function () {
        return $('<div class="form-group" id="addressesContainer">\
				<label>Enter addresses</label>\
				</div>')
    },
    getAddressesGroup = function (label, name, hasAddButton, hasCarsAmount, baseNumber) {
        baseNumber = parseInt(baseNumber)
        if (isNaN(baseNumber)) {
            baseNumber = 0;
        }
        var addButton = '<button'
        addButton += ' data-start-number="' + baseNumber + '"'
        if (hasCarsAmount) {
            addButton += ' data-mod="addCarsAmount"'
        }
        addButton += ' data-name="' + name + '"'
        addButton += ' data-action="add" type="button" class="btn btn-sm btn-success pull-right">Add</button>'
        return $('<div>\
					<label>' + label + '</label>\
					<div data-type="address-group"></div>' +
        ((hasAddButton) ? addButton : "") +
        '<div class="clearfix"></div>\
    </div>')
    },
    getAddress = (function () {
        var uniqNumber = 1
        return function (locationsList, name, hasCarsAmount, isRemovable, baseNumber) {
            baseNumber = parseInt(baseNumber)
            if (isNaN(baseNumber)) {
                baseNumber = 0;
            }
            uniqNumber++
            var container = $('<div class="input-group"></div>'),
                input = $('<input data-number="' + (baseNumber + uniqNumber) + '" name="' 
                    + name + '" data-type="address" type="text" class="form-control">')
            container.append(input)
            container.append(getDropDownAddressHTML(locationsList, isRemovable))
            if (hasCarsAmount) {
                //TODO: style spinner input, now it looks very bad
                container.append(getSpinerInput("cars", 1))
            }
            return container;
        }
    })(),
    getFavAddress = (function() {
        var uniqNumber = 1
        return function (locationsList, inputNamePrefix, baseNumber) {
            baseNumber = parseInt(baseNumber)
            if (isNaN(baseNumber)) {
                baseNumber = 0;
            }
            uniqNumber++
            var container = $('<div class="input-group fav-address"></div>'),
                nameInput = $('<div class="input-group"><input data-number="' + (baseNumber + uniqNumber) + '" name="' 
                    + inputNamePrefix + '_name" data-type="address-name" type="text" class="form-control" placeholder="Enter short name"></div>'),
                addressInput = $('<div class="input-group"><input data-number="' + (baseNumber + uniqNumber) + '" name="'
                    + inputNamePrefix + '_address" data-type="address" type="text" class="form-control"></div>'),
                removeBtn = $('<div class="input-group-btn"><button class="btn btn-danger" type="button" data-action="remove">\
                    <span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span>\
                </button></div>')
            addressInput.append(getDropDownAddressHTML(locationsList, false))
            nameInput.append(removeBtn)
            container.append(nameInput)
            container.append(addressInput)
            
            
            return container;
        }
    })()
    getDateTimePicker = function (name, value, config) {
        var picker = $('<div class="input-group date">\
				<input type="text" class="form-control" name="' + name + '" value="' + value + '" />\
				<span class="input-group-addon">\
					<span class="glyphicon glyphicon-calendar"></span>\
				</span>\
			</div>')
        picker.datetimepicker(config)
        return picker
    },
    getTime = function (isNow, isCustom) {
        console.log(isNow, isCustom)
        var container = $('<div class="form-group"><label>Enter time</label></div>')
        if (isNow == isCustom && isNow == true) {
            container.append('<div class="radio">\
				<label class="radio-inline control-label"><input type="radio" name="time" value="now">Now</label>\
				</div>')
            var pickerWrap = $('<div class="radio form-inline">\
					<label class="radio-inline control-label">\
					<input type="radio" name="time" value="specified">Later\
					</label>\
					</div>'),
                picker = getDateTimePicker("time_specified", "", {
                    locale: 'en',
                    minDate: new Date()
                })
            pickerWrap.append(picker)
            container.append(pickerWrap)
            container.bind("click", function (e) {
                var target = $(e.target),
                    tagName = target[0].tagName.toLowerCase()
                if (tagName == "input") {
                    if (target.attr("name") == "time_specified") {
                        var radio = target.closest("div.radio").find('input[type="radio"]')
                        radio.prop("checked", true)
                    } else if (target.attr("name") == "time" && target.val() == "specified") {
                        var radio = target.closest("div.radio").find('input[type="text"]').focus()
                        radio.prop("checked", true)
                    }
                } else {
                    var closestDiv = target.closest('div')
                    if (closestDiv.hasClass("date")) {
                        var radio = target.closest("div.radio").find('input[type="radio"]')
                        radio.prop("checked", true)
                    }
                }
            })
        } else if (isCustom) {
            container.append(getDateTimePicker("time_specified", "", {
                locale: 'en',
                minDate: new Date()
            }))
        } else {
            container.attr("class", "") //remove spaceing
            container.html('<input type="hidden" name="time" value="now">')
        }
        return container
    },
    getFeaturesGroup = function (feature, inputName) {
        var items = feature.items
        if (items.length < 1) {
            return "";
        }
        var container = $('<div class="input-group"><label>' + feature.categoryName + '</label><br></div')

        if (feature.featureSpecialName != null && feature.featureSpecialName != undefined) {
            inputName = feature.featureSpecialName
        }
        var type = "radio"
        if (feature.multiSelect) {
            type = "checkbox"
        }
        for (var i in items) {
            var item = items[i]
            container.append($('<label class="radio-inline">\
				<input type="' + type + '" name="' + inputName + '" value="' + item.id + '"' + ((item.checked) ? "checked" : "") + '>' + item.name +
            '</label>'))
        }
        container.append('<div class="clearfix"></div>')
        if (feature.deselectable) {
            var deselectBtn = $('<button type="button" class="btn btn-default pull-right">Deselect</button>')
            deselectBtn.bind("click", function (e) {
                container.find("input").prop('checked', false)
            })
            container.append(deselectBtn)
        }
        return container
    },
    getFeaturesItem = function (item, inputName) {
        return $('<div class="checkbox">\
			<label>\
			<input type="checkbox" name="' + inputName + '"\
			value="' + item.id + '">' + item.name + '</label>\
			</div>')
    },
    getFeaturesContainer = function () {
        return $('<div class="form-group">\
			<label>Pick additional options</label>\
			</div>')
    },
    getSpinerInput = function (inputName, minValue) {
        var container = $('<div class="input-group">\
				<input type="text" name="' + inputName + '" class="form-control" data-type="cars-amount">\
				<div class="input-group-btn">\
				<button type="button" class="btn btn-default" data-action="inc"><span class="glyphicon glyphicon-chevron-up"></span></button>\
				<button type="button" class="btn btn-default" data-action="dec"><span class="glyphicon glyphicon-chevron-down"></span></button>\
				</div>'),
            input = container.find('[data-type="cars-amount"]'),
            incBtn = container.find('[data-action="inc"]'),
            decBtn = container.find('[data-action="dec"]')
        input.numeric({decimal: false, negative: false})
        input.val(minValue)
        //coz jquery.numeric doesn`t support min/max
        var min = minValue,
            max = 99
        filter = function (e) {
            if (input.val() < min) {
                input.val(min)
            } else if (input.val() > max) {
                input.val(max)
            }
        }
        input.bind("keyup", filter)
        input.bind("change", filter)
        incBtn.bind("click", function (e) {
            var oldVal = parseInt(input.val())
            if (!oldVal.isNan) {
                input.val(oldVal + 1)
            } else {
                input.val(99)
            }
            filter()
        })
        decBtn.bind("click", function (e) {
            var oldVal = parseInt(input.val())
            if (!oldVal.isNan) {
                input.val(oldVal - 1)
            } else {
                input.val(1)
            }
            filter()
        })
        return container
    },
    getCarsAmount = function (minValue) {
        if (minValue === null || minValue === undefined || parseInt(minValue).isNaN) {
            minValue = 1
        }
        var container = $('<div class="form-group">\
					<label>Enter cars amount</label></div>')
        container.append(getSpinerInput("cars_amount", minValue))
        return container
    },
    getContacts = function () {
        var container = $('<div class="form-group" id="contacts">\
				<label>Provide contact information</label>\
                <div class="input-group">\
                    <span class="input-group-addon glyphicon glyphicon-user"></span>\
                    <input type="text" class="form-control" name="user_name" data-type="user_name" placeholder="Enter your name">\
                </div>\
				<div class="input-group">\
					<span class="input-group-addon glyphicon glyphicon-phone"></span>\
					<input type="phone" class="form-control" name="phone" id="phone" data-type="phone" placeholder="Enter your phone number">\
				</div>\
				<div class="input-group">\
					<span class="input-group-addon glyphicon glyphicon-envelope"></span>\
					<input type="email" name="email" class="form-control" id="email" placeholder="Enter your email">\
				</div>\
			</div>')
        container.find('[data-type="phone"]').mask("(999) 999-9999")

        return container
    },
    getBlocked = function(){
        var container = $('<div class="row">\
            <div class="col-md-12">\
                <div class="error-template">\
                    <h1>Sorry, our account is blocked!</h1>\
                </div>\
            </div>\
        </div>')
        /*
        <div class="error-actions">
                    <a href="http://www.jquery2dotnet.com" class="btn btn-primary btn-lg"><span class="glyphicon glyphicon-home"></span>
                        Take Me Home </a><a href="http://www.jquery2dotnet.com" class="btn btn-default btn-lg"><span class="glyphicon glyphicon-envelope"></span> Contact Support </a>
                </div>
        */
        return container
    },
    getLogin = function(){
        var container = $('<div><form id="login-form" class="form-signin" method="post" action="/test/wait/3000">\
        <div class="form-group">\
            <h2 class="form-signin-heading">Please sign in</h2>\
            <div class="input-group">\
                <span class="input-group-addon glyphicon glyphicon-envelope"></span>\
                <input type="email" class="form-control" name="email" data-type="email" placeholder="Enter your email">\
            </div>\
            <div class="input-group">\
                <span class="input-group-addon glyphicon glyphicon-lock"></span>\
                <input type="password" class="form-control" name="password" data-type="password" placeholder="Enter your password">\
            </div>\
            <button class="btn btn-lg btn-primary btn-block" data-action="login" type="submit">Sign in</button>\
        </div></form></div>')
        /*
        <div class="checkbox">
          <label>
            <input type="checkbox" value="remember-me"> Remember me
          </label>
        </div>*/
        return container
    },
    getRegistration = function(){
        var form = $('<form id="reg-form" class="form-signin" method="post" action="/test/wait/3000">\
            <div class="form-group"><h2 class="form-signin-heading">Please sign up</h2></div></form>');
        form.append(getContacts())
        form.append($('<div class="form-group">\
            <label>Provide password</label>\
            <div class="input-group">\
                <span class="input-group-addon glyphicon glyphicon-lock"></span>\
                <input type="password" class="form-control" name="password" data-type="password" placeholder="Enter your password">\
            </div>\
            <div class="input-group">\
                <span class="input-group-addon glyphicon glyphicon-lock"></span>\
                <input type="password" class="form-control" name="password_repeat" data-type="password2" placeholder="Repeat password">\
            </div>\
        </div>'))
        form.append($('<button class="btn btn-lg btn-primary btn-block" data-action="reg" type="submit">Sign up</button>'))
        var container = $('<div></div>')
        container.append(form)
        return container
    },
    getAbout = function(){
        var container = $('<div class="row">\
            <div class="col-md-12 text-center">\
                    <h1>HERE IS ABOUT!</h1>\
            </div>\
        </div>')
        return container
    },
    getEditAccount = function(userFavLocations){
        var container = $('<div></div>')

        var formContacts = $('<form id="contacts" class="form-signin" method="post" action="/test/wait/3000">\
            <div class="form-group"><h2 class="form-signin-heading">Change contacts</h2></div></form>');
        formContacts.find('div').append(getContacts())
        formContacts.append($('<button class="btn btn-primary pull-right" data-action="save"\
                data-form-id="contacts" type="submit">Save</button>'))
        container.append(formContacts)

        var locInputName = "fav_locations",
            groupAddresses = getAddressesGroup("Your favourite locations :", locInputName, true, false, 1),
            addressesContainer = groupAddresses.find('[data-type="address-group"]'),
            formAddresses = $('<form id="addresses" class="form-signin" method="post" action="/test/wait/3000">\
                <div class="form-group"><h2 class="form-signin-heading">Change locations</h2></div></form>')
        for (var key in userFavLocations){
            var location = userFavLocations[key]
            if (!location.isUserLocation){
                var addressBlock = getFavAddress(userFavLocations, locInputName, false, true, 1),
                    address = addressBlock.find('[data-type="address"]'),
                    name = addressBlock.find('[data-type="address-name"]')
                address.val(location.address)
                name.val(location.name)
                addressesContainer.append(addressBlock)
            }
        }
        console.log(groupAddresses)
        formAddresses.find('div').append(groupAddresses)
        formAddresses.append($('<button class="btn btn-primary pull-right" data-action="save"\
                data-form-id="addresses" type="submit">Save</button>'))
        
        container.append(formAddresses)
        return container
    },
    getViewOrder = function(){
        var container = $('<div>Edit getViewOrder</div>')
        return container
    },
    getViewHistory = function(history){
        console.log(history)
        var container = $('<div><h2>Your history</h2></div>')
        container.append('<h3 class="pull-right">page '+(parseInt(history.pageNumber)+1)+"/"+history.pagesAmount+'</h3>')
        container.append('<div class="clearfix"></div>')

        var table = $('<table class="table"></table>'),
            table_wrap = $('<div class="table-responsive"></div>')
        container.append(table_wrap)
        table_wrap.append(table)
        var header = $('<thead class="cf"><tr></tr></thead>')
        header.append('<th>Service type</th>')
        header.append('<th>Registration datetime</th>')
        header.append('<th>Execution datetime</th>')
        header.append('<th>Payment type</th>')
        header.append('<th>Options</th>')
        header.append('<th>Locations</th>')
        header.append('<th>Cars</th>')
        header.append('<th>Distance</th>')
        header.append('<th>Price</th>')
        table.append(header)
        if ($.isSet(history.oreders)){
            for (var i=0; i<history.orders.length; i++){
                var order = history.orders[i],
                    height = order.assembledRoutes.length
                    for (var j=0; j<height; j++){
                        var row = $('<tr></tr>'),
                            route = order.assembledRoutes[j]
                        if (j==0){
                            row.append('<td colspan="'+height+'">'+order.order.serviceType.name+'</td>')
                            row.append('<td colspan="'+height+'">'+order.order.registrationDate+'</td>')
                            row.append('<td colspan="'+height+'">'+order.order.executionDate+'</td>')
                            row.append('<td colspan="'+height+'">'+order.order.paymentType+'</td>')
                            row.append('<td colspan="'+height+'">OPTIONS</td>')
                        }
                        row.append('<td>'+route.sourceAddress+" -> "+route.destinationAddress+'</td>')
                        row.append('<td>'+route.totalCars+"/"+route.finishedCars+'</td>')
                        row.append('<td>'+route.totalDistance+'</td>')
                        row.append('<td>'+route.totalPrice+'</td>')
                        table.append(row)
                    }

                //table
                /*header.append('<th>Service type</th>')
                header.append('<th>Registration datetime</th>')
                header.append('<th>Execution datetime</th>')
                header.append('<th>Payment type</th>')
                header.append('<th>Locations</th>')
                header.append('<th>Options</th>')
                header.append('<th>Comment</th>')
                */
            }
        }
        return container
    }

    return public_interface = {
        "getDropDownAddressHTML": getDropDownAddressHTML,
        "getAddressesContainer": getAddressesContainer,
        "getAddressesGroup": getAddressesGroup,
        "getAddress": getAddress,
        "getContacts": getContacts,
        "getCarsAmount": getCarsAmount,
        "getTime": getTime,
        "getFeaturesContainer": getFeaturesContainer,
        "getFeaturesItem": getFeaturesItem,
        "getFeaturesGroup": getFeaturesGroup,
        "getHeaderContainer": getHeaderContainer,
        "getHeaderItem": getHeaderItem,
        "getContentContainer": getContentContainer,
        "getOrderPage": getOrderPage,
        "getLoader": getLoader,
        "getWhiteLoader": getWhiteLoader,
        "getBlocked": getBlocked,
        "getLogin" : getLogin,
        "getRegistration" : getRegistration,
        "getAbout" : getAbout,
        "getEditAccount" : getEditAccount,
        "getViewOrder" : getViewOrder,
        "getViewHistory" : getViewHistory,
        "lockAllControls" : lockAllControls,
        "unlockAllControls" : unlockAllControls,
        "makeNiceSubmitButton" : makeNiceSubmitButton,
        "getFavAddress" : getFavAddress
    }
})()