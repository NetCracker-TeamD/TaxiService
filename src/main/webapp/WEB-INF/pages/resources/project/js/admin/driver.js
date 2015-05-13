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

var radioInput = '<label class="radio-inline"><input type="radio" name="driver_new_gender" value="MALE">Male</label><label class="radio-inline"><input type="radio" name="driver_new_gender" value="FEMALE">Female</label>';

var editDriverButton = '<button type="button" onclick="startEditDriver(event)" data-toggle="modal" data-target="#" data-driver-id="" class="btn btn-primary btn-sm" aria-label="Left Align">Edit</button>';
var editCarButton = '<button type="button" onclick="startEditCar(event)" data-toggle="modal" data-target="#" data-driver-id="" class="btn btn-primary btn-sm" aria-label="Left Align">Edit</button>';
var removeDriverButton = '<button type="button" onclick="" data-toggle="modal" data-target="#remove_driver" data-driver-id="" class="btn btn-primary btn-sm" aria-label="Left Align">Remove</button>';
var removeCarButton = '<button type="button" onclick="" data-toggle="modal" data-target="#remove_car" data-driver-id="" class="btn btn-primary btn-sm" aria-label="Left Align">Remove</button>';
var driverInfo =
    '<tr class="hidden driver-info">' +
    '<td colspan="10" class="driver-panel"><div id="collapseThree" class="collapse" aria-expanded="false">' +
    '<div class="panel-body driver-panel"><div class="row">' +
    '<div class="col-md-6">' +
    '<h3 class="sm-hr">Driver:</h3>' +
    '<div class="form-group"><label class="control-label">Last Name:</label></div>' +
    '<div class="form-group"><label class="control-label">First Name:</label></div>' +
    '<div class="form-group"><label class="control-label">E-mail:</label></div>' +
    '<div class="form-group"><label class="control-label">Phone number:</label></div>' +
    '<div class="form-group"><label class="control-label">Sex:</label></div>' +
    '<div class="form-group"><label class="control-label">Enabled:</label></div>' +
    '<div class="form-group"><label class="control-label">At work:</label></div>' +
    '<div class="form-group"><label class="control-label">License serial:</label></div>' +
    editDriverButton + ' ' +
    removeDriverButton +
    '</div>' +
    '</div></div>' +
    '<div class="hide-driver-info" align="center" onclick="closeDriverInfo(event)">' +
    '<span class="glyphicon glyphicon-chevron-up btn-lg" aria-hidden="true"></span>' +
    '</div>' +
    '</div></td>' +
    '</tr>';

var carInfo =
    '<div class="col-md-6">' +
    '<h3 class="sm-hr">Car:</h3>' +
    '<div class="form-group"><label class="control-label">Model:</label></div>' +
    '<div class="form-group"><label class="control-label">Category:</label></div>' +
    '<div class="form-group"><label class="control-label">Class:</label></div>' +
    '<div class="form-group"><label class="control-label">Enabled:</label></div>' +
    editCarButton + ' ' +
    removeCarButton +
    '</div>';

var selectCarClassInput = '<select class="form-control-auto-size"><option>A</option><option>B</option><option>C</option><option>D</option></select>';
var selectCarCategoryInput = '<select class="form-control-auto-size"><option>Business</option><option>Standard</option><option>Economy</option></select>';
//var selectCarClassInput = '<select class="form-control-auto-size"><option>Premium</option><option>Standard</option><option>Cheep</option></select>';

var createCarModal = $('#create_driver');

function openDriverInfo(node, driverId) {
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

    $.ajax('/admin/driver-info', {
        type: 'post',
        dataType: 'json',
        data: {id: driverId},
        success: function (response) {
            if (response.result == "success") {
                //alert("Success");
                //showSuccess(response.content);
                //removeCarModal.modal('hide');
                // Begin driver info generating
                var content = response.content;

                var lastName = info.find('h3').eq(0).next();
                lastName.append(' ' + content.lastName);

                var firstName = lastName.next();
                firstName.append(' ' + content.firstName);

                var email = firstName.next();
                email.append(' ' + content.email);

                var phone = email.next();
                phone.append(' ' + content.phone);

                var sex = phone.next();
                sex.append(' ' + content.sex.charAt(0) + content.sex.substr(1).toLowerCase());

                var enabled = sex.next();
                if (content.enabled)
                    enabled.append(' Yes');
                else
                    enabled.append(' No');

                var atWork = enabled.next();
                if (content.work)
                    atWork.append(' Yes');
                else
                    atWork.append(' No');

                var license = atWork.next();
                license.append(' ' + response.content.license);

                // Driver features start
                var driverFeatures = response.content.features;
                var allDriverFeatures = response.driverFeatures;
                var driverFeatureHtml = license;
                $.each(allDriverFeatures, function (key, value) {
                    driverFeatureHtml.after('<div class="form-group"><label class="control-label">' + value + ':</label></div>');
                    driverFeatureHtml = driverFeatureHtml.next();
                    if (key in driverFeatures) {
                        driverFeatureHtml.append(' <span class="glyphicon glyphicon-ok glyphicon-yes" aria-hidden="true"></span>')
                    } else {
                        driverFeatureHtml.append(' <span class="glyphicon glyphicon-remove " aria-hidden="true"></span>')
                    }
                });
                // Driver features end

                if (content.car != undefined) {
                    info.find('.row').eq(0).append(carInfo);

                    var car = response.content.car;

                    var model = info.find('h3').eq(1).next();
                    model.append(' ' + car.model);

                    var category = model.next();
                    category.append(' ' + car.category);

                    var carClass = category.next();
                    carClass.append(' ' + car.class);

                    var carEnabled = carClass.next();
                    if (car.enabled)
                        carEnabled.append(' Yes');
                    else
                        carEnabled.append(' No');

                    // Car features start
                    var carFeatures = response.content.car.features;
                    var allCarFeatures = response.carFeatures;
                    var carFeatureHtml = carEnabled;
                    $.each(allCarFeatures, function (key, value) {
                        carFeatureHtml.after('<div class="form-group"><label class="control-label">' + value + ':</label></div>');
                        carFeatureHtml = carFeatureHtml.next();
                        if (key in carFeatures) {
                            carFeatureHtml.append(' <span class="glyphicon glyphicon-ok glyphicon-yes" aria-hidden="true"></span>')
                        } else {
                            carFeatureHtml.append(' <span class="glyphicon glyphicon-remove " aria-hidden="true"></span>')
                        }
                    });
                    // Car features end

                }

                info.closest('tr').removeClass('hidden');
                info.collapse('show');
                // End driver info generating
            } else {
                if (response.result == "failure") {
                    alert("Error 0: Response failure");
                    //showError(removeCarModal, response.content);
                } else {
                    alert("Error 1: Incorrect semantic part of response");
                    //showError(removeCarModal, "Something went wrong... Try again later");
                }
            }
        },
        error: function () {
            alert("Error 2: No answer");
            //showError(removeCarModal, "Something went wrong... Try again later");
        }
    });
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
    var carCategory = record.find(':nth-child(3)').eq(0).find(':nth-child(1)').eq(0);
    var carClass = record.find(':nth-child(4)').eq(0).find(':nth-child(1)').eq(0);
    var enabled = record.find(':nth-child(5)').eq(0);

    var carModel = model.closest('div').contents().eq(1);
    model.eq(0).after(textInput);
    model.next().val(carModel.text().substr(1)).focus();
    carModel.remove();

    var carCategoryName = carCategory.closest('div').contents().eq(1);
    carCategoryName.after(selectCarClassInput);
    carCategoryName.next().find('option:contains("' + carCategoryName.text().substr(1) + '")').attr('selected', 'selected');
    carCategoryName.remove();

    var carClassName = carClass.closest('div').contents().eq(1);
    carClass.after(selectCarCategoryInput);
    carClass.next().find('option:contains("' + carClassName.text().substr(1) + '")').attr('selected', 'selected');
    carClassName.remove();

    var enabledStatus = enabled.contents().eq(1).text();
    var enabledLabel = enabled.contents().eq(0).text();
    enabledLabel = enabledLabel.substr(0, enabledLabel.length - 1);
    enabled.html(checkDiv);
    if (enabledStatus === " Yes") {
        enabled.find('label').html(checkedInput + enabledLabel);
    } else {
        enabled.find('label').html(uncheckedInput + enabledLabel);
    }

    var feature = enabled.next();
    while (feature.is('div')) {
        var featureStatus = feature.find('span');
        var featureLabel = feature.find('label').text();
        featureLabel = featureLabel.substr(0, featureLabel.length - 1);
        feature.html(checkDiv);
        if (featureStatus.hasClass('glyphicon-yes'))
            feature.find('label').html(checkedInput + featureLabel);
        else
            feature.find('label').html(uncheckedInput + featureLabel);
        feature = feature.next();
    }

    var lastElem = feature.prev();

    //Remove 'Edit' and 'Remove' buttons
    feature.next().remove();
    feature.remove();


    //TODO: Driver edit here
    var formControl = '<div class="form-group"><label class="control-label">Driver:</label></div>';
    lastElem.after(formControl);
    lastElem.next().append(selectInput);

    lastElem.next().after(cancelButton);
    lastElem.next().after(' ');
    lastElem.next().after(saveCarButton);
}

function startEditDriver(node) {
    var record = $(node.target).closest('.col-md-6');

    var normalState = record.html();        //for cancel
    record.after(hiddenDiv);
    record.next().html(normalState);

    var lastName = record.find(':nth-child(2)').eq(0).find(':nth-child(1)').eq(0);
    var firstName = record.find(':nth-child(3)').eq(0).find(':nth-child(1)').eq(0);
    var mail = record.find(':nth-child(4)').eq(0).find(':nth-child(1)').eq(0);
    var phone = record.find(':nth-child(5)').eq(0).find(':nth-child(1)').eq(0);
    var sex = record.find(':nth-child(6)').eq(0);
    var enabled = record.find(':nth-child(7)').eq(0);
    var atWork = record.find(':nth-child(8)').eq(0);
    var license = record.find(':nth-child(9)').eq(0).find(':nth-child(1)').eq(0);

    var drvLastName = lastName.closest('div').contents().eq(1);
    lastName.eq(0).after(textInput);
    lastName.next().val(drvLastName.text().substr(1)).focus();
    drvLastName.remove();

    var drvFirstName = firstName.closest('div').contents().eq(1);
    firstName.eq(0).after(textInput);
    firstName.next().val(drvFirstName.text().substr(1));
    drvFirstName.remove();

    var drvMail = mail.closest('div').contents().eq(1);
    mail.eq(0).after(textInput);
    mail.next().val(drvMail.text().substr(1));
    drvMail.remove();

    var drvPhone = phone.closest('div').contents().eq(1);
    phone.eq(0).after(textInput);
    phone.next().val(drvPhone.text().substr(1));
    drvPhone.remove();

    var drvSex = sex.contents().eq(1).text().substr(1).toUpperCase();
    sex.html(radioInput);
    sex.find('[value=' + drvSex + ']').attr('checked', 'checked');

    var enabledStatus = enabled.contents().eq(1).text();
    var enabledLabel = enabled.contents().eq(0).text();
    enabledLabel = enabledLabel.substr(0, enabledLabel.length - 1);
    enabled.html(checkDiv);
    if (enabledStatus === " Yes") {
        enabled.find('label').html(checkedInput + enabledLabel);
    } else {
        enabled.find('label').html(uncheckedInput + enabledLabel);
    }

    var workStatus = atWork.contents().eq(1).text();
    var workLabel = atWork.contents().eq(0).text();
    workLabel = workLabel.substr(0, workLabel.length - 1);
    atWork.html(checkDiv);
    if (workStatus === " Yes") {
        atWork.find('label').html(checkedInput + workLabel);
    } else {
        atWork.find('label').html(uncheckedInput + workLabel);
    }

    var drvLicense = license.closest('div').contents().eq(1);
    license.eq(0).after(textInput);
    license.next().val(drvLicense.text().substr(1));
    drvLicense.remove();

    var feature = license.closest('div').next();
    while (feature.is('div')) {
        var featureStatus = feature.find('span');
        var featureLabel = feature.find('label').text();
        featureLabel = featureLabel.substr(0, featureLabel.length - 1);
        feature.html(checkDiv);
        if (featureStatus.hasClass('glyphicon-yes'))
            feature.find('label').html(checkedInput + featureLabel);
        else
            feature.find('label').html(uncheckedInput + featureLabel);
        feature = feature.next();
    }

    var lastElem = feature.prev();

    //Remove 'Edit' and 'Remove' buttons
    feature.next().remove();
    feature.remove();

    lastElem.after(cancelButton);
    lastElem.after(' ');
    lastElem.after(saveDriverButton);

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

