/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamd.taxi.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Олег
 */
@Embeddable
public class GroupListPK implements Serializable {
    @Column(name = "user_id")
    private long userId;

    @Column(name = "group_id")
    private int groupId;

    public GroupListPK() {
    }

    public GroupListPK(long userId, int groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) userId;
        hash += groupId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GroupListPK)) {
            return false;
        }
        GroupListPK other = (GroupListPK) object;
        if (this.userId != other.userId) {
            return false;
        }
        return this.groupId == other.groupId;
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.GroupListPK[ userId=" + userId + ", groupId=" + groupId + " ]";
    }

}
