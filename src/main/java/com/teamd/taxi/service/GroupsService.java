package com.teamd.taxi.service;

import com.teamd.taxi.entity.GroupList;
import com.teamd.taxi.entity.GroupListPK;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.entity.UserGroup;
import com.teamd.taxi.models.admin.CreateGroupModel;
import com.teamd.taxi.models.admin.UpdateGroupModel;
import com.teamd.taxi.persistence.repository.GroupListRepository;
import com.teamd.taxi.persistence.repository.GroupsRepository;
import com.teamd.taxi.persistence.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupsService {

    @Autowired
    private GroupsRepository groupsRepository;
    @Autowired
    private GroupListRepository groupListRepository;


    @Transactional
    public List<UserGroup> getGroupsList(){
        return groupsRepository.findAll();
    }

    @Transactional
    public List<GroupList> getGroupForUser(User authorizedUser){
        return  authorizedUser.getGroups();
    }

    @Transactional
    public boolean isManager(long userId,int groupId){
        GroupList list=groupListRepository.findOne(new GroupListPK(userId,groupId));
        if (list==null)
            return false;
        return list.isManager();
    }

    public List<GroupList> getGroupsListByUserGroupId(Integer id){
        return groupListRepository.findByUserGroupGroupId(id);
    }

    public boolean isExistUserGroup(Integer id){
        return groupsRepository.findOne(id) != null;
    }

    public List<GroupList> getGroupsListWhichNotContainsUserGroupId(Integer id){
        return groupListRepository.findByNotContainsUserGroupGroupId(id);
    }

    public void removeGroup(Integer id){
        groupsRepository.delete(id);
    }

    @Transactional
    public void updateGroupWithUpdateGroupModel(UpdateGroupModel updateGroupModel){
        UserGroup userGroup = groupsRepository.findOne(updateGroupModel.getId());
        userGroup = updateGroupModel.changeGroup(userGroup);
        groupsRepository.save(userGroup);
    }

    @Transactional
    public  void createGroupWithCreateGroupModel(CreateGroupModel createGroupModel){
        UserGroup userGroup = new UserGroup();
        userGroup.setName(createGroupModel.getName());
        userGroup.setDiscount(Float.parseFloat(createGroupModel.getDiscount()));
        userGroup.setGroups(null);
        groupsRepository.save(userGroup);
    }
}
