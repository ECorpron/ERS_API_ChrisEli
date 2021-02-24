package com.revature.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

//@Entity
//@Table(name = "ERS_USER_ROLES")
public enum Role {

    //@Column(name = "ROLE_NAME", nullable = false, unique = true)
    ADMIN("Admin"),
    FINANCE_MANAGER("Finance Manager"),
    EMPLOYEE("Employee"),
    DELETED("Deleted");

    private String roleName;

    Role(String name) {
        this.roleName = name;
    }

    public static Role getByName(String name) {

        for (Role role : Role.values()) {
            if (role.roleName.equals(name)) {
                return role;
            }
        }
        return EMPLOYEE;
    }

    @Override
    public String toString() {
        return roleName;
    }

}
