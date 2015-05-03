var Templates = (function(){
	var 
		getDropDownAddressHTML = function(items, isRemovable){

			var str = '<div class="input-group-btn">\
					<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false"><span class="caret"></span></button>\
					<ul class="dropdown-menu dropdown-menu-right" role="menu">'
			$.each(items, function(i,item){
				str += '<li><a>'+item.name+' <small>'+item.address+'</small></a></li>'
			})
			str += '</ul>'
			if (isRemovable==true){
				str += '<button class="btn btn-danger" type="button" data-action="remove">\
					<span class="glyphicon glyphicon-remove-sign" aria-hidden="true"></span>\
					</button>'
			}
			str += '</div>'
			return str
		},
		getAddressesContainer = function(){
			return $('<div class="form-group" id="addressesContainer">\
					<label>Enter addresses</label>\
					</div>')
		},
		getAddressesGroup = function(label, hasAddButton){
			var addButton = '<button data-action="add" type="button" class="btn btn-sm btn-success pull-right">Add</button>'
			return $('<div>\
						<label>'+label+'</label>\
						<div data-type="address-group"></div>'+
						((hasAddButton)?addButton:"")+
						'<div class="clearfix"></div>\
					</div>')
		},
		getAddress = function(locationsList, isRemovable){
			return $('<div class="input-group">\
				<input name="addresses" type="text" class="form-control">'+
				getDropDownAddressHTML(locationsList, isRemovable) + '</div>')
		},
		getDateTimePicker = function(name){
			var picker = $('<div id="datetimepicker" class="input-append date">\
				<input type="text" name="'+name+'"></input>\
				<span class="add-on">\
				<i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>\
				</span>\
				</div>')
			picker.datetimepicker({
		        format: 'dd/MM/yyyy hh:mm',
		        pickSeconds: false,
		        language: 'en',
		      });
			return picker
		},
		getTime = function(isNow, isCustom){
			var container = $('<div class="form-group"><label>Enter time</label</div>')
			/*if (isNow == isCustom && isNow == true) {

			} else  if (isCustom) {
				container.append(getDateTimePicker)
			} else {*/
				container.attr("class","")
				container.html('<input type="hidden" name="time" value="now">')
			//}

			return container
		},
		getFeaturesGroup = function(groupName, inputName, items){
			var container = $('<div class="input-group"><label>'+groupName+'</label><br></div')
			for (var i in items){
				var item = items[i]
				container.append($('<label class="radio-inline">\
					<input type="radio" name="'+inputName+'" value="'+item+'">'+item+
					'</label>'))
			}
			container.append('<div class="clearfix"></div>')
			var deselectBtn = $('<button type="button" class="btn btn-default pull-right">Deselect</button>')
			deselectBtn.bind("click", function(e){
				container.find("input").prop('checked', false)
			})
			container.append(deselectBtn)
			return container
		},
		getFeaturesItem = function(itemName, inputName){
			return $('<div class="checkbox">\
				<label>\
				<input type="checkbox" name="'+inputName+'"\
				value="'+itemName+'">'+itemName+'</label>\
				</div>')
		},
		getFeaturesContainer = function(){
			return $('<div class="form-group">\
				<label>Pick additional options</label>\
				</div>')
		},
		getCarsAmount = function(minValue){
			if (minValue===null || minValue===undefined || parseInt(minValue).isNaN) {
				minValue = 1
			}
			var container = $('<div class="form-group">\
					<label>Enter cars amount</label>\
					<div class="input-group">\
					<input type="text" name="cars_amount" class="form-control" data-type="cars-amount">\
					<div class="input-group-btn">\
					<button type="button" class="btn btn-default" data-action="inc"><span class="glyphicon glyphicon-chevron-up"></span></button>\
					<button type="button" class="btn btn-default" data-action="dec"><span class="glyphicon glyphicon-chevron-down"></span></button>\
					</div>\
					</div>\
				</div>'),
				input = container.find('[data-type="cars-amount"]'),
				incBtn = container.find('[data-action="inc"]'),
				decBtn = container.find('[data-action="dec"]')
			input.numeric({ decimal: false, negative: false })
			input.val(minValue)
			//coz jquery.numeric doesn`t support min/max
			var min = minValue,
				max = 99
				filter = function(e){ if (input.val()<min) { input.val(min) } else if (input.val()>max) { input.val(max) } }
			input.bind("keyup", filter)
			input.bind("change", filter)
			incBtn.bind("click", function(e){
				var oldVal = parseInt(input.val())
				if (!oldVal.isNan) {
					input.val(oldVal+1)
				} else {
					input.val(99)
				}
				filter()
			})
			decBtn.bind("click", function(e){
				var oldVal = parseInt(input.val())
				if (!oldVal.isNan) {
					input.val(oldVal-1)
				} else {
					input.val(1)
				}
				filter()
			})
			return container
		},
		getContacts = function(){
			var container = $('<div class="form-group" id="contacts">\
					<label>Provide contact information</label>\
					<div class="input-group">\
						<span class="input-group-addon glyphicon glyphicon-phone"></span>\
						<input type="phone" class="form-control" id="phone" data-type="phone">\
					</div>\
					<div class="input-group">\
						<span class="input-group-addon">@</span>\
						<input type="email" class="form-control" id="email">\
					</div>\
				</div>')
			container.find('[data-type="phone"]').mask("(999) 999-9999")

			return container
		},
		getMakeOrderBtn = function(){

		}

	return public_interface = {
		"getDropDownAddressHTML" : getDropDownAddressHTML,
		"getAddressesContainer" : getAddressesContainer,
		"getAddressesGroup" : getAddressesGroup,
		"getAddress" : getAddress,
		"getContacts" : getContacts,
		"getCarsAmount" : getCarsAmount,
		"getTime" : getTime,
		"getFeaturesContainer" : getFeaturesContainer,
		"getFeaturesItem" : getFeaturesItem,
		"getFeaturesGroup" : getFeaturesGroup,
		"getMakeOrderBtn" : getMakeOrderBtn
	}
})()