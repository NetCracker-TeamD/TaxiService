package com.teamd.taxi.models.admin;

import java.util.List;
import java.util.Map;

/**
 * Created by Anatoliy on 22.05.2015.
 */
public class ChangeManagerStatusModel {

    private Integer groupId;

    private List<Map<Integer, Boolean>> users;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public List<Map<Integer, Boolean>> getUsers() {
        return users;
    }

    public void setUsers(List<Map<Integer, Boolean>> users) {
        this.users = users;
    }



    @Override
    public String toString() {
        return "ChangeManagerStatusModel{" +
                "groupId=" + groupId +
                ", users=" + users +
                '}';
    }
}
