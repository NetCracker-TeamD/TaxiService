package com.teamd.taxi.validation;

import com.teamd.taxi.entity.GroupListPK;
import com.teamd.taxi.models.admin.ChangeManagerStatusModel;
import com.teamd.taxi.persistence.repository.GroupListRepository;
import com.teamd.taxi.persistence.repository.GroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Map;

/**
 * Created by Anatoliy on 22.05.2015.
 */
@Component
public class ChangeManagerStatusValidator implements Validator {

    private static final String GROUP_ID = "groupId";
    private static final String LIST_USERS = "users";

    @Autowired
    private GroupsRepository groupsRepository;

    @Autowired
    private GroupListRepository groupListRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return ChangeManagerStatusModel.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ChangeManagerStatusModel changeManagerStatusModel = (ChangeManagerStatusModel) o;

        boolean existGroup = true;
        if(changeManagerStatusModel.getGroupId() == null){
            errors.rejectValue(GROUP_ID,"admin.group.groupId.empty");
            existGroup=false;
        }else{
            if(groupsRepository.findOne(changeManagerStatusModel.getGroupId()) == null){
                errors.rejectValue(GROUP_ID,"admin.group.groupId.nonexistent");
                existGroup=false;
            }
        }

        if(changeManagerStatusModel.getUsers() == null){
            errors.rejectValue(LIST_USERS,"admin.group.listUsers.empty");
        }else{
            if (existGroup==true) {
                for (Map<Integer,Boolean> userIdAndMgr : changeManagerStatusModel.getUsers()) {

                    for(Integer userId : userIdAndMgr.keySet()){
                        if(groupListRepository.findOne(new GroupListPK(userId,changeManagerStatusModel.getGroupId())) == null){
                            errors.rejectValue(LIST_USERS, "admin.group.user.notContainsInThisGroup");
                            break;
                        }
                    }
                }
            }
        }

    }
}
