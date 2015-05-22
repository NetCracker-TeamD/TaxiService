package com.teamd.taxi.models.admin;

import java.util.List;

/**
 * Created by Anatoliy on 22.05.2015.
 */
public class DeleteUsersFromGroupModel {
    private Integer groupId;

    private List<Integer> users;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }


    @Override
    public String toString() {
        return "DeleteUsersFromGroupModel{" +
                "groupId=" + groupId +
                ", users=" + users +
                '}';
    }
}
