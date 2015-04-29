/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamd.taxi.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * @author Олег
 */
@Entity
@Table(name = "group_list", schema = "public")
public class GroupList implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected GroupListPK groupListPK;

    @Column(name = "is_manager")
    private boolean isManager;

    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;

    @JoinColumn(name = "group_id", referencedColumnName = "group_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private UserGroup userGroup;

    public GroupList() {
    }

    public GroupList(GroupListPK groupListPK) {
        this.groupListPK = groupListPK;
    }

    public GroupList(GroupListPK groupListPK, boolean isManager) {
        this.groupListPK = groupListPK;
        this.isManager = isManager;
    }

    public GroupList(long userId, int groupId) {
        this.groupListPK = new GroupListPK(userId, groupId);
    }

    public GroupListPK getGroupListPK() {
        return groupListPK;
    }

    public void setGroupListPK(GroupListPK groupListPK) {
        this.groupListPK = groupListPK;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setIsManager(boolean isManager) {
        this.isManager = isManager;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupListPK != null ? groupListPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GroupList)) {
            return false;
        }
        GroupList other = (GroupList) object;
        return !((this.groupListPK == null && other.groupListPK != null) || (this.groupListPK != null && !this.groupListPK.equals(other.groupListPK)));
    }

    @Override
    public String toString() {
        return "com.teamd.taxi.entity.GroupList[ groupListPK=" + groupListPK + " ]";
    }

}
