
var checkedInput = '<input type="checkbox" checked="checked" value="true"/>';
var uncheckedInput = '<input type="checkbox"  value="false"/>';
var textInput = '<input class="form-control-auto-size" type="text" value="Hello"/>';
var selectInput = '<select class="form-control-auto-size"></select>';
var selectClassInput = '<select class="form-control-auto-size"><option>Business</option><option>Standard</option><option>Economy</option></select>';
var selectCategoryInput = '<select class="form-control-auto-size"><option>A</option><option>B</option><option>C</option><option>D</option></select>';
var saveButton = '<button title="Save changes" type="button"  data-toggle="modal" data-target="#" data-car-id="" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span></button>';
var cancelButton = '<button title="Cancel" type="button"  data-toggle="modal" data-target="#" data-car-id="" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-share-alt" aria-hidden="true"></span></button>';
var hiddenDiv = '<div class="hidden"></div>';


var createTariffModal = $('#create_tariff');
var removeTariffModal = $('#remove_tariff');
var successModal = $('#successModal');

function startEditTariff(node) {
    var record = $(node.target).closest('tr');

    var normalState = record.html();        //for cancel
    record.append(hiddenDiv);
    record.find('div').eq(0).html(normalState);

    var from = record.find(':nth-child(2)').eq(0);
    var to = record.find(':nth-child(3)').eq(0);
    var price = record.find(':nth-child(4)').eq(0);
    var tariffType = record.find(':nth-child(5)').eq(0);

    var tariffFrom = from.eq(0).text();
    from.eq(0).text('');
    from.eq(0).html(textInput);
    from.eq(0).find(':nth-child(1)').val(tariffFrom);
    from.eq(0).find(':first-child').focus();

    var tariffTo = to.eq(0).text();
    to.eq(0).text('');
    to.eq(0).html(textInput);
    to.eq(0).find(':nth-child(1)').val(tariffTo);

    var tariffPrice = price.eq(0).text();
    price.eq(0).text('');
    price.eq(0).html(textInput);
    price.eq(0).find(':nth-child(1)').val(tariffPrice);

    var tariffTypeVal = tariffType.eq(0).text();
    tariffType.eq(0).text('');
    tariffType.eq(0).html(textInput);
    tariffType.eq(0).find(':nth-child(1)').val(tariffTypeVal);

    var manage = record.find(':nth-child(6)').eq(0);



    manage.children().remove();
    manage.append(saveButton);
    manage.find(':first-child').attr('onclick', 'updateTariff("hello");');
    manage.append(' ');
    manage.append(cancelButton);
    manage.find(':nth-child(2)').attr('onclick', 'cancelEdit(event);');

}

function removeTariff(id) {
    //alert(id);
    $.ajax('/admin/tariff_by_time-delete', {
        type: 'post',
        dataType: 'json',
        data: {id: id},
        success: function (response) {
            //alert("Success");
            if (response.result == "success") {
                showSuccess(response.content);
                removeTariffModal.modal('hide');
            } else {
                if (response.result == "failure") {
                    showError(removeTariffModal, response.content);
                } else {
                    showError(removeTariffModal, "Something went wrong... Try again later");
                }
            }
        },
        error: function () {
            //alert("Error");
            showError(removeTariffModal, "Something went wrong... Try again later");
        }
    });
}

function updateTariff(value) {
    alert(updateTariff);
    //TODO Ajax here
}

function createTariff() {
    alert("createTariff");
    //TODO Ajax here
}

function cancelEdit(node) {
    var record = $(node.target).closest('tr');
    var normalState = record.find('div').eq(0).html();
    record.html(normalState);
}

createTariffModal.on('shown.bs.modal', function () {
    $('#tariff_model').focus();
});

createTariffModal.on('show.bs.modal', function () {
    $('#car_model').val('');
    $('#car_wifi').removeAttr('checked');
    $('#car_animal').removeAttr('checked');
});

removeTariffModal.on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget);
    var modal = $(this);
    modal.find("[name='tariff_id']").val(button.data('tariff-id'));
    removeTariffModal.find('.modal-error').hide();
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

