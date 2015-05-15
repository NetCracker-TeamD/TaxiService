function drawTable(data) {
    $('#main_table_content').empty();
    var row_ap = $("<thead> <tr>");
    $("#main_table_content").append(row_ap)
    row_ap.append($("<th>" + "# " + "</th>"));
    for (var prop in data[0]) {
        row_ap.append($("<th>" + prop + "</th>"));
    }
    row_ap.append($("<th>Pick group to view statistic</th>"));
    var row_down = $(" </tr> </thead> <tbody >");
    $("#main_table_content").append(row_down)
    for (var i = 0; i < data.length; i++) {
        var row = $("<tr />");
        $("#main_table_content").append(row);
        row.append($("<td>" + (i + 1) + "</td>"));
        for (var prop in data[i]) {
            row.append($("<td>" + data[i][prop] + "</td>"));
        }
        row.append($("<td><input type='radio' name='pick_your_group' checked> </td>"));
    }
    var row_end = $("</tbody>" +
    "<button class='btn btn-default' id='show_statistic_menu' type='submit'>Apply</button>");
    $("#main_table_content").append(row_end);
}

$(document).ready(function(){
    $.get("groupList", function (data) {
        if (data.length!=0){
            drawTable(data);
        }else{
            $("#main_table_content").append("<h2 align='center'>You don't belong to any group!</h2>")
        }
    });
});

