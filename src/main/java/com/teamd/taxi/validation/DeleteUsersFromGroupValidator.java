package com.teamd.taxi.validation;

import com.teamd.taxi.entity.GroupListPK;
import com.teamd.taxi.models.admin.DeleteUsersFromGroupModel;
import com.teamd.taxi.persistence.repository.GroupListRepository;
import com.teamd.taxi.persistence.repository.GroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by Anatoliy on 22.05.2015.
 */
@Component
public class DeleteUsersFromGroupValidator implements Validator {

    private static final String GROUP_ID = "groupId";
    private static final String LIST_USERS = "users";

    @Autowired
    private GroupsRepository groupsRepository;

    @Autowired
    private GroupListRepository groupListRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return DeleteUsersFromGroupModel.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        DeleteUsersFromGroupModel deleteUsersFromGroupModel = (DeleteUsersFromGroupModel) o;

        boolean existGroup = true;
        if (deleteUsersFromGroupModel.getGroupId() == null) {
            errors.rejectValue(GROUP_ID, "admin.group.groupId.empty");
            existGroup = false;
        } else {
            if (groupsRepository.findOne(deleteUsersFromGroupModel.getGroupId()) == null) {
                errors.rejectValue(GROUP_ID, "admin.group.groupId.nonexistent");
                existGroup = false;
            }
        }

        if (deleteUsersFromGroupModel.getUsers() == null) {
            errors.rejectValue(LIST_USERS, "admin.group.listUsers.empty");
        } else {
            if (existGroup == true) {
                for (Integer userId : deleteUsersFromGroupModel.getUsers()) {
                    if (groupListRepository.findOne(new GroupListPK(userId, deleteUsersFromGroupModel.getGroupId())) == null) {
                        errors.rejectValue(LIST_USERS, "admin.group.user.notContainsInThisGroup");
                    }
                }
            }
        }
    }
}
