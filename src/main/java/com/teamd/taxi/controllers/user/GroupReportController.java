package com.teamd.taxi.controllers.user;

import com.teamd.taxi.entity.UserGroup;
import com.teamd.taxi.service.GroupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("/user")
public class GroupReportController {

    @Autowired
    GroupsService groupsService;

    @RequestMapping(value = "/groupList")
    @ResponseBody
    public List<Map<String, Object>> getAllGroups() {
        List<Map<String, Object>> groupsList = new ArrayList<>();
        for (UserGroup groups : groupsService.getGroupsList()) {
            Map<String, Object> group = new TreeMap<>();
            group.put("Group name", groups.getName());
            group.put("Discount", groups.getDiscount());
            groupsList.add(group);
        }
        return groupsList;
    }

    @RequestMapping("/group")
    public ModelAndView viewStatistic(Model model, HttpServletRequest request) {
        return new ModelAndView("user/group_view");
    }
}
