/**
 * Created by Dub on 21-Apr-15.
 */
var checkedInput = '<input type="checkbox" checked="checked" value="true"/>';
var uncheckedInput = '<input type="checkbox"  value="false"/>';
var checkDiv = '<div class="checkbox"><label></label></div>';
var hiddenDiv = '<div class="hidden"></div>';
var textInput = '<input class="form-control-auto-size" type="text" value="Default text"/>'; //TODO: Remove value
var selectInput = '<select class="form-control-auto-size"><option>Anton Antonov</option><option>Vladimid Vald</option><option>Ivan Ivamov</option><option>Petrov petrov</option></select>';

var saveDriverButton = '<button type="button" onclick="" data-toggle="modal" data-target="#" data-driver-id="" class="btn btn-primary btn-sm">Save changes</button>';
var saveCarButton = '<button type="button" onclick="" data-toggle="modal" data-target="#" data-driver-id="" class="btn btn-primary btn-sm">Save changes</button>';
var cancelButton = '<button type="button" onclick="cancelEdit(event)" class="btn btn-default btn-sm">Cancel</button>';

var radioInput = '<label class="radio-inline"><input type="radio" name="driver_new_gender" value="man">Man</label><label class="radio-inline"><input type="radio" name="driver_new_gender" value="woman">Woman</label>';
//var driverInfo = '<tr class="hidden driver-info"><td colspan="8" class="driver-panel"><div id="collapseThree" class="collapse" aria-expanded="false"><div class="panel-body driver-panel"><div class="row"><div class="col-md-6"><h3 class="sm-hr">Driver:</h3><div class="form-group"><label class="control-label">Last Name:</label></div><div class="form-group"><label class="control-label">First Name:</label></div><div class="form-group"><label class="control-label">E-mail:</label></div><div class="form-group"><label class="control-label">Gender:</label></div><div class="form-group"><label class="control-label">No Smoke:</label></div><div class="form-group"><label class="control-label">License serial:</label></div><div class="form-group"><label class="control-label">License due:</label></div></div><div class="col-md-6"><h3 class="sm-hr">Car:</h3><div class="form-group"><label class="control-label">Model:</label>Aston Martin DB9</div><div class="form-group"><label class="control-label">Class:</label>Premium</div><div class="form-group"><label class="control-label">Wi-Fi:</label>Yes</div><div class="form-group"><label class="control-label">Transportation of animals:</label>No</div><div class="form-group"><label class="control-label">Conditioner:</label>Yes</div><div class="form-group"><label class="control-label">Smoking in car:</label>No</div></div></div></div><div class="row hide-driver-info" align="center" onclick="closeDriverInfo(event)"><span class="glyphicon glyphicon-chevron-up btn-lg" aria-hidden="true"></span></div></div></td></tr>';
var editDriverButton = '<button type="button" onclick="startEditDriver(event)" data-toggle="modal" data-target="#" data-driver-id="" class="btn btn-primary btn-sm" aria-label="Left Align">Edit</button>';
var editCarButton = '<button type="button" onclick="startEditCar(event)" data-toggle="modal" data-target="#" data-driver-id="" class="btn btn-primary btn-sm" aria-label="Left Align">Edit</button>';
var removeDriverButton = '<button type="button" onclick="" data-toggle="modal" data-target="#remove_driver" data-driver-id="" class="btn btn-primary btn-sm" aria-label="Left Align">Remove</button>';
var removeCarButton = '<button type="button" onclick="" data-toggle="modal" data-target="#remove_car" data-driver-id="" class="btn btn-primary btn-sm" aria-label="Left Align">Remove</button>';
var driverInfo = '<tr class="hidden driver-info"><td colspan="8" class="driver-panel"><div id="collapseThree" class="collapse" aria-expanded="false"><div class="panel-body driver-panel"><div class="row"><div class="col-md-6"><h3 class="sm-hr">Driver:</h3><div class="form-group"><label class="control-label">Last Name:</label></div><div class="form-group"><label class="control-label">First Name:</label></div><div class="form-group"><label class="control-label">E-mail:</label></div><div class="form-group"><label class="control-label">Gender:</label></div><div class="form-group"><label class="control-label">Smoke:</label></div><div class="form-group"><label class="control-label">License serial:</label></div><div class="form-group"><label class="control-label">License due:</label></div>' + editDriverButton + ' ' + removeDriverButton + '</div></div></div><div class="hide-driver-info" align="center" onclick="closeDriverInfo(event)"><span class="glyphicon glyphicon-chevron-up btn-lg" aria-hidden="true"></span></div></div></td></tr>';
var carInfo = '<div class="col-md-6"><h3 class="sm-hr">Car:</h3><div class="form-group"><label class="control-label">Model:</label>Aston Martin DB9</div><div class="form-group"><label class="control-label">Class:</label>Premium</div><div class="form-group"><label class="control-label">Wi-Fi:</label>Yes</div><div class="form-group"><label class="control-label">Transportation of animals:</label>No</div><div class="form-group"><label class="control-label">Conditioner:</label>Yes</div><div class="form-group"><label class="control-label">Smoking in car:</label>No</div>' + editCarButton + ' ' + removeCarButton + '</div>';
var selectCarClassInput = '<select class="form-control-auto-size"><option>Premium</option><option>Standard</option><option>Cheep</option></select>';
var createCarModal = $('#create_driver');

function openDriverInfo(node) {
    // ignore if mailto click
    if ($(node.target).is('a')) {
        return;
    }
    var record = $(node.target).closest('tr');
    record.addClass('hidden');
    record.after(driverInfo);

    var info = record.next().find('.collapse');

    info.on('hidden.bs.collapse', function (event) {
        var node = $(event.target).closest('tr').eq(0);
        var listRecord = node.prev();
        node.remove();
        listRecord.removeClass('hidden');

    });

    var drLastName = info.find('h3').eq(0).next();
    drLastName.append(' ' + record.children().eq(1).text());

    var drFirstName = drLastName.next();
    drFirstName.append(' ' + record.children().eq(2).text());

    var drMail = drFirstName.next();
    drMail.append(' ' + record.children().eq(3).html());

    var drGender = drMail.next();
    drGender.append(' ' + record.children().eq(4).text());

    var drNoSmoke = drGender.next();
    if (record.children().eq(5).find('span').hasClass('glyphicon-no'))
        drNoSmoke.append(' Yes');
    else
        drNoSmoke.append(' No');

    var drSerial = drNoSmoke.next();
    drSerial.append(' j2k3-so23-sdf3-pxw2');     //May  serial was got from hidden field

    var drDueDate = drSerial.next();
    drDueDate.append(' ' + record.children().eq(7).text());

    if (record.children().eq(6).text() !== 'No car') {
        var car = info.find('.row').eq(0);
        car.append(carInfo);
        //TODO: Ajax here
    }

    info.closest('tr').removeClass('hidden');
    info.collapse('show');

}

function closeDriverInfo(node) {
    var info = $(node.target).closest('tr');
    info.find('.collapse').collapse('hide');
}

function startEditCar(node) {
    var record = $(node.target).closest('.col-md-6');

    var normalState = record.html();        //for cancel
    record.after(hiddenDiv);
    record.next().html(normalState);
    //alert(record.next().html());

    var model = record.find(':nth-child(2)').eq(0).find(':nth-child(1)').eq(0);
    var carClass = record.find(':nth-child(3)').eq(0).find(':nth-child(1)').eq(0);
    var wifi = record.find(':nth-child(4)').eq(0);
    var animal = record.find(':nth-child(5)').eq(0);
    var cond = record.find(':nth-child(6)').eq(0);
    var smoke = record.find(':nth-child(7)').eq(0);

    //Remove button
    record.find(':nth-child(8)').eq(0).remove();
    record.find(':nth-child(8)').eq(0).remove();
    //
    var carModel = model.closest('div').contents().eq(1);
    carModel.remove();
    model.eq(0).after(textInput);
    model.next().val(carModel.text()).focus();

    var carClassName = carClass.closest('div').contents().eq(1);
    carClass.after(selectCarClassInput);
    carClass.next().find('option:contains("' + carClassName.text() + '")').attr('selected', 'selected');
    carClassName.remove();

    var wifiStatus = wifi.contents().eq(1).text();
    var wifiLabel = wifi.contents().eq(0).text();
    wifiLabel = wifiLabel.substr(0, wifiLabel.length - 1);
    wifi.html(checkDiv);
    if (wifiStatus === "Yes") {
        wifi.find('label').html(checkedInput + wifiLabel);
    } else {
        wifi.find('label').html(uncheckedInput + wifiLabel);
    }

    var animalStatus = animal.contents().eq(1).text();
    var animalLabel = animal.contents().eq(0).text();
    animalLabel = animalLabel.substr(0, animalLabel.length - 1);
    animal.html(checkDiv);
    if (animalStatus === "Yes") {
        animal.find('label').html(checkedInput + animalLabel);
    } else {
        animal.find('label').html(uncheckedInput + animalLabel);
    }

    var condStatus = cond.contents().eq(1).text();
    var condLabel = cond.contents().eq(0).text();
    condLabel = condLabel.substr(0, condLabel.length - 1);
    cond.html(checkDiv);
    if (condStatus === "Yes") {
        cond.find('label').html(checkedInput + condLabel);
    } else {
        cond.find('label').html(uncheckedInput + condLabel);
    }


    var smokeStatus = smoke.contents().eq(1).text();
    var smokeLabel = smoke.contents().eq(0).text();
    smokeLabel = smokeLabel.substr(0, smokeLabel.length - 1);
    smoke.html(checkDiv);
    if (smokeStatus === "Yes") {
        smoke.find('label').html(checkedInput + smokeLabel);
    } else {
        smoke.find('label').html(uncheckedInput + smokeLabel);
    }

    //Driver Change here
    var formControl = '<div class="form-group"><label class="control-label">Driver:</label></div>';
    smoke.after(formControl);
    smoke.next().append(selectInput);

    smoke.next().after(cancelButton);
    smoke.next().after(' ');
    smoke.next().after(saveCarButton);
}

function startEditDriver(node) {
    var record = $(node.target).closest('.col-md-6');

    var normalState = record.html();        //for cancel
    record.after(hiddenDiv);
    record.next().html(normalState);
    //alert(record.next().html());

    var lastName = record.find(':nth-child(2)').eq(0).find(':nth-child(1)').eq(0);
    var firstName = record.find(':nth-child(3)').eq(0).find(':nth-child(1)').eq(0);
    var mail = record.find(':nth-child(4)').eq(0).find(':nth-child(1)').eq(0);
    var gender = record.find(':nth-child(5)').eq(0);
    var smoke = record.find(':nth-child(6)').eq(0);
    var serial = record.find(':nth-child(7)').eq(0).find(':nth-child(1)').eq(0);
    var licenseDue = record.find(':nth-child(8)').eq(0).find(':nth-child(1)').eq(0);

    //Remove button
    record.find(':nth-child(9)').eq(0).remove();
    record.find(':nth-child(9)').eq(0).remove();


    var drLastName = lastName.closest('div').contents().eq(1);
    lastName.eq(0).after(textInput);
    lastName.next().val(drLastName.text()).focus();
    drLastName.remove();

    var drLFirstName = firstName.closest('div').contents().eq(1);
    firstName.eq(0).after(textInput);
    firstName.next().val(drLFirstName.text());
    drLFirstName.remove();

    var drMail = mail.next();
    mail.eq(0).after(textInput);
    mail.next().val(drMail.text());
    drMail.remove();

    var drGender = gender.contents().eq(1).text().substr(1).toLowerCase();
    gender.html(radioInput);
    gender.find('[value=' + drGender + ']').attr('checked', 'checked');

    var smokeStatus = smoke.contents().eq(1).text();
    var smokeLabel = smoke.contents().eq(0).text();
    smokeLabel = smokeLabel.substr(0, smokeLabel.length - 1);
    smoke.html(checkDiv);
    if (smokeStatus === " Yes") {
        smoke.find('label').html(checkedInput + smokeLabel);
    } else {
        smoke.find('label').html(uncheckedInput + smokeLabel);
    }

    var drSerial = serial.closest('div').contents().eq(1);
    serial.eq(0).after(textInput);
    serial.next().val(drSerial.text()).focus();
    drSerial.remove();

    var drLicenseDue = licenseDue.closest('div').contents().eq(1);
    licenseDue.eq(0).after(textInput);
    licenseDue.next().val(drLicenseDue.text()).focus();
    drLicenseDue.remove();

    var lastElem = licenseDue.closest('div');

    lastElem.after(cancelButton);
    lastElem.after(' ');
    lastElem.after(saveDriverButton);
    lastElem.after('<br>');

}

function updateCar(value) {
    alert('update Car');
    //TODO Ajax here
}
function updateDriver(value) {
    alert('update Driver');
    //TODO Ajax here
}

function createDriver() {
    alert("create Driver");
    //TODO Ajax here
}

function cancelEdit(node) {
    var record = $(node.target).closest('div');
    var normalState = record.next().html();
    record.html(normalState);
    record.next().remove();
}

createCarModal.on('shown.bs.modal', function () {
    $('#driver_first_name').focus();
});

createCarModal.on('show.bs.modal', function () {
    $('#car_model').val('');
    $('#car_wifi').removeAttr('checked');
    $('#car_animal').removeAttr('checked');
});

