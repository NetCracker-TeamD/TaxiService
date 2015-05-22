<%--
  Created by IntelliJ IDEA.
  User: Dub
  Date: 22-May-15
  Time: 08:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Groups</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="authorisation form">
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="/pages/resources/bootstrap/css/bootstrap.css">
    <link rel="stylesheet" href="/pages/resources/project/css/admin.css">
    <script src="/pages/resources/jquery/jquery-2.1.3.js"></script>
    <script src="/pages/resources/bootstrap/js/bootstrap.js"></script>
</head>

<body>
<!--common navigation bar for this service -->
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Smart Taxi</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="#">Users</a></li>
                <li class="active"><a href="">Groups</a></li>
                <li><a href="/admin/drivers">Drivers</a></li>
                <li><a href="/admin/cars">Cars</a></li>
                <li><a href="">Tariffs</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Reports
                        <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="#">Report type 1</a></li>
                        <li><a href="#">Report type 2</a></li>
                        <li><a href="#">Report type 3</a></li>
                    </ul>
                </li>
            </ul>
            <div class="navbar-form navbar-right">
                <button type="button" class="btn btn-primary">Sign out</button>
            </div>
        </div>
    </div>
</nav>

<div class="modal fade" id="create_group" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">New Group</h4>
            </div>
            <div class="modal-body">
                <div class="alert alert-danger alert-dismissible modal-error">
                    <p>Error-Message</p>
                </div>
                <div class="form-group">
                    <label for="group_name" class="control-label">Group Name:</label>
                    <input type="text" class="form-control" id="group_name">
                </div>
                <div class="form-group">
                    <label for="group_discount" class="control-label">Discount:</label>

                    <div class="input-group">
                        <span class="input-group-addon">%</span>
                        <input type="text" class="form-control" id="group_discount">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="button"
                            onclick="createGroup($('#group_name').val(),$('#group_discount').val())"
                            class="btn btn-success">Create Group
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="update_group" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Update Group</h4>
            </div>
            <div class="modal-body">
                <div class="alert alert-danger alert-dismissible modal-error">
                    <p>Error-Message</p>
                </div>
                <div class="form-group">
                    <label for="group_new_name" class="control-label">Group Name:</label>
                    <input type="text" class="form-control" id="group_new_name">
                </div>
                <div class="form-group">
                    <label for="group_new_discount" class="control-label">Discount:</label>

                    <div class="input-group">
                        <span class="input-group-addon">%</span>
                        <input type="text" class="form-control" id="group_new_discount">
                    </div>
                    <input type="hidden" name="group_id"/>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="button"
                            onclick="updateGroup($('#update_group').find('[name=\'group_id\']').val(),$('#group_new_name').val(),
                            $('#group_new_discount').val())" class="btn btn-warning">Save Changes
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade centered-modal" id="remove_group" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Remove group</h4>
            </div>
            <div class="modal-body">
                <div class="alert alert-danger alert-dismissible modal-error">
                    <p>Error-Message</p>
                </div>
                <form>
                    <input type="hidden" name="group_id"/>
                </form>
                <p class="lead">Are you really want to remove this group?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-danger"
                        onclick="removeGroup($('#remove_group').find('[name=\'group_id\']').val())">Remove Group
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade centered-modal" id="remove_users" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Remove users</h4>
            </div>
            <div class="modal-body">
                <div class="alert alert-danger alert-dismissible modal-error">
                    <p>Error-Message</p>
                </div>
                <form>
                    <input type="hidden" name="group_id"/>
                </form>
                <p class="lead">Remove selected users from current group?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-info"
                        onclick="removeSelectedUsers()">Remove Users
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade centered-modal" id="success-modal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Success</h4>
            </div>
            <div class="modal-body">
                <p class="lead">Successful operation</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-success" data-dismiss="modal">Ok</button>
            </div>
        </div>
    </div>
</div>

<div class="container" id="main_container">
    <h2 class="sm-hr">Groups</h2>

    <div id="alert_error" class="alert alert-danger alert-dismissible" role="alert" style="display: none">
        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span
                aria-hidden="true">&times;</span></button>
        <p><strong>Error!</strong> Error message.</p>
    </div>

    <div id="alert_success" class="alert alert-success alert-dismissible" role="alert" style="display: none">
        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span
                aria-hidden="true">&times;</span></button>
        <p><strong>Success.</strong> Success messages.</p>
    </div>

    <div class="row sm-hr">
        <div class="col-md-4" style="padding-right: 0">
            <button type="button" class="btn btn-success btn-sm" data-toggle="modal" data-target="#create_group">
                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span> New Group
            </button>
            <button type="button" class="btn btn-info btn-sm" data-toggle="modal" data-target="#update_group">
                <span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Edit Group
            </button>
            <button type="button" class="btn btn-info btn-sm" data-toggle="modal" data-target="#remove_group">
                <span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Remove Group
            </button>
        </div>
        <div class="col-md-4" style="padding-left: 5px">
        </div>
        <div class="col-md-4">
            <%--<form class="form-inline pull-right ">--%>
            <%--<div class="form-group">--%>
            <%--<form method="get">--%>
            <%--<label for="sort-input">Sort</label>--%>
            <%--<select class="form-control input-sm" id="sort-input" name="order" onchange="form.submit()">--%>
            <%--<option value="last_name">by Last Name</option>--%>
            <%--<option value="first_name">by First Name</option>--%>
            <%--</select>--%>
            <%--</form>--%>
            <%--</div>--%>
            <%--</form>--%>
        </div>
    </div>
    <div class="row">
        <br/>

        <div id="group_list" class="col-md-4">
            <div class="panel panel-default" style="border: none;-webkit-box-shadow: none;box-shadow: none;">
                <div class="panel-heading">Groups</div>
                <div style="height: 600px; overflow-y: auto;">
                    <ul class="list-group" onclick="changeActiveGroup(event)"></ul>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="panel panel-info">
                <div class="panel-heading">Users of Group
                    <button title="Change users manage status" type="button" class="btn btn-info btn-sm"
                            style="margin-left: 150px" onclick="manageStatusToggle()">
                        <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                    </button>
                    <button title="Remove users from group" type="button" class="btn btn-info btn-sm"
                            data-toggle="modal"
                            data-target="#remove_users">
                        <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                    </button>
                </div>
                <ul class="list-group" id="group_users_list"
                    style="height: 600px; overflow-y: auto;display: none;"></ul>
            </div>
        </div>

        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading" onclick="makeUsersToAdd(undefined,event)">Add new users</div>
                <ul class="list-group" id="all_users_list" opened="false"
                    style="height: 600px; overflow-y: auto;display: none;"></ul>
            </div>
        </div>

    </div>

    <hr/>
    <footer>
        <p>&#169 TeamD 2015</p>
    </footer>
</div>
<script type="application/javascript" src="/pages/resources/project/js/admin/group.js"></script>
</body>

</html>

