package com.revature.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ERS_REIMBURSEMENT_STATUSES")
public enum ReimbursementStatus {
    // values declared within enums are constants and are comma separated
    @Column(name = "REIMB_STATUS", nullable = false, unique = true)
    PENDING("Pending"),
    APPROVED("Approved"),
    DENIED("Denied"),
    CLOSED("Closed");

    private String reimbursementStaus;

    // enum constructors are implicitly private
    ReimbursementStatus(String name) {
        this.reimbursementStaus = name;
    }

    public static ReimbursementStatus getByName(String name) {

        for (ReimbursementStatus role : ReimbursementStatus.values()) {
            if (role.reimbursementStaus.equals(name)) {
                return role;
            }
        }

        return PENDING;

        // functional implementation of the above code
//        return Arrays.stream(Role.values())
//                .filter(role -> role.roleName.equals(name))
//                .findFirst()
//                .orElse(LOCKED);

    }

    public static ReimbursementStatus getByNumber(Integer number){
        switch (number){
            case 1:
                return PENDING;
            case 2:
                return APPROVED;
            case 3:
                return DENIED;
        }
        return CLOSED;
    }

    @Override
    public String toString() {
        return reimbursementStaus;
    }

}
