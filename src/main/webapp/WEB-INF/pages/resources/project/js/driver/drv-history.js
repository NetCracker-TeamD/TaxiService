var attr='';
$(document).ready(function () {
    //datepicker
    var nowTemp = new Date();
    var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

    $('.clear_param').on('click',function(){
       window.location="history";
    });


    var checkin = $('#from_date').datepicker({
        format: 'dd/mm/yyyy',
        onRender: function(date) {
            return date.valueOf() > now.valueOf() ? 'disabled' : '';
        }
    }).on('changeDate', function(ev) {
        if (ev.date.valueOf() > checkout.date.valueOf()) {
            var newDate = new Date(ev.date)
            newDate.setDate(newDate.getDate() + 1);
            checkout.setValue(newDate);
        }
        checkin.hide();
        $('#to_date')[0].focus();
    }).data('datepicker');
    var checkout = $('#to_date').datepicker({
        format: 'dd/mm/yyyy',
        onRender: function(date) {
            return date.valueOf() > now.valueOf() ? 'disabled' : '';
        }
    }).on('changeDate', function(ev) {
        checkout.hide();
    }).data('datepicker');


    //validation get-query
    $('form').submit(function(e){
        var emptyinputs = $(this).find('input').filter(function(){
            return !$.trim(this.value).length;  // get all empty fields
        }).prop('disabled',true);
        var emptyinputs = $(this).find('option').filter(function(){
            return !$.trim(this.value).length;  // get all empty fields
        }).prop('disabled',true);
    });

    //Dropdown History
    $('.history_list .history_node').css('cursor', 'pointer');
    $('.history_list .history_node').click(function () {
        $(this).parent()
            .children('.history_details')
            .stop()
            .slideToggle();
    });
    $('#viewType button').click(function () {
        $('#viewType button').removeClass();
        $('#viewType button').addClass("btn btn-info");
        $(this).removeClass();
        $(this).addClass("btn btn-info active");
        if ($(this).val() == "detailed") {
            $('.panel-body .history_details').show();
        }
        if ($(this).val() == "list") {
            $('.panel-body .history_details').hide();
        }
    });
    $('.map-panel .panel-heading').click(function () {
        $(this).parent()
            .children('.map')
            .stop()
            .slideToggle();
    });

    //set attribute for href
    setAttr('id');
    setAttr('address');
    setAttr('srvc_type');
    setAttr('startDate');
    setAttr('endDate');
    $('#type_sort li a').each(function() {
        var _href=$(this).attr("href");
        $(this).attr("href", _href + attr);
    });
    $('.pagination li a').each(function(){
        var _href=$(this).attr("href");
        $(this).attr("href",_href+attr);
    });
});

function setAttr(name){
    var param=getUrlParameter(name);
    if(!(typeof param === 'undefined')){
        attr+='&'+name+'='+param;
    }
}

function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) {
            return sParameterName[1];
        }
    }
}