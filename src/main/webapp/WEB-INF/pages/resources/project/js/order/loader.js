var Loader = (function () {
    var
        loadResults = {},
        callBacks = [],
        runCallBacks = function () {
            for (var i in callBacks) {
                var callBack = callBacks[i]
                if (typeof callBack == "function") {
                    callBack()
                }
            }
            console.log("callBacks called")
        }
    addThreadNames = function (names) {
        if (typeof names == "string") {
            loadResults[names] = null
        } else {
            for (var i in names) {
                loadResults[names[i]] = null
            }
        }
    },
        check = function () {
            var doCallBack = true;
            for (var key in loadResults) {
                if (loadResults[key] == null) {
                    doCallBack = false
                }
            }
            if (doCallBack) {
                runCallBacks()

            }
        },
        addCallBack = function (callBack) {
            callBacks.push(callBack)
        },
        setStatus = function (name, status) {
            loadResults[name] = status
            console.log("'" + name + "' loaded with status '" + status + "'")
            check()
        }

    return public_interface = {
        "addThreadNames": addThreadNames,
        "check": check,
        "setStatus": setStatus,
        "addCallBack": addCallBack
    }

})