package com.teamd.taxi.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created on 04-May-15.
 *
 * @author Nazar Dub
 */
@Service
public class AdminPagesUtil {

    public ArrayList<Integer> getPagination(int currentPage, int lastPage) {
        ArrayList<Integer> pageList = new ArrayList<>();
        lastPage--;
        int correctingVal = 0;


        if (currentPage >= lastPage) {
            correctingVal = lastPage - currentPage - 1;
        }

        if (currentPage <= 4) {
            correctingVal = 3 - currentPage;
        }

        for (int i = 0; i < 5; i++) {
            int scrollPage = currentPage + i - 3 + correctingVal;
            if (scrollPage > lastPage) {
                break;
            }
            pageList.add(scrollPage);
        }
        return pageList;
    }
}
