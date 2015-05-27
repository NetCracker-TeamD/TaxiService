package com.teamd.taxi.service;

import com.teamd.taxi.entity.GroupList;
import com.teamd.taxi.entity.GroupListPK;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.entity.UserGroup;
import com.teamd.taxi.exception.ItemNotFoundException;
import com.teamd.taxi.models.admin.*;
import com.teamd.taxi.persistence.repository.GroupListRepository;
import com.teamd.taxi.persistence.repository.GroupsRepository;
import com.teamd.taxi.persistence.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GroupsService {

    @Autowired
    private GroupsRepository groupsRepository;
    @Autowired
    private GroupListRepository groupListRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<UserGroup> getGroupsList() {
        return groupsRepository.findAll();
    }

    @Transactional
    public List<GroupList> getGroupForUser(User authorizedUser) {
        return authorizedUser.getGroups();
    }

    @Transactional
    public boolean isManager(long userId, int groupId) throws ItemNotFoundException {
        GroupList list = groupListRepository.findOne(new GroupListPK(userId, groupId));
        if (list == null) {
            throw new ItemNotFoundException("group[" + groupId + "] not found");
        }
        return list.isManager();
    }

    public List<GroupList> getGroupsListByUserGroupId(Integer id) {
        return groupListRepository.findByUserGroupGroupId(id);
    }

    public boolean isExistUserGroup(Integer id) {
        return groupsRepository.findOne(id) != null;
    }

    public List<User> getUsersNotFromGroup(Integer id) {
        return userRepository.getUserNotFromGroup(id);
    }

    public void removeGroup(Integer id) {
        groupsRepository.delete(id);
    }

    @Transactional
    public void updateGroupWithUpdateGroupModel(UpdateGroupModel updateGroupModel) {
        UserGroup userGroup = groupsRepository.findOne(updateGroupModel.getId());
        userGroup = updateGroupModel.changeGroup(userGroup);
        groupsRepository.save(userGroup);
    }

    @Transactional
    public void createGroupWithCreateGroupModel(CreateGroupModel createGroupModel) {
        UserGroup userGroup = new UserGroup();
        userGroup.setName(createGroupModel.getName());
        userGroup.setDiscount(Float.parseFloat(createGroupModel.getDiscount())/100.0f);
        userGroup.setGroups(null);
        groupsRepository.save(userGroup);
    }

    @Transactional
    public void addUsersToGroupWithAddUsersGroupModel(AddUsersGroupModel addUsersGroupModel) {
        UserGroup userGroup = groupsRepository.findOne(addUsersGroupModel.getGroupId());
        List<GroupList> groupLists = userGroup.getGroups();

        for (Integer userId : addUsersGroupModel.getUsers()) {
            groupLists.add(new GroupList(userId, addUsersGroupModel.getGroupId()));
        }
        userGroup.setGroups(groupLists);
    }

    @Transactional
    public void deleteUsersFromGroupWithDeleteUsersFromGroupModel(DeleteUsersFromGroupModel deleteUsersFromGroupModel) {
        UserGroup userGroup = groupsRepository.findOne(deleteUsersFromGroupModel.getGroupId());
        List<GroupList> groupLists = userGroup.getGroups();

        for (Integer userId : deleteUsersFromGroupModel.getUsers()) {
            groupListRepository.delete(new GroupListPK(userId, deleteUsersFromGroupModel.getGroupId()));
        }
    }

    @Transactional
    public void updateManagerStatus(ChangeManagerStatusModel changeManagerStatusModel) {
        List<GroupList> groupLists = new ArrayList<>();
        for (Map<Integer, Boolean> userIdAndBoolean : changeManagerStatusModel.getUsers()) {
            for (Integer userId : userIdAndBoolean.keySet()) {
                groupLists.add(new GroupList(new GroupListPK(userId, changeManagerStatusModel.getGroupId()), userIdAndBoolean.get(userId)));
            }
        }

        groupListRepository.save(groupLists);
    }
}
