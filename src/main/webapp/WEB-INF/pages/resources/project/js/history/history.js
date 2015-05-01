$(document).ready(function() {
    var sort = getUrlParameter('sort');
    var page = getUrlParameter('page');
    $()
    $('.panel-body #history_node').css({background: '#FFF5EE'});
    $('.panel-body #history_node').mouseenter(function () {
        $(this).css({'background': '#FFFF00'});
    }).mouseleave(function () {
        $(this).css({'background' : '#FFF5EE'});
    });
    $('#history_list #history_node').css('cursor', 'pointer');
    $('#history_list #history_node').click(function() {
        $(this).parent()
            .children('#history_details')
            .stop()
            .slideToggle();
    });
} );
function getUrlParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++)
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam)
        {
            return sParameterName[1];
        }
    }
}