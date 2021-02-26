package com.revature.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

//@Entity
//@Table(name="ERS_REIMBURSEMENT_TYPES")
public enum ReimbursementType {
    // values declared within enums are constants and are comma separated
    //@Column(name = "REIMB_TYPE", nullable = false, unique = true)
    DEFAULT("Default"),
    LODGING("Lodging"),
    TRAVEL("Travel"),
    FOOD("Food"),
    OTHER("Other");

    private String reimbursementType;

    // enum constructors are implicitly private
    ReimbursementType(String name) {
        this.reimbursementType = name;
    }

    public static ReimbursementType getByName(String name) {

        for (ReimbursementType role : ReimbursementType.values()) {
            if (role.reimbursementType.equals(name)) {
                return role;
            }
        }

        return OTHER;

    }

    public static ReimbursementType getByNumber(Integer number){
        switch (number){
            case 1:
                return LODGING;
            case 2:
                return TRAVEL;
            case 3:
                return FOOD;
            case 4:
                return OTHER;
        }
        return OTHER;
    }

    @Override
    public String toString() {
        return reimbursementType;
    }

}
