package com.teamd.taxi.service;

import com.teamd.taxi.models.PagingLink;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class PagingLinksGenerator {

    private static final int LINKS_GROUP_SIZE = 3;

    private static final String PAGE_NUM_PARAM = "page";

    private static final String DELIMITER = "...";

    /**
     * @param page    page links list is generated for.
     * @param builder must be initialized with appropriate request URI and common params.
     *                Page number param will be added to it and generated URI
     *                will be attached to link.
     * @return list of paging links associated with current page request
     */
    public List<PagingLink> generateLinks(Page<?> page, UriComponentsBuilder builder) {
        // because of Page<?> is 0-indexed
        // but we wanna to links be 1-indexed
        final int currentPageNum = page.getNumber() + 1;
        final int pagesCount = page.getTotalPages();

        if (currentPageNum > pagesCount) {
            return null;
        }
        List<PagingLink> links = new ArrayList<>();
        builder.queryParam(PAGE_NUM_PARAM, 0);
        //links list
        /* Generally, we have 3 groups of links
         1, 2, 3 ... x - 1, x, x + 1 ... y - 2, y - 1, y
         where x - number of current page,  y - number of the last page
         */
        //********group of the current page ***********
        int onTheSides = (LINKS_GROUP_SIZE - 1) / 2;
        int currentGroupStart = Math.max(Math.min(currentPageNum - onTheSides, pagesCount - LINKS_GROUP_SIZE + 1), 1);
        int currentGroupEnd = Math.min(Math.max(LINKS_GROUP_SIZE, currentPageNum + onTheSides), pagesCount);
        for (int pageNumber = currentGroupStart; pageNumber <= currentGroupEnd; pageNumber++) {
            links.add(new PagingLink(
                            Integer.toString(pageNumber),
                            builder.replaceQueryParam(PAGE_NUM_PARAM, pageNumber).build().encode().toUriString(),
                            pageNumber == currentPageNum, //is active?
                            false, //is disabled
                            pageNumber
                    )
            );
        }
        //********* first delimiter *********
        if (currentGroupStart > LINKS_GROUP_SIZE + 1) {
            links.add(0, new PagingLink(DELIMITER, "#", false, true, 0));
        }
        //*********** start group ***************
        int firstGroupStart = 1;
        int firstGroupEnd = Math.min(LINKS_GROUP_SIZE, currentGroupStart - 1);
        for (int i = 0, pageNumber = firstGroupStart; pageNumber <= firstGroupEnd; pageNumber++, i++) {
            links.add(i, new PagingLink(
                            Integer.toString(pageNumber),
                            builder.replaceQueryParam(PAGE_NUM_PARAM, pageNumber).build().encode().toUriString(),
                            false, //is active?
                            false, //is disabled?
                            pageNumber
                    )
            );
        }
        //***********second delimiter******
        if (currentGroupEnd < pagesCount - LINKS_GROUP_SIZE) {
            links.add(new PagingLink(DELIMITER, "#", false, true, 0));
        }
        //*********final group**********
        int lastGroupStart = Math.max(pagesCount - LINKS_GROUP_SIZE, currentGroupEnd) + 1;
        int lastGroupEnd = pagesCount;
        for (int pageNumber = lastGroupStart; pageNumber <= lastGroupEnd; pageNumber++) {
            links.add(new PagingLink(
                            Integer.toString(pageNumber),
                            builder.replaceQueryParam(PAGE_NUM_PARAM, pageNumber).build().encode().toUriString(),
                            false, //is active?
                            false, //is enabled?
                            pageNumber
                    )
            );
        }
        return links;
    }
}
