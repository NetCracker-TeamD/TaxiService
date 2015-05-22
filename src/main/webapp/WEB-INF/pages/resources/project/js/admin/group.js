/**
 * Created by Nazar Dub
 */
var addUsersButton =
    '<button title="Add selected users" onclick="addSelectedUsers()" style="margin-right: 15px" type="button" class="btn btn-primary btn-sm">' +
    '<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Add' +
    '</button>';

var hideFreeUsersList =
    '<button title="Hide" type="button" class="btn btn-primary btn-sm" style="margin-left: 15px" onclick="hideUsersToAdd()">' +
    '<span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span>' +
    '</button>';

var searchInFreeUsers =
    '<input type="text" class="search-user-input" placeholder="Search...">' +
    '<button class="btn btn-default btn-sm left-collapse-border" onclick="searchFreeUsers($(\'.search-user-input\').val())" type="button">' +
    '<span class="glyphicon glyphicon-search" aria-hidden="true"></span>' +
    '</button>';

var userAddHead =
    '<button title="Add selected users" onclick="addSelectedUsers()" style="margin-right: 15px" type="button" class="btn btn-primary btn-sm">' +
    '<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span> Add' +
    '</button>' +

    '<input type="text" class="search-user-input" placeholder="Search...">' +
    '<button class="btn btn-default btn-sm left-collapse-border" onclick="searchFreeUsers($(\'.search-user-input\').val())" type="button">' +
    '<span class="glyphicon glyphicon-search" aria-hidden="true"></span>' +
    '</button>' +

    '<button title="Hide" type="button" class="btn btn-primary btn-sm" style="margin-left: 15px" onclick="hideUsersToAdd()">' +
    '<span class="glyphicon glyphicon-arrow-up" aria-hidden="true"></span>' +
    '</button>';

var groupList = $('#group_list');
var groupUsersList = $('#group_users_list');
var freeUsersList = $('#all_users_list');

var successModal = $('#successModal');

var alertSuccess = $('#alert_success');
var alertError = $('#alert_error');

var createGroupModal = $('#create_group');
var updateGroupModal = $('#update_group');
var removeGroupModal = $('#remove_group');
var removeUsersModal = $('#remove_users');

function showModalSuccess(message) {
    successModal.find('.lead').html(message);
    successModal.modal('show');
}

function showModalError(modalId, message) {
    var errorAlert = modalId.find('.modal-error').eq(0);
    errorAlert.find('p').html("<strong>Error!</strong> " + message);
    errorAlert.slideDown();
}

function showAlertError(message) {
    alertError.find('p').html('<strong>Error!</strong> ' + message);
    alertError.slideDown(400);
    setTimeout(function () {
        alertError.slideUp(600);
    }, 5000);
}

function showAlertSuccess(message) {
    alertSuccess.find('p').html('<strong>Success.</strong> ' + message);
    alertSuccess.slideDown(400);
    setTimeout(function () {
        alertSuccess.slideUp(600);
    }, 5000);
}

removeGroupModal.on('show.bs.modal', function (event) {
    removeGroupModal.find('.modal-error').hide();
    var modal = $(this);
    groupList.find('li').each(function (index, element) {
        if ($(element).hasClass('active')) {
            modal.find("[name='group_id']").val($(element).attr('group_id'));
            return;
        }
    });
});

removeUsersModal.on('show.bs.modal', function (event) {
    removeUsersModal.find('.modal-error').hide();
});

updateGroupModal.on('show.bs.modal', function (event) {
    updateGroupModal.find('.modal-error').hide();
    var modal = $(this);
    groupList.find('li').each(function (index, element) {
        if ($(element).hasClass('active')) {
            var groupData = $(element);
            modal.find("[name='group_id']").val(groupData.attr('group_id'));
            $('#group_new_name').val(groupData.contents().eq(1).text());
            var discount = groupData.find('span').text();
            $('#group_new_discount').val(discount.substr(0, discount.length - 1));
            return;
        }
    });
});

updateGroupModal.on('shown.bs.modal', function () {
    $('#group_new_name').focus();
});

createGroupModal.on('shown.bs.modal', function () {
    $('#group_name').focus();
});

createGroupModal.on('show.bs.modal', function () {
    createGroupModal.find('.modal-error').hide();
});

successModal.on('hidden.bs.modal', function (event) {
    location.reload(true);
});

function hideErrorModal(modalId) {
    modalId.find('.modal-error').slideUp();
}

// Returns all groups
// URL: /admin/groups/get/all
var groupListMock =
    [
        {id: 7, name: "OneGroup", discount: "-5.5%"},
        {id: 9, name: "TwoGroup", discount: "+0.05%"},
        {id: 11, name: "ThreeGroup", discount: "+55%"},
        {id: 90, name: "FourGroup", discount: "-0.19%"}

    ];
// Returns all users from group with id = 89
// URL: /admin/groups/get/users?groupId=89
var usersOfGroup1Mock =
    [
        {id: 4, lastName: "Last2", firstName: "Igor", isMgr: false},
        {id: 5, lastName: "Kdsas", firstName: "NAme", isMgr: false},
        {id: 42, lastName: "Kols", firstName: "Loisd", isMgr: true},
        {id: 41, lastName: "Lsd2", firstName: "Poks", isMgr: false}
    ];

// Returns all users from group with id = 15
// URL: /admin/groups/get/users?groupId=15
var usersOfGroup2Mock =
    [
        {id: 34, lastName: "Last2", firstName: "Pols", isMgr: false},
        {id: 66, lastName: "Last3", firstName: "firs2", isMgr: true},
        {id: 2, lastName: "Last4", firstName: "firs2", isMgr: true},
        {id: 1, lastName: "Kojus", firstName: "firs42", isMgr: false}
    ];

// Returns all users  not from group with id = 89
// URL: /admin/groups/get/freeUsers?groupId=89
var usersToAddMock1 =
    [
        {id: 34, lastName: "Last2", firstName: "Pols"},
        {id: 66, lastName: "Last3", firstName: "firs2"},
        {id: 2, lastName: "Last4", firstName: "firs2"},
        {id: 12, lastName: "Kojus2", firstName: "firs423"},
        {id: 13, lastName: "Kojus3", firstName: "firs425"},
        {id: 14, lastName: "Kojus4", firstName: "firs426"},
        {id: 15, lastName: "Kojus5", firstName: "firs4287"},
        {id: 16, lastName: "Kojus6", firstName: "firs428"}
    ];

function getGroups(callback, activeGroupId) {
    var list = groupList.find('ul');
    list.html('');
    $.ajax('/admin/groups/get/all', {
        type: 'post',
        dataType: 'json',
        success: function (response) {
            if (response.result == "success") {
                $.each(response.content, function (key, value) {
                    list.append($('<li group_id="' + value.id + '" class="list-group-item"></li>')
                        .append('<span class="badge">' + value.discount + '</span>' + value.name));
                });
                if (activeGroupId !== undefined) {
                    list.find("li[group_id='" + activeGroupId + "']").addClass('active');
                } else list.find('li').eq(0).addClass('active');
                if (callback !== undefined) {
                    callback(getCurrentGroupId());
                }
            } else {
                showAlertError("Some problem on server");
            }
        },
        error: function () {
            showAlertError("Some problem on server");
        }
    });
}

function changeActiveGroup(node) {
    var group = $(node.target).closest('li');
    groupList.find('li').each(function (index, element) {
        if ($(element).hasClass('active')) {
            $(element).removeClass('active');
            return;
        }
    });
    group.addClass('active');
    hideUsersList();
    makeUsersList(group.attr('group_id'));
    if (freeUsersList.attr('opened') === 'true') {
        hideUsersToAdd();
        makeUsersToAdd();
    }
}


// Return result of operation and message
// URL: /admin/groups/delete?id=35
function removeGroup(groupId) {
    alert(groupId);
}

// Return result of operation and message
// URL: /admin/groups/update?id=35&name=newGroupName&discount=15
function updateGroup(groupId, name, discount) {
    alert(groupId);
    alert(name);
    alert(discount);
}

// Return result of operation and message
// URL: /admin/groups/create?name=groupName&discount=-0.05
function createGroup(name, discount) {
    alert(name);
    alert(discount);
}

function showUsersList() {
    groupUsersList.slideDown(400);
}

function showUsersToAdd() {
    freeUsersList.prev().removeAttr('onclick');
    freeUsersList.attr('opened', true);
    freeUsersList.prev().html(addUsersButton + searchInFreeUsers + hideFreeUsersList);
    freeUsersList.slideDown(400);
}

function hideUsersToAdd() {
    freeUsersList.prev().attr('onclick', 'makeUsersToAdd(undefined,event)');
    freeUsersList.attr('opened', false);
    freeUsersList.slideUp(400);
    freeUsersList.prev().html('Add new users');
}

function hideUsersList() {
    //groupUsersList.closest('.panel').slideUp(150);
    groupUsersList.hide();
}

function selectUser(node) {
    var user = $(node.target).closest('li');
    if (!user.hasClass('active')) {
        user.addClass('active');
    } else {
        user.removeClass('active');
    }
}

function makeUsersList(groupId) {
    if (groupId === undefined) {
        groupId = groupList.find('.active').attr('group_id');
        if (groupId === undefined) return;
    }
    $.ajax('/admin/groups/get/users', {
        type: 'post',
        dataType: 'json',
        data: {groupId: getCurrentGroupId()},
        success: function (response) {
            if (response.result == "success") {
                var users = response.content;
                groupUsersList.html('');
                for (var key in users) {
                    var value = users[key];
                    var userRow = $('<li user_id="' + value.id + '" class="list-group-item">' + value.lastName + ' ' + value.firstName + '</li>');
                    userRow.attr('onclick', 'selectUser(event)');
                    if (value.isMgr === 'true') {
                        userRow.prepend('<span title="Manager" class="glyphicon glyphicon-user" aria-hidden="true"></span> ');
                    }
                    groupUsersList.append(userRow);
                }
                showUsersList();
            } else {
                showAlertError("Some problem on server");
            }
        },
        error: function () {
            showAlertError("Some problem on server");
        }
    });
}

function makeUsersToAdd(groupId, node) {
    if (node !== undefined && ($(node.target).is('button') || $(node.target).is('span'))) return;
    if (groupId === undefined) {
        groupId = groupList.find('.active').attr('group_id');
        if (groupId === undefined) return;
    }
    $.ajax('/admin/groups/get/freeUsers', {
        type: 'post',
        dataType: 'json',
        data: {groupId: getCurrentGroupId()},
        success: function (response) {
            if (response.result == "success") {
                var users = response.content;
                freeUsersList.html('');
                $.each(users, function (key, value) {
                    var userRow = $('<li user_id="' + value.id + '" class="list-group-item">' + value.lastName + ' ' + value.firstName + '</li>');
                    userRow.attr('onclick', 'selectUser(event)');
                    freeUsersList.append(userRow);
                });
                showUsersToAdd();
            } else {
                showAlertError("Some problem on server");
            }
        },
        error: function () {
            showAlertError("Some problem on server");
        }
    });
}

function getCurrentGroupId() {
    return parseInt(groupList.find('.active').attr('group_id'));
}

function getSelectedUserFromGroup() {
    return groupUsersList.find('.active');
}

function getSelectedUserFree() {
    return freeUsersList.find('.active');
}

function manageStatusToggle() {
    var dataToSend = {};
    dataToSend.groupId = getCurrentGroupId();
    dataToSend.users = [];
    getSelectedUserFromGroup().each(function (key, value) {
        var user = {};
        user.id = parseInt($(value).attr('user_id'));
        var status = $(value).find('.glyphicon').is('span');
        user.mgr = !status;
        dataToSend.users.push(user);
    });
    alert(JSON.stringify(dataToSend));
    //TODO: ajax
}

function removeSelectedUsers() {
    var dataToSend = {};
    dataToSend.groupId = getCurrentGroupId();
    dataToSend.users = [];
    getSelectedUserFromGroup().each(function (key, value) {
        dataToSend.users.push(parseInt($(value).attr('user_id')));
    });
    alert(JSON.stringify(dataToSend));
    //TODO: ajax
}

function addSelectedUsers() {
    var dataToSend = {};
    dataToSend.groupId = getCurrentGroupId();
    dataToSend.users = [];
    getSelectedUserFree().each(function (key, value) {
        dataToSend.users.push(parseInt($(value).attr('user_id')));
    });
    alert(JSON.stringify(dataToSend));
    //TODO: ajax
}

function searchFreeUsers(text) {
    text = text.toLowerCase();
    freeUsersList.find('li').each(function (key, value) {
        var record = $(value);
        if (record.text().toLowerCase().indexOf(text) > -1) {
            record.slideDown();
        } else {
            $(value).slideUp();
        }
    });
}

getGroups(makeUsersList);


