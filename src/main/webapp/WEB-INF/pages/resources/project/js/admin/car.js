/**
 * Created by Dub on 21-Apr-15.
 */
var checkedInput = '<input type="checkbox" checked="checked" value="true"/>';
var uncheckedInput = '<input type="checkbox"  value="false"/>';
var textInput = '<input class="form-control-auto-size" type="text" value="Hello"/>';
//var selectInput = '<select class="form-control-auto-size"><option>Anton Antonov</option><option>Vladimid Vald</option><option>Ivan Ivamov</option><option>Petrov petrov</option></select>';
var selectInput = '<select class="form-control-auto-size"></select>';
var selectClassInput = '<select class="form-control-auto-size"><option>Business</option><option>Standard</option><option>Economy</option></select>';
var selectCategoryInput = '<select class="form-control-auto-size"><option>A</option><option>B</option><option>C</option><option>D</option></select>';
var saveButton = '<button title="Save changes" type="button"  data-toggle="modal" data-target="#" data-car-id="" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span></button>';
var cancelButton = '<button title="Cancel" type="button"  data-toggle="modal" data-target="#" data-car-id="" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-share-alt" aria-hidden="true"></span></button>';
var hiddenDiv = '<div class="hidden"></div>';


var createCarModal = $('#create_car');
var removeCarModal = $('#remove_car');
var successModal = $('#successModal');

function startEditCar(node) {
    var record = $(node.target).closest('tr');

    var normalState = record.html();        //for cancel
    record.append(hiddenDiv);
    record.find('div').eq(0).html(normalState);

    var model = record.find(':nth-child(2)').eq(0);
    var carCategory = record.find(':nth-child(3)').eq(0);
    var carClass = record.find(':nth-child(4)').eq(0);

    var carModel = model.eq(0).text();
    model.eq(0).text('');
    model.eq(0).html(textInput);
    model.eq(0).find(':nth-child(1)').val(carModel);
    model.eq(0).find(':first-child').focus();

    var carCategoryVal = carCategory.text();
    carCategory.eq(0).text('');
    carCategory.eq(0).html(selectCategoryInput);
    carCategory.find('option:contains("' + carCategoryVal + '")').attr('selected', 'selected');

    var carClassVal = carClass.text();
    carClass.eq(0).text('');
    carClass.eq(0).html(selectClassInput);
    carClass.find('option:contains("' + carClassVal + '")').attr('selected', 'selected');

    var featureElem = carClass.next().eq(0);
    while (featureElem.find(':first-child').hasClass('glyphicon')) {

        if (featureElem.find(':first-child').hasClass('glyphicon-yes')) {
            featureElem.append(checkedInput);
        } else {
            featureElem.append(uncheckedInput);
        }
        featureElem.find(':first-child').eq(0).remove();

        featureElem = featureElem.next();
    }

    var driver = featureElem;
    var manage = driver.next();

    var driverId = driver.attr('driver-id');
    var driverName = driver.eq(0).text();
    driver.eq(0).text('');
    driver.eq(0).html(selectInput);
    driver.find('select').append($("<option></option>").attr("value", driverId).text(driverName));
    driver.find('select').append($("<option></option>").attr("value", '-1').text('No driver'));
    //driver.find('option:contains("' + driverName + '")').attr('selected', 'selected');
    //TODO: dynamic  downloading of drivers list

    manage.children().remove();
    manage.append(saveButton);
    manage.find(':first-child').attr('onclick', 'updateCar("hello");');
    manage.append(' ');
    manage.append(cancelButton);
    manage.find(':nth-child(2)').attr('onclick', 'cancelEdit(event);');

}

function removeCar(id) {
    //alert(id);
    $.ajax('admin/car-delete', {
        type: 'post',
        dataType: 'json',
        data: {id: 1},
        success: function (response) {
            alert("Succes");
            if (response.result == "success") {
                showSuccess(response.content);
                removeCarModal.modal('hide');
            } else {
                if (response.result == "failure") {
                    showError(removeCarModal, response.content);
                } else {
                    showError(removeCarModal, "Something went wrong... Try again later");
                }
            }
        },
        error: function () {
            alert("Error");
            showError(removeCarModal, "Something went wrong... Try again later");
        }
    });
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

removeCarModal.on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var modal = $(this);
    modal.find("[name='car_id']").val(button.data('car-id'));
    removeCarModal.find('.modal-error').hide();
});


function showSuccess(message) {
    successModal.find('.lead').html(message);
    successModal.modal('show');
}


function showError(modalId, message) {
    var errorAlert = modalId.find('.modal-error').eq(0);
    errorAlert.find('p').html("<strong>Error!</strong> " + message);
    errorAlert.show(1000);
}
function hideError(modalId) {
    $(modalId).hide('normal');
}

successModal.on('hidden.bs.modal', function (event) {
    location.reload(true);
});


