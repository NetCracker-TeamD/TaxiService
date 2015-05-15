var Templates = (function () {
    var
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
    getHeaderItem = function (label, url, action) {
        var str = '<li><a href="'+url+'"'
        if (action !== undefined && action !== null){
            str += 'data-action="'+action+'"'
        }
        str += '>'+label+'</a></li>' 
        var container = $(str)
        return container
    },
    getHeaderItemIcon = function (label, url, action, icon) {
        var item = getHeaderItem(label, url, action)
        if (icon !== undefined && icon !== null) {
            var link = item.find('a')
            link.prepend('<span class="glyphicon glyphicon-'+icon+'"></span>&nbsp;')
        }
        return item
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
		        <p class="text">' + text + '</p>\
		    </div>\
  		</div>')
        return container
    },
    getOrderPage = function () {
        var container = $('<div class="col-sm-5 col-sm-offset-1">\
					<h2>Create order</h2>\
					<form class="form-horizontal" id="orderForm" method="POST" action="/test/wait/3000">\
						<div class="form-group">\
							<label for="serviceType">Chose service type</label>\
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
				<div>\
					<button type="button" class="btn btn-success btn-lg btn-block has-spinner" data-action="make-order">\
                        <span class="spinner"><i class="glyphicon glyphicon-refresh glyphicon-spin"></i></span>\
                    Make Order</button>\
				</div>')
        return container;
    },
    getDropDownAddressHTML = function (items, isRemovable) {

        var str = '<div class="input-group-btn">\
				<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false"><span class="caret"></span></button>\
				<ul class="dropdown-menu dropdown-menu-right" role="menu">'
        if (items.length > 0) {
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
                input = $('<input data-number="' + (baseNumber + uniqNumber) + '" name="' + name + '" data-type="address" type="text" class="form-control">')
            container.append(input)
            container.append(getDropDownAddressHTML(locationsList, isRemovable))
            if (hasCarsAmount) {
                //TODO: style spinner input, now it looks very bad
                container.append(getSpinerInput("cars", 1))
            }
            return container;
        }
    })(),
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
            <button class="btn btn-lg btn-primary btn-block has-spinner" data-action="login" type="submit">\
                <span class="spinner"><i class="glyphicon glyphicon-refresh glyphicon-spin"></i></span>\
            Sign in</button>\
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
            <div class="form-group"><h2 class="form-signin-heading">Please sign un</h2></div></form>');
        form.append($('<div class="form-group">\
            <label>Provide name</label>\
            <div class="input-group">\
                <span class="input-group-addon glyphicon glyphicon-user"></span>\
                <input type="text" class="form-control" name="user_name" data-type="user_name" placeholder="Enter your name">\
            </div>\
        </div>'))
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
        form.append($('<button class="btn btn-lg btn-primary btn-block has-spinner" data-action="reg" type="submit">\
                <span class="spinner"><i class="glyphicon glyphicon-refresh glyphicon-spin"></i></span>\
            Sign up</button>'))
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
    getEditAccount = function(){
    var container = $('<div>Edit account</div>')
        return container
    },
    getViewOrder = function(){
    var container = $('<div>Edit getViewOrder</div>')
        return container
    },
    getViewHistory = function(){
    var container = $('<div>Edit getViewHistory</div>')
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
        "getBlocked": getBlocked,
        "getLogin" : getLogin,
        "getRegistration" : getRegistration,
        "getHeaderItemIcon" : getHeaderItemIcon,
        "getAbout" : getAbout,
        "getEditAccount" : getEditAccount,
        "getViewOrder" : getViewOrder,
        "getViewHistory" : getViewHistory,
    }
})()