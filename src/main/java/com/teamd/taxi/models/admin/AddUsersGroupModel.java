package com.teamd.taxi.models.admin;

import com.teamd.taxi.validation.ExistingGroupId;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by Anatoliy on 22.05.2015.
 */
public class AddUsersGroupModel {

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
        return "AddUsersGroupModel{" +
                "groupId=" + groupId +
                ", users=" + users +
                '}';
    }
}
