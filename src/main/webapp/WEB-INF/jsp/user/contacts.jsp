<%--
  Created by IntelliJ IDEA.
  User: anton
  Date: 5/25/15
  Time: 4:36 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="form-group" id="contacts">
  <label>Provide contact information</label>
  <div class="form-group">
    <div class="input-group"><span class="input-group-addon glyphicon glyphicon-user"></span>
      <input type="text" class="form-control" name="firstName" data-type="user_name" placeholder="Enter first name" required>
    </div>
    <div class="help-block with-errors"></div>
  </div>
  <div class="form-group">
    <div class="input-group"><span class="input-group-addon glyphicon glyphicon-user"></span>
      <input type="text" class="form-control" name="lastName" placeholder="Enter last name" required>
    </div>
    <div class="help-block with-errors"></div>
  </div>
  <div class="form-group">
    <div class="input-group"><span class="input-group-addon glyphicon glyphicon-phone"></span>
      <input type="text" data-type="phone" class="form-control" name="phoneNumber" placeholder="Enter your phone number" required>
    </div>
    <div class="help-block with-errors"></div>
  </div>
  <div class="form-group">
    <div class="input-group"><span class="input-group-addon glyphicon glyphicon-envelope"></span>
      <input type="email" name="email" class="form-control" placeholder="Enter your email" required>
    </div>
    <div class="help-block with-errors"></div>
  </div>
</div>