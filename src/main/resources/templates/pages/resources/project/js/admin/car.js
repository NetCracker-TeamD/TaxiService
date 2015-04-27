/**
 * Created by Dub on 21-Apr-15.
 */
var checkedInput = '<input type="checkbox" checked="checked" value="true"/>';
var uncheckedInput = '<input type="checkbox"  value="false"/>';
var textInput = '<input class="form-control-auto-size" type="text" value="Hello"/>';
var selectInput = '<select class="form-control-auto-size"><option>Anton Antonov</option><option>Vladimid Vald</option><option>Ivan Ivamov</option><option>Petrov petrov</option></select>';
var selectClassInput = '<select class="form-control-auto-size"><option>Premium</option><option>Standard</option><option>Cheep</option></select>';
var saveButton = '<button title="Save changes" type="button"  data-toggle="modal" data-target="#" data-car-id="" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span></button>';
var cancelButton = '<button title="Cancel" type="button"  data-toggle="modal" data-target="#" data-car-id="" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-share-alt" aria-hidden="true"></span></button>';
var hiddenDiv = '<div class="hidden"></div>';


var createCarModal = $('#create_car');

function startEditCar(node) {
    var record = $(node.target).closest('tr');

    var normalState = record.html();        //for cancel
    record.append(hiddenDiv);
    record.find('div').eq(0).html(normalState);

    var model = record.find(':nth-child(2)').eq(0);
    var carClass = record.find(':nth-child(3)').eq(0);
    var wifi = record.find(':nth-child(4)').eq(0);
    var animal = record.find(':nth-child(5)').eq(0);
    var cond = record.find(':nth-child(6)').eq(0);
    var smoke = record.find(':nth-child(7)').eq(0);
    var driver = record.find(':nth-child(8)').eq(0);
    var manage = record.find(':nth-child(9)').eq(0);

    var carModel = model.eq(0).text();
    model.eq(0).text('');
    model.eq(0).html(textInput);
    model.eq(0).find(':nth-child(1)').val(carModel);
    model.eq(0).find(':first-child').focus();

    if (wifi.find(':first-child').hasClass('glyphicon-yes')) {
        wifi.append(checkedInput);
    } else {
        wifi.append(uncheckedInput);
    }
    wifi.find(':first-child').eq(0).remove();

    if (animal.find(':first-child').hasClass('glyphicon-yes')) {
        animal.append(checkedInput);
    } else {
        animal.append(uncheckedInput);
    }
    animal.find(':first-child').eq(0).remove();

    if (cond.find(':first-child').hasClass('glyphicon-yes')) {
        cond.append(checkedInput);
    } else {
        cond.append(uncheckedInput);
    }
    cond.find(':first-child').eq(0).remove();

    if (smoke.find(':first-child').hasClass('glyphicon-yes')) {
        smoke.append(checkedInput);
    } else {
        smoke.append(uncheckedInput);
    }
    smoke.find(':first-child').eq(0).remove();

    var carClassName = carClass.text();
    carClass.eq(0).text('');
    carClass.eq(0).html(selectClassInput);
    carClass.find('option:contains("' + carClassName + '")').attr('selected', 'selected');

    var driverId;
    driver.eq(0).text('');
    driver.eq(0).html(selectInput);
    //TODO: dynamic  downloading of drivers list

    manage.children().remove();
    manage.append(saveButton);
    manage.find(':first-child').attr('onclick', 'updateCar("hello");');
    manage.append(' ');
    manage.append(cancelButton);
    manage.find(':nth-child(2)').attr('onclick', 'cancelEdit(event);');

}

function updateCar(value) {
    alert(updateCar);
    //TODO Ajax here
}

function createCar() {
    alert("createCar");
    //TODO Ajax here
}

function cancelEdit(node) {
    var record = $(node.target).closest('tr');
    var normalState = record.find('div').eq(0).html();
    record.html(normalState);
}

createCarModal.on('shown.bs.modal', function () {
    $('#car_model').focus();
});

createCarModal.on('show.bs.modal', function () {
    $('#car_model').val('');
    $('#car_wifi').removeAttr('checked');
    $('#car_animal').removeAttr('checked');
});

