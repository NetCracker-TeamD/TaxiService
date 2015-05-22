package com.teamd.taxi.controllers.admin;

import com.teamd.taxi.entity.GroupList;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.entity.UserGroup;
import com.teamd.taxi.models.admin.AdminResponseModel;
import com.teamd.taxi.models.admin.CreateGroupModel;
import com.teamd.taxi.models.admin.UpdateCarModel;
import com.teamd.taxi.models.admin.UpdateGroupModel;
import com.teamd.taxi.service.GroupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by Anatoliy on 21.05.2015.
 */
@Controller
@RequestMapping(value = "/admin/groups")
public class GroupAdminController {

    private static final String MESSAGE_GROUP_ID_NOT_EXIST = "admin.group.groupId.nonexistent";
    private static final String MESSAGE_SUCCESS_GROUP_DELETE = "admin.group.success.delete";
    private static final String MESSAGE_SUCCESS_GROUP_UPDATE = "admin.group.success.update";
    private static final String MESSAGE_SUCCESS_GROUP_CREATE = "admin.group.success.create";

    @Autowired
    Environment env;

    @Autowired
    private GroupsService groupsService;

    @RequestMapping("/")
    public String getGroupsPage() {
        return "admin/groups";
    }

    @RequestMapping(value = "/get/all", method = RequestMethod.POST)
    @ResponseBody
    public Object getAllGroups() {
        AdminResponseModel<List<Map<String, String>>> adminResponseModel = new AdminResponseModel<>();
        adminResponseModel.setResultSuccess();

        List<UserGroup> userGroups = groupsService.getGroupsList();
        List<Map<String, String>> listResponse = new ArrayList<>();
        Map<String, String> group = null;

        for (UserGroup userGroup : userGroups) {
            group = new HashMap<>();
            group.put("id", userGroup.getGroupId().toString());
            group.put("name", userGroup.getName());
            group.put("discount", userGroup.getDiscount().toString());

            listResponse.add(group);
            group = null;
        }

        adminResponseModel.setContent(listResponse);
        return adminResponseModel;
    }

    @RequestMapping(value = "/get/users", method = RequestMethod.POST)
    @ResponseBody
    public Object getUsersByGroupId(@RequestParam(value = "groupId") Integer groupId) {

        if (groupsService.isExistUserGroup(groupId)) {
            AdminResponseModel<List<Map<String, String>>> adminResponseModel = new AdminResponseModel<>();
            adminResponseModel.setResultSuccess();

            List<GroupList> groupLists = groupsService.getGroupsListByUserGroupId(groupId);
            List<Map<String, String>> listResponse = new ArrayList<>();
            Map<String, String> userInGroup = null;

            for (GroupList groupList : groupLists) {
                userInGroup = new HashMap<>();
                User user = groupList.getUser();
                userInGroup.put("id", user.getId().toString());
                userInGroup.put("lastName", user.getLastName());
                userInGroup.put("firstName", user.getFirstName());
                userInGroup.put("isMgr", new Boolean(groupList.isManager()).toString());

                listResponse.add(userInGroup);
                userInGroup = null;
            }

            adminResponseModel.setContent(listResponse);
            return adminResponseModel;
        } else {
            AdminResponseModel<String> adminResponseModel = new AdminResponseModel<>();
            adminResponseModel.setResultFailure().setContent(env.getRequiredProperty(MESSAGE_GROUP_ID_NOT_EXIST));
            return adminResponseModel;
        }
    }

    @RequestMapping(value = "/get/freeUsers", method = RequestMethod.POST)
    @ResponseBody
    public Object getUsersWhichNotIncludedInGroupWithGroupId(@RequestParam(value = "groupId") Integer groupId) {
        if (groupsService.isExistUserGroup(groupId)) {
            AdminResponseModel<List<Map<String, String>>> adminResponseModel = new AdminResponseModel<>();
            adminResponseModel.setResultSuccess();

            List<GroupList> groupLists = groupsService.getGroupsListWhichNotContainsUserGroupId(groupId);
            List<Map<String, String>> listResponse = new ArrayList<>();
            Map<String, String> userInGroup = null;

            for (GroupList groupList : groupLists) {
                userInGroup = new HashMap<>();
                User user = groupList.getUser();
                userInGroup.put("id", user.getId().toString());
                userInGroup.put("lastName", user.getLastName());
                userInGroup.put("firstName", user.getFirstName());

                listResponse.add(userInGroup);
                userInGroup = null;
            }

            adminResponseModel.setContent(listResponse);
            return adminResponseModel;
        } else {
            AdminResponseModel<String> adminResponseModel = new AdminResponseModel<>();
            adminResponseModel.setResultFailure().setContent(env.getRequiredProperty(MESSAGE_GROUP_ID_NOT_EXIST));
            return adminResponseModel;
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    public Object deleteGroup(@RequestParam(value = "id") Integer groupId) {
        AdminResponseModel<String> response = new AdminResponseModel<>();
        try {
            groupsService.removeGroup(groupId);
            response.setResultSuccess();
            response.setContent(env.getRequiredProperty(MESSAGE_SUCCESS_GROUP_DELETE));
        } catch (EmptyResultDataAccessException e) {
            response.setContent(env.getRequiredProperty(MESSAGE_GROUP_ID_NOT_EXIST));
        }
        return response;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Object updateGroup(@RequestBody @Valid UpdateGroupModel updateGroupModel, BindingResult result) {

        if (result.hasErrors()) {
            AdminResponseModel<Map<String, String>> response = new AdminResponseModel<>();
            response.setResultFailure();

            Map<String, String> mapError = new HashMap<>();

            for (FieldError fieldError : result.getFieldErrors()) {
                mapError.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            response.setContent(mapError);
            return response;
        } else {

            groupsService.updateGroupWithUpdateGroupModel(updateGroupModel);

            AdminResponseModel<String> response = new AdminResponseModel<>();
            response.setResultSuccess().setContent(env.getRequiredProperty(MESSAGE_SUCCESS_GROUP_UPDATE));
            return response;
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Object createGroup(@Valid CreateGroupModel createGroupModel, BindingResult result) {
        if (result.hasErrors()) {
            AdminResponseModel<Map<String, String>> response = new AdminResponseModel<>();
            response.setResultFailure();

            Map<String, String> mapError = new HashMap<>();

            for (FieldError fieldError : result.getFieldErrors()) {
                mapError.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            response.setContent(mapError);
            return response;
        } else {

            groupsService.createGroupWithCreateGroupModel(createGroupModel);

            AdminResponseModel<String> response = new AdminResponseModel<>();
            response.setResultSuccess().setContent(env.getRequiredProperty(MESSAGE_SUCCESS_GROUP_CREATE));
            return response;
        }
    }

}
