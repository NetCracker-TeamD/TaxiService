package com.teamd.taxi.service;

import com.teamd.taxi.entity.BlackListItem;
import com.teamd.taxi.persistence.repository.BlackListItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Олег on 24.05.2015.
 */
@Service
public class BlackListService {

    @Autowired
    private BlackListItemRepository blackListItemRepository;

    public BlackListItem findByUserId(long userId) {
        return blackListItemRepository.findByUserId(userId);
    }
}
