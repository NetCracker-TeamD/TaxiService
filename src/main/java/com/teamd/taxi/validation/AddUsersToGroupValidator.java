package com.teamd.taxi.validation;

import com.teamd.taxi.entity.GroupListPK;
import com.teamd.taxi.models.admin.AddUsersGroupModel;
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
public class AddUsersToGroupValidator implements Validator{

    private static final String GROUP_ID = "groupId";
    private static final String LIST_USERS = "users";

    @Autowired
    private GroupsRepository groupsRepository;

    @Autowired
    private GroupListRepository groupListRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return AddUsersGroupModel.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        AddUsersGroupModel addUsersGroupModel = (AddUsersGroupModel) o;

        boolean existGroup = true;
        if(addUsersGroupModel.getGroupId() == null){
            errors.rejectValue(GROUP_ID,"admin.group.groupId.empty");
            existGroup=false;
        }else{
            if(groupsRepository.findOne(addUsersGroupModel.getGroupId()) == null){
                errors.rejectValue(GROUP_ID,"admin.group.groupId.nonexistent");
                existGroup=false;
            }
        }

        if(addUsersGroupModel.getUsers() == null){
            errors.rejectValue(LIST_USERS,"admin.group.listUsers.empty");
        }else{
            if (existGroup==true) {
                for (Integer userId : addUsersGroupModel.getUsers()) {
                    if(groupListRepository.findOne(new GroupListPK(userId,addUsersGroupModel.getGroupId())) != null){
                        errors.rejectValue(LIST_USERS, "admin.group.user.yetContainsInThisGroup");
                    }
                }
            }
        }
    }
}
