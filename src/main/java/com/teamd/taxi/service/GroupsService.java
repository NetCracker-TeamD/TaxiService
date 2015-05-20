package com.teamd.taxi.service;

import com.teamd.taxi.entity.GroupList;
import com.teamd.taxi.entity.GroupListPK;
import com.teamd.taxi.entity.User;
import com.teamd.taxi.entity.UserGroup;
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
}
