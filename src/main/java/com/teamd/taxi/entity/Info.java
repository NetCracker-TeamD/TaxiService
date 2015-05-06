package com.teamd.taxi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "info", schema = "public")
public class Info implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "info_name")
    private String name;

    @Column(name = "info_value")
    private String value;

    public Info() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
