/**
 * Created by Dub on 21-Apr-15.
 */
var checkedInput = '<input type="checkbox" checked="checked" value="true"/>';
var uncheckedInput = '<input type="checkbox"  value="false"/>';
var textInput = '<input class="form-control-auto-size" type="text" value="Hello"/>';
var selectInput = '<select class="form-control-auto-size"></select>';
var selectClassInput = '<select class="form-control-auto-size"><option>Business</option><option>Standard</option><option>Economy</option></select>';
var selectCategoryInput = '<select class="form-control-auto-size"><option>A</option><option>B</option><option>C</option><option>D</option></select>';
var saveButton = '<button title="Save changes" type="button"  data-toggle="modal" data-target="#" data-car-id="" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span></button>';
var cancelButton = '<button title="Cancel" type="button"  data-toggle="modal" data-target="#" data-car-id="" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-share-alt" aria-hidden="true"></span></button>';
var hiddenDiv = '<div class="hidden"></div>';


var createCarModal = $('#create_car');
var removeCarModal = $('#remove_car');
var successModal = $('#successModal');

var showCarsUrl = '/admin/cars';
var showAddFormCarUrl = '/admin/getForm_add_car';
var getDriversUrl = '/admin/getDrivers';
var divIdForGeneratedFeatures = '#car_features_generated';
var classInternalDivs = 'checkbox';
var arrayIdFeaturesInHTML = Array();

var toggleBetweenGeneratedDriversAndChangeDriver = true;

var createCarUrl = '/admin/create_car';

var modalErrorHidden = "alert alert-danger alert-dismissible modal-error hidden";
var modalErrorVisible = "alert alert-danger alert-dismissible modal-error";


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
    $.ajax('/admin/car-delete', {
        type: 'post',
        dataType: 'json',
        data: {id: id},
        success: function (response) {
            //alert("Success");
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
            //alert("Error");
            showError(removeCarModal, "Something went wrong... Try again later");
        }
    });
}

function updateCar(value) {
    alert(updateCar);
    //TODO Ajax here
}

function createCar() {

    var mapFeatures = new Object();


    for(var i=0; i<arrayIdFeaturesInHTML.length; i++){
        var value = $('#'+arrayIdFeaturesInHTML[i]).val();
        mapFeatures[arrayIdFeaturesInHTML[i]] = value;
    }

    var JSONPostData = new Object();
    JSONPostData['modelName'] = $('#car_model').val();
    JSONPostData['classId'] = $('#car_class').val();
    JSONPostData['category'] = $('#car_category').val();
    JSONPostData['enable'] = $('#car_enable').val();
    JSONPostData['driverId'] = $('#car_driver').val();
    JSONPostData['mapFeatures'] = mapFeatures;

    $.ajax({
        type: 'POST',
        url:  createCarUrl,
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(JSONPostData),
        dataType: 'json',
        async: false,
        success: function(response) {
            if (response.result == "success") {
                showSuccess(response.content["message"]);
                removeCarModal.modal('hide');
            } else {
                if (response.result == "failure") {
                    hideAllErrors();
                    showModalErrors(response);
                } else {
                    showError(removeCarModal, "Something went wrong... Try again later");
                }
            }

        },
        error: function(error) {
            showError(removeCarModal, "Something went wrong... Try again later");
        }
    });
}

function hideAllErrors (){
    $("#modelNameError").attr("class", modalErrorHidden);
    $("#classIdError").attr("class", modalErrorHidden);
    $("#mapFeaturesError").attr("class", modalErrorHidden);
    $("#categoryError").attr("class", modalErrorHidden);
    $("#enableError").attr("class", modalErrorHidden);
    $("#driverIdError").attr("class", modalErrorHidden);
}

function showModalErrors(response){
    if(response.content["modelName"]!=null){
        $("#modelNameError").attr("class", modalErrorVisible);
        $("#modelNameError p").text(response.content["modelName"]);
    }
    if(response.content["classId"]!=null){
        $("#classIdError").attr("class", modalErrorVisible);
        $("#classIdError p").text(response.content["classId"]);
    }
    if(response.content["category"]!=null){
        $("#categoryError").attr("class", modalErrorVisible);
        $("#categoryError p").text(response.content["category"]);
    }
    if(response.content["enable"]!=null){
        $("#enableError").attr("class", modalErrorVisible);
        $("#enableError p").text(response.content["enable"]);
    }
    if(response.content["driverId"]!=null){
        $("#driverIdError").attr("class", modalErrorVisible);
        $("#driverIdError p").text(response.content["driverId"]);
    }
    if(response.content["mapFeatures"]!=null){
        $("#mapFeaturesError").attr("class", modalErrorVisible);
        $("#mapFeaturesError p").text(response.content["mapFeatures"]);
    }
}

function cancelEdit(node) {
    var record = $(node.target).closest('tr');
    var normalState = record.find('div').eq(0).html();
    record.html(normalState);
}

createCarModal.on('shown.bs.modal', function () {
    $('#car_model').focus();
});

removeCarModal.on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var modal = $(this);
    modal.find("[name='car_id']").val(button.data('car-id'));
    removeCarModal.find('.modal-error').hide();
});

successModal.on('hidden.bs.modal', function (event) {
    location.reload(true);
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

createCarModal.on('show.bs.modal', function () {

    $('#car_model').val('');
    $('#car_enable').val('false');
    $('#car_enable').attr("checked",false);

    $("#car_category :contains('B')").val("B");
    $('#car_category option[value=B]').attr('selected','selected');
    $("#car_class :contains('Standard')").val('2');
    $("#car_class option[value='2']").attr('selected', 'selected');

    hideAllErrors();

    showAddFormCar();
    generationDrivers(document.getElementById('car_driver'), 'No driver');
    toggleBetweenGeneratedDriversAndChangeDriver=false;

});

function showAddFormCar(){
    $.ajax({
        type: 'GET',
        url: showAddFormCarUrl,
        dataType: 'json',
        async: false,
        success: function(result){
            generateFeaturesInAddFormCar(result,divIdForGeneratedFeatures,classInternalDivs);
        },
        error: function(error){
            showError(removeCarModal, "Something went wrong... Try again later");
        }
    });
}

function generateFeaturesInAddFormCar(arrayMaps, divId, classInternalDiv){

    $(divId+' div.' + classInternalDiv).remove();

    var startTag = '<div class="'+classInternalDiv+'"><label>';
    var endTag = '</label></div>';

    var stringBuffer = null;

    for(var i=0; i<arrayMaps.length; i++){
        if(stringBuffer==null){
            stringBuffer=startTag;
        }else {
            stringBuffer = stringBuffer + startTag;
        }
        stringBuffer=stringBuffer + '<input id="'+arrayMaps[i]['id']+'" type="checkbox" onclick="switcherFeatures(this)" value="false">'+arrayMaps[i]['feature_name'];
        stringBuffer=stringBuffer+endTag;

        arrayIdFeaturesInHTML[i] = arrayMaps[i]['id'];
    }

    $(divId).append(stringBuffer);
}

function switcherFeatures(checkbox){
    if(!checkbox.checked){
        $(checkbox).val('false');
    }else{
        $(checkbox).val('true');
    }
}

function generationDrivers(selectElement, defaultOptionString){

    if (!toggleBetweenGeneratedDriversAndChangeDriver) {
    } else {
        var arrayMaps = getDrivers(getDriversUrl);

        var endTag = '</option>';

        var selectionElementID = '#' + $(selectElement).attr('id');
        $(selectionElementID + ' ' + 'option').remove();

        var stringBuffer = null;

        for (var i = 0; i < arrayMaps.length; i++) {
            if (stringBuffer == null) {
                stringBuffer = '<option value="-1" selected="selected">' + defaultOptionString + endTag + '<option value=' + arrayMaps[0]['id'] + '>';
            } else {
                stringBuffer = stringBuffer + '<option value=' + arrayMaps[i]['id'] + '>';
            }
            stringBuffer = stringBuffer + ' ' + arrayMaps[i]['first_name'] + '  ' + arrayMaps[i]['last_name'];
            stringBuffer = stringBuffer + endTag;
        }

        $(selectionElementID).append(stringBuffer);
    }
    toggleBetweenGeneratedDriversAndChangeDriver=true;
}

function getDrivers(driversUrl){
    var result;
    $.ajax({
        type: 'GET',
        url: driversUrl,
        dataType: 'json',
        async: false,
        success: function(response){
            result = response;
        },
        error: function(jqXHR, textStatus, errorThrown){
            alert(jqXHR.status+' '+jqXHR.responseText);
        }
    });
    return result;
}

function changeDriver(thisElement){
    toggleBetweenGeneratedDriversAndChangeDriver=false;
}