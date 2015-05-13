
var checkedInput = '<input type="checkbox" checked="checked" value="true"/>';
var uncheckedInput = '<input type="checkbox"  value="false"/>';
var textInput = '<input class="form-control-auto-size" type="text" value="Hello"/>';
var selectInput = '<select class="form-control-auto-size"></select>';
var selectClassInput = '<select class="form-control-auto-size"><option>Business</option><option>Standard</option><option>Economy</option></select>';
var selectCategoryInput = '<select class="form-control-auto-size"><option>A</option><option>B</option><option>C</option><option>D</option></select>';
var saveButton = '<button title="Save changes" type="button"  data-toggle="modal" data-target="#" data-car-id="" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span></button>';
var cancelButton = '<button title="Cancel" type="button"  data-toggle="modal" data-target="#" data-car-id="" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-share-alt" aria-hidden="true"></span></button>';
var hiddenDiv = '<div class="hidden"></div>';

var successModal = $('#successModal');

function startEditTariff(node) {
    var record = $(node.target).closest('tr');

    var normalState = record.html();        //for cancel
    record.append(hiddenDiv);
    record.find('div').eq(0).html(normalState);

    var name = record.find(':nth-child(2)').eq(0);
    var price = record.find(':nth-child(3)').eq(0);

    var nameVal = name.eq(0).text();
    name.eq(0).text('');
    name.eq(0).html(textInput);
    name.eq(0).find(':nth-child(1)').val(nameVal);
    name.eq(0).find(':first-child').focus();

    var priceVal = price.eq(0).text();
    price.eq(0).text('');
    price.eq(0).html(textInput);
    price.eq(0).find(':nth-child(1)').val(priceVal);

    var manage = record.find(':nth-child(4)').eq(0);

    manage.children().remove();
    manage.append(saveButton);
    manage.find(':first-child').attr('onclick', 'updateTariff("hello");');
    manage.append(' ');
    manage.append(cancelButton);
    manage.find(':nth-child(2)').attr('onclick', 'cancelEdit(event);');

}

function updateTariff(value) {
    alert(updateTariff);
    //TODO Ajax here
}

function cancelEdit(node) {
    var record = $(node.target).closest('tr');
    var normalState = record.find('div').eq(0).html();
    record.html(normalState);
}

successModal.on('hidden.bs.modal', function (event) {
    location.reload(true);
});


