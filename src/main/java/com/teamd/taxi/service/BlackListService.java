package com.teamd.taxi.service;

import com.teamd.taxi.entity.BlackListItem;
import com.teamd.taxi.persistence.repository.BlackListItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Олег on 24.05.2015.
 */
@Service
public class BlackListService {

    public static final int REFUSED_ORDERS_LIMIT = 3;

    @Autowired
    private BlackListItemRepository blackListItemRepository;

    public List<BlackListItem> findByUserId(long userId) {
        return blackListItemRepository.findByUserId(userId);
    }

    public Long countByUserId(long userId) {
        return blackListItemRepository.countByUserId(userId);
    }

    public BlackListItem save(BlackListItem item) {
        return blackListItemRepository.save(item);
    }
}
