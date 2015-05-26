<%--
  Created by IntelliJ IDEA.
  User: anton
  Date: 5/25/15
  Time: 4:36 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="form-group" id="contacts"><label>Provide contact information</label>

  <div class="input-group"><span class="input-group-addon glyphicon glyphicon-user"></span> <input type="text"
                                                                                                   class="form-control"
                                                                                                   name="firstName"
                                                                                                   data-type="user_name"
                                                                                                   placeholder="Enter first name">
  </div>
  <div class="input-group"><span class="input-group-addon glyphicon glyphicon-user"></span> <input type="text"
                                                                                                   class="form-control"
                                                                                                   name="lastName"
                                                                                                   data-type="user_name"
                                                                                                   placeholder="Enter last name">
  </div>
  <div class="input-group"><span class="input-group-addon glyphicon glyphicon-phone"></span> <input type="phone"
                                                                                                    class="form-control"
                                                                                                    name="phoneNumber"
                                                                                                    id="phone"
                                                                                                    data-type="phone"
                                                                                                    placeholder="Enter your phone number">
  </div>
  <div class="input-group"><span class="input-group-addon glyphicon glyphicon-envelope"></span> <input type="email"
                                                                                                       name="email"
                                                                                                       class="form-control"
                                                                                                       id="email"
                                                                                                       placeholder="Enter your email">
  </div>
</div>