package com.revature.dtos;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

/**
 * A DTO to facilitate simple approving and denying of reimbursements
 */
public class ApproveDeny {
    private Integer status;
    private Integer id;

    /**
     * Sets args to default values
     */
    public ApproveDeny() {
        super();
    }

    /**
     * Sets status and id
     * @param status the status to be set to
     * @param id the id of the reimbursement being changed
     */
    public ApproveDeny(Integer status, Integer id) {
        this.status = status;
        this.id = id;
    }

    @Enumerated(EnumType.ORDINAL)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApproveDeny)) return false;
        ApproveDeny that = (ApproveDeny) o;
        return Objects.equals(getStatus(), that.getStatus()) &&
                Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStatus(), getId());
    }

    @Override
    public String toString() {
        return "ApproveDeny{" +
                "status=" + status +
                ", id=" + id +
                '}';
    }
}
