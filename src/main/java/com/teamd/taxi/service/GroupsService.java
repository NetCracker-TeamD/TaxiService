package com.teamd.taxi.service;

import com.teamd.taxi.entity.UserGroup;
import com.teamd.taxi.persistence.repository.GroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupsService {

    @Autowired
    private GroupsRepository groupsRepository;

    @Transactional
    public List<UserGroup> getGroupsList(){
        return groupsRepository.findAll();
    }
}
