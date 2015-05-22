$.fn.onAvailable = function (fn) {
    var sel = this.selector;
    var timer
    var self = this
    if (this.length > 0) {
        fn.call(this)
    }
    else {
        timer = setInterval(function () {
            if ($(sel).length > 0) {
                fn.call($(sel))
                clearInterval(timer)
            }
        }, 50)
    }
}

$.fn.serializeObject = function () {
    var disabled = this.find(':input:disabled').removeAttr('disabled');
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    disabled.attr('disabled','disabled');
    return o;
};

$.isSet = function (obj) {
    return (typeof obj !== "undefined" && obj !== null)
}

$.isFunc = function (obj) {
    return (typeof obj === "function")
}

$.getURLParam = function(url, name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(url);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}