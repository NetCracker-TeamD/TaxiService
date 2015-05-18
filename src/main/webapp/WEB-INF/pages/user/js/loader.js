/*
Usage template

//init loader and bind callback when all necessary datas will be loaded
var loader = new Loader()
loader.addCallBack(function(){ 
	console.log("calling create page")
	createPage()
})
//binding data loaders
var ids = loader.getArrayUniqId(3)
DataTools.getUser(loader, ids[0], function(status, response, userInfo){
	tmpStorage.user = userInfo
})
DataTools.getServiceTypes(loader, ids[1], function(status, response, serviceTypes){
	tmpStorage.serviceTypes = serviceTypes
})
DataTools.getServiceTypes(loader, ids[2], function(status, response, favLications){
	tmpStorage.favLications = favLications
})


*/

var Loader = (function(){
	var 
		loadResults = {},
		callBacks = [],
		uniqId = 0,
		getUniqId = function(doFill){
			var id = (++uniqId).toString()
			while ($.isSet(loadResults[id])) {
				(++uniqId).toString()
			}
			if (!$.isSet(doFill) || doFill == true ) {
				addThreadNames(id)
			}
			return id;
		},
		getArrayUniqId = function(size, doFill){
			var ids = []
			for (var i=0;i<size;i++){
				ids.push(getUniqId(false))
			}
			if (!$.isSet(doFill) || doFill == true ) {
				addThreadNames(ids)
			}
			return ids;
		},
		runCallBacks = function(){
			console.log("calling")
			for (var i in callBacks){
				var callBack = callBacks[i]
				if (typeof callBack == "function"){
					callBack()
				}
			}
			console.log("all callBacks called")
		},
		addThreadName = function(name){
			loadResults[name] = 0
			console.log("Thread "+name+" added")
		},
		addThreadNames = function(names){
			if (typeof names == "string"){
				addThreadName(names)
			} else {
				for (var i in names){
					addThreadName(names[i])
				}
			}
		},
		check = function(){
			var doCallBack = true;
			for (var key in loadResults){
				if (loadResults[key] == 0){
					doCallBack = false
				}
			}
			console.log(loadResults)
			console.log(doCallBack)
			if (doCallBack) {
				runCallBacks()
			}
		},
		addCallBack = function(callBack){
			callBacks.push(callBack)
			console.log("callback added")
		},
		setStatus = function(name, status){
			loadResults[name] = status.toString()
			console.log("Thread '"+name+"' loaded with status '"+loadResults[name]+"'")
			check()
		}

	return public_interface = {
		"addThreadNames" : addThreadNames,
		"check" : check,
		"setStatus" : setStatus,
		"addCallBack" : addCallBack,
		"getUniqId" : getUniqId,
		"getArrayUniqId" : getArrayUniqId
	}
	
})