/**
 * Created by Dub on 21-Apr-15.
 */
var checkedInput = '<input type="checkbox" checked="checked" value="true"/>';
var uncheckedInput = '<input type="checkbox"  value="false"/>';
var textInput = '<input class="form-control-auto-size" type="text" value="Hello"/>';
var selectInput = '<select class="form-control-auto-size"></select>';
var selectClassInput = '<select class="form-control-auto-size"><option value="3">Business</option><option value="2">Standard</option><option value="1">Economy</option><option value="4">Cargo</option></select>';
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

var createCarUrl = '/admin/create_car';

var modalErrorHidden = "alert alert-danger alert-dismissible modal-error hidden";
var modalErrorVisible = "alert alert-danger alert-dismissible modal-error";

var countButtonStartEditCar = 0;
var maxNumberCarsOnPage = 50;

function startEditCar(node) {
    countButtonStartEditCar++;
    if (countButtonStartEditCar > maxNumberCarsOnPage) {
        countButtonStartEditCar = 0;
    }
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


    var carClassVal = carClass.find("p.hidden").text();
    carClass.eq(0).text('');
    carClass.eq(0).html(selectClassInput);
    carClass.find('option[value="' + carClassVal + '"]').attr('selected', 'selected');


    var featureElem = carClass.next().eq(0);
    while (featureElem.find(':first-child').hasClass('glyphicon')) {

        if (featureElem.find(':first-child').hasClass('enable')) {
            if (featureElem.find(':first-child').hasClass('glyphicon-yes')) {
                featureElem.append(checkedInput);
            } else {
                featureElem.append(uncheckedInput);
            }
            featureElem.find(":checkbox").attr({"id": "enable", "onclick": "switcherFeatures(this)"});

            featureElem.find(':first-child').eq(0).remove();

            featureElem = featureElem.next();
            continue;
        }
        var featureId = featureElem.find(':first-child').attr("id");
        if (featureElem.find(':first-child').hasClass('glyphicon-yes')) {
            featureElem.append(checkedInput);
        } else {
            featureElem.append(uncheckedInput);
        }
        featureElem.find(":checkbox").attr({"id": featureId, "onclick": "switcherFeatures(this)"});
        featureElem.find(':first-child').eq(0).remove();

        featureElem = featureElem.next();
    }

    var driver = featureElem;
    var manage = driver.next();

    var driverId = driver.attr('driver-id');
    var driverName = driver.eq(0).text();
    driver.eq(0).text('');
    driver.eq(0).html(selectInput);
    //driver.find('select').append($("<option></option>").attr("value", driverId).text(driverName));
    //driver.find('select').append($("<option></option>").attr("value", '-1').text('No driver'));
    //driver.find('option:contains("' + driverName + '")').attr('selected', 'selected');

    driver.find("select").attr("id", "car_driver_edit_" + countButtonStartEditCar);
    driver.find("select").attr("load", "true");
    driver.find("select").attr("loadByChange", "true");
    driver.find("select").attr("name", "select_drivers");
    driver.find("select").attr("onchange", "changeDriver(this)");

    if (driverId.trim().length == 0 || (driverName.trim().length == 0)) {
        driver.find("select").attr("onclick", "generationDrivers(this, 'No driver','')");
        generationDrivers(driver.find("select"), "No driver", "");
    }
    else {
        var selectedDriver = "'<option value=\"" + driverId + "\">" + driverName + "</option>'";
        driver.find("select").attr("onclick", "generationDrivers(this, 'No driver'," + selectedDriver + ")");
        generationDrivers(driver.find("select"), "No driver", selectedDriver);
    }

    driver.find("select").attr("load", "true");
    driver.find("select").attr("loadByChange", "true");

    manage.children().remove();
    manage.append(saveButton);
    manage.find(':first-child').eq(0).attr('onclick', 'updateCar(event)');
    manage.append(' ');
    manage.append(cancelButton);
    manage.find(':nth-child(2)').attr('onclick', 'cancelEdit(event)');

}

function removeCar(id) {
    //alert(id);
    $.ajax('/admin/car-delete', {
        type: 'post',
        dataType: 'json',
        data: {id: id},
        success: function (response) {
            if (response.result == "success") {
                showSuccess(response.content);
                removeCarModal.modal('hide');
                $(successModal).attr("reloadPage", "false");

                var tr = $("#" + id).closest("tr");
                tr.addClass("deleted-car");
                var editButton = tr.find("button[title='Edit']");
                var removeButton = tr.find("button[title='Remove']");
                editButton.prop("disabled", true);
                removeButton.prop("disabled", true);

                tr.hover(function deleteHover() {
                    tr.css({"background-color": "rgba(192, 192, 192, 0.8)"});
                });

                findElementsWithEmptyDropDownAndReloadTheir();

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

function newDataWithUpdateCar(tr) {

    var model = tr.find(':nth-child(2)').eq(0);
    var carCategory = tr.find(':nth-child(3)').eq(0);
    var carClass = tr.find(':nth-child(4)').eq(1);

    var listFeatureId = [];
    var enableValue = false;
    var featureElement = tr.find(':nth-child(5)').eq(0);

    while (featureElement.find(":first-child").is($("input"))) {
        var checkBox = featureElement.find(":first-child");

        if (checkBox.is("#enable")) {
            enableValue = checkBox.prop("checked");
            featureElement = featureElement.next();
            continue;
        }
        if (checkBox.prop("checked")) {
            listFeatureId.push(checkBox.attr("id"));
        }
        featureElement = featureElement.next();
    }

    var driverElement = featureElement;

    var newData = {};
    newData['id'] = model.attr("car-id");
    newData['modelName'] = model.find(":first-child").val();
    newData['category'] = carCategory.find(":first-child").val();
    newData['classId'] = carClass.find(":first-child").val();
    newData['enable'] = enableValue;
    newData['driverId'] = driverElement.find(":first-child").val();
    newData['features'] = listFeatureId;

    return newData;
}

function oldDataWithCarsPage(tr) {
    var divHiddenElement = tr.find("div.hidden");

    var modelHidden = divHiddenElement.find(':nth-child(2)').eq(0);
    var oldModelName = modelHidden.text();
    var oldCarId = modelHidden.attr("car-id");

    var categoryHidden = modelHidden.next();
    var oldCategory = categoryHidden.text();

    var carClassHidden = categoryHidden.next();
    var oldClassId = carClassHidden.find("p.hidden").text();

    var oldListFeatureId = [];
    var oldEnableValue;
    var featureElementHidden = carClassHidden.next();

    while (featureElementHidden.find(':first-child').hasClass('glyphicon')) {

        if (featureElementHidden.find(':first-child').hasClass('enable')) {
            if (featureElementHidden.find(':first-child').hasClass('glyphicon-yes')) {
                oldEnableValue = true;
            } else {
                oldEnableValue = false;
            }
            featureElementHidden = featureElementHidden.next();
            continue;
        }

        var oldFeatureId = featureElementHidden.find(':first-child').attr("id");
        if (featureElementHidden.find(':first-child').hasClass('glyphicon-yes')) {
            oldListFeatureId.push(oldFeatureId);
        }
        featureElementHidden = featureElementHidden.next();
    }

    var driverElementHidden = featureElementHidden;
    var oldDriverId = driverElementHidden.attr("driver-id");

    var oldData = {};
    oldData['id'] = oldCarId;
    oldData['modelName'] = oldModelName;
    oldData['category'] = oldCategory;
    oldData['classId'] = oldClassId;
    oldData['enable'] = oldEnableValue;
    oldData['driverId'] = oldDriverId;
    oldData['features'] = oldListFeatureId;

    return oldData;
}

function setUpdateDataInHiddenBlock(newData, tr) {
    var divHiddenElement = tr.find("div.hidden");

    var modelHidden = divHiddenElement.find(':nth-child(2)').eq(0);
    modelHidden.text(newData['modelName']);

    var categoryHidden = modelHidden.next();
    categoryHidden.text(newData['category']);

    var carClassHidden = categoryHidden.next();
    var carClass = tr.find(':nth-child(4)').eq(1);
    carClassHiddenText = carClass.find("option[value='" + newData['classId'] + "']").text();
    carClassHiddenHTML = carClassHiddenText + "<p class=\"hidden\">" + newData['classId'] + "</p>" + carClass.val();
    carClassHidden.empty();
    carClassHidden.append(carClassHiddenHTML);

    var featureElem = carClassHidden.next();
    while (featureElem.find(':first-child').hasClass('glyphicon')) {

        if (featureElem.find(':first-child').hasClass('enable')) {
            if (featureElem.find(':first-child').hasClass('glyphicon-yes')) {
                if (!newData['enable']) {
                    featureElem.find(':first-child').removeClass("glyphicon-yes glyphicon-ok");
                    featureElem.find(':first-child').addClass("glyphicon-no glyphicon-remove");
                }
            } else {
                if (newData['enable']) {
                    featureElem.find(':first-child').removeClass("glyphicon-no glyphicon-remove");
                    featureElem.find(':first-child').addClass("glyphicon-yes glyphicon-ok");
                }
            }
            featureElem = featureElem.next();
            continue;
        }


        var featureId = featureElem.find(':first-child').attr("id");
        if (featureElem.find(':first-child').hasClass('glyphicon-yes')) {
            if (!containsObject(newData['features'], featureId)) {
                featureElem.find(':first-child').removeClass("glyphicon-yes glyphicon-ok");
                featureElem.find(':first-child').addClass("glyphicon-no glyphicon-remove");
            }
        } else {
            if (containsObject(newData['features'], featureId)) {
                featureElem.find(':first-child').removeClass("glyphicon-no glyphicon-remove");
                featureElem.find(':first-child').addClass("glyphicon-yes glyphicon-ok");
            }
        }

        featureElem = featureElem.next();
    }

    var driverHiddenElement = featureElem;
    driverHiddenElement.attr("driver-id", newData['driverId']);
    driverHiddenElement.empty();

    var driverElement = tr.find(':nth-child(9)').eq(0);
    var driverName = "";
    if (newData['driverId'] !== "-1" && newData['driverId'].trim().length !== 0) {
        driverName = driverElement.find("option[value='" + newData['driverId'] + "']").text();
        console.log(driverName);
    }
    var driverHTML = "<a href=''>" + driverName + "</a>";

    driverHiddenElement.append(driverHTML);
}

function containsObject(arrayObject, searchObject) {
    if (searchObject == null) {
        return false;
    }
    if (arrayObject.length == 0) {
        return false;
    }
    for (var i = 0; i < arrayObject.length; i++) {
        if (arrayObject[i] === searchObject) {
            return true;
        }
    }
    return false;
}

function updateCar(value) {

    var tr = $(value.target).closest('tr');

    var newData = newDataWithUpdateCar(tr);
    var oldData = oldDataWithCarsPage(tr);

    console.log(newData);
    console.log("-------------------------------------");
    console.log(oldData);


    var PostData = {};

    if (oldData['modelName'].trim() !== newData['modelName'].trim()) {
        PostData['modelName'] = newData['modelName'];
    }
    if (oldData['category'].trim() !== newData['category'].trim()) {
        PostData['category'] = newData['category'];
    }
    if (oldData['classId'].trim() !== newData['classId'].trim()) {
        PostData['classId'] = newData['classId'];
    }
    if (newData['enable'] !== oldData['enable']) {
        PostData['enable'] = newData['enable'];
    }

    var isDriverChanged = false;

    if (oldData['driverId'].trim().length == 0) oldData['driverId'] = "-1";
    if (oldData['driverId'].trim() !== newData['driverId'].trim()) {
        PostData['driverId'] = newData['driverId'];
        isDriverChanged = true;
    }
    if (JSON.stringify(newData['features']) !== JSON.stringify(oldData['features'])) {
        PostData['features'] = newData['features'];
    }

    if (oldData['id'] === newData['id']) {
        PostData['id'] = newData['id'];
    }

    if (Object.keys(PostData).length === 1) {
        tr.prev().hide();
        cancelEdit(value);
        return;
    }

    $.ajax({
        type: 'POST',
        url: "/admin/update_car",
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(PostData),
        dataType: 'json',
        async: false,
        success: function (response) {

            var trError = tr.prev();

            if (response.result == "success") {
                trError.hide();

                $(successModal).attr("reloadPage", "false");
                showSuccessUpdateCar(response.content["message"]);
                setUpdateDataInHiddenBlock(newData, tr);
                cancelEdit(value);
                if (isDriverChanged) {
                    findElementsWithEmptyDropDownAndReloadTheir();
                }

            } else {
                if (response.result == "failure") {

                    $("#update_errors_" + oldData["id"] + " p").remove();
                    trError.show(100);

                    var errors = "";
                    $.each(response.content, function (key, value) {
                        errors = errors + "<p>" + value + "</p>";
                    });
                    $("#update_errors_" + oldData["id"]).append(errors);

                } else {
                    showError(removeCarModal, "Something went wrong... Try again later");
                }
            }

        },
        error: function (error) {
            showError(removeCarModal, "Something went wrong... Try again later");
        }
    });
}

function findElementsWithEmptyDropDownAndReloadTheir() {
    var arrayElements = document.getElementsByName("select_drivers");
    for (var i = 0; i < arrayElements.length; i++) {
        var selectElement = arrayElements[i];

        $("#" + selectElement.id).attr("loadByChange", true);
        $("#" + selectElement.id).attr("load", true);

        selectElement.click();

        $("#" + selectElement.id).attr("loadByChange", true);
        $("#" + selectElement.id).attr("load", true);

    }
}

function createCar() {

    var mapFeatures = new Object();


    for (var i = 0; i < arrayIdFeaturesInHTML.length; i++) {
        var value = $('#' + arrayIdFeaturesInHTML[i]).val();
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
        url: createCarUrl,
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(JSONPostData),
        dataType: 'json',
        async: false,
        success: function (response) {
            if (response.result == "success") {
                createCarModal.hide();
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
        error: function (error) {
            showError(removeCarModal, "Something went wrong... Try again later");
        }
    });
}

function hideAllErrors() {
    $("#modelNameError").attr("class", modalErrorHidden);
    $("#classIdError").attr("class", modalErrorHidden);
    $("#mapFeaturesError").attr("class", modalErrorHidden);
    $("#categoryError").attr("class", modalErrorHidden);
    $("#enableError").attr("class", modalErrorHidden);
    $("#driverIdError").attr("class", modalErrorHidden);
}

function showModalErrors(response) {
    if (response.content["modelName"] != null) {
        $("#modelNameError").attr("class", modalErrorVisible);
        $("#modelNameError p").text(response.content["modelName"]);
    }
    if (response.content["classId"] != null) {
        $("#classIdError").attr("class", modalErrorVisible);
        $("#classIdError p").text(response.content["classId"]);
    }
    if (response.content["category"] != null) {
        $("#categoryError").attr("class", modalErrorVisible);
        $("#categoryError p").text(response.content["category"]);
    }
    if (response.content["enable"] != null) {
        $("#enableError").attr("class", modalErrorVisible);
        $("#enableError p").text(response.content["enable"]);
    }
    if (response.content["driverId"] != null) {
        $("#driverIdError").attr("class", modalErrorVisible);
        $("#driverIdError p").text(response.content["driverId"]);
    }
    if (response.content["mapFeatures"] != null) {
        $("#mapFeaturesError").attr("class", modalErrorVisible);
        $("#mapFeaturesError p").text(response.content["mapFeatures"]);
    }
}

function cancelEdit(node) {
    var record = $(node.target).closest('tr');
    var trError = record.prev();
    trError.hide();
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
    if ($(successModal).attr("reloadPage") === "true") {
        location.reload(true);
    }
    $(successModal).attr("reloadPage", "true");
});

function showSuccess(message) {
    successModal.find('.lead').html(message);
    successModal.modal('show');
}

function showSuccessUpdateCar(message) {
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
    $('#car_enable').attr("checked", false);

    $("#car_category :contains('B')").val("B");
    $('#car_category option[value=B]').attr('selected', 'selected');
    $("#car_class :contains('Standard')").val('2');
    $("#car_class option[value='2']").attr('selected', 'selected');

    hideAllErrors();

    showAddFormCar();

    $("#car_driver").attr("load", "true");
    $("#car_driver").attr("loadByChange", "true");
    generationDrivers(document.getElementById('car_driver'), 'No driver', '');
    $("#car_driver").attr("load", "true");
    $("#car_driver").attr("loadByChange", "true");
});

function showAddFormCar() {
    $.ajax({
        type: 'GET',
        url: showAddFormCarUrl,
        dataType: 'json',
        async: false,
        success: function (result) {
            generateFeaturesInAddFormCar(result, divIdForGeneratedFeatures, classInternalDivs);
        },
        error: function (error) {
            showError(removeCarModal, "Something went wrong... Try again later");
        }
    });
}

function generateFeaturesInAddFormCar(arrayMaps, divId, classInternalDiv) {

    $(divId + ' div.' + classInternalDiv).remove();

    var startTag = '<div class="' + classInternalDiv + '"><label>';
    var endTag = '</label></div>';

    var stringBuffer = null;

    for (var i = 0; i < arrayMaps.length; i++) {
        if (stringBuffer == null) {
            stringBuffer = startTag;
        } else {
            stringBuffer = stringBuffer + startTag;
        }
        stringBuffer = stringBuffer + '<input id="' + arrayMaps[i]['id'] + '" type="checkbox" onclick="switcherFeatures(this)" value="false">' + arrayMaps[i]['feature_name'];
        stringBuffer = stringBuffer + endTag;

        arrayIdFeaturesInHTML[i] = arrayMaps[i]['id'];
    }

    $(divId).append(stringBuffer);
}

function switcherFeatures(checkbox) {
    if (!checkbox.checked) {
        $(checkbox).val('false');
    } else {
        $(checkbox).val('true');
    }
}

function generationDrivers(selectElement, defaultOptionString, selectedDriver) {

    var selectionElementID = '#' + $(selectElement).attr('id');

    if ($(selectionElementID).attr("loadByChange") === "false") {
        $(selectionElementID).attr("load", "false");
        return;
    }
    if ($(selectionElementID).attr("load") === "false") {
        $(selectionElementID).attr("load", "true");
        return;
    }

    var arrayMaps = getDrivers(getDriversUrl);

    var endTag = '</option>';


    //$(selectionElementID + ' ' + 'option').remove();
    $(selectionElementID).html("");

    var stringBuffer = null;

    if (arrayMaps.length == 0) {
        if (selectedDriver.length !== 0) {
            stringBuffer = selectedDriver;
            stringBuffer = stringBuffer + '<option value="-1">' + defaultOptionString + endTag;
        } else {
            stringBuffer = '<option value="-1">' + defaultOptionString + endTag;
            $(selectionElementID).append(stringBuffer);
            return;
        }
    } else {
        for (var i = 0; i < arrayMaps.length; i++) {
            if (stringBuffer == null) {
                if (selectedDriver.length !== 0) {
                    stringBuffer = selectedDriver;
                    stringBuffer = stringBuffer + '<option value="-1">' + defaultOptionString + endTag + '<option value=' + arrayMaps[0]['id'] + '>';
                } else {
                    stringBuffer = '<option value="-1">' + defaultOptionString + endTag + '<option value=' + arrayMaps[0]['id'] + '>';
                }
            } else {
                stringBuffer = stringBuffer + '<option value=' + arrayMaps[i]['id'] + '>';
            }
            stringBuffer = stringBuffer + ' ' + arrayMaps[i]['last_name'] + ' ' + arrayMaps[i]['first_name'];
            stringBuffer = stringBuffer + endTag;
        }
    }

    $(selectionElementID).append(stringBuffer);
    $(selectionElementID).attr("loadByChange", "false");
    $(selectionElementID).attr("load", "false");
}

function getDrivers(driversUrl) {
    var result;
    $.ajax({
        type: 'GET',
        url: driversUrl,
        dataType: 'json',
        async: false,
        success: function (response) {
            result = response;
        },
        error: function (error) {
            showError(removeCarModal, "Something went wrong... Try again later");
        }
    });
    return result;
}

function changeDriver(selectElement) {
    var selectionElementID = '#' + $(selectElement).attr('id');
    $(selectionElementID).attr("loadByChange", "true");
}

function selectOrder(order) {
    $("#sort-input").find("[value=" + order + "]").attr("selected", "selected");
}