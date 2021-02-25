package com.revature.dtos;

import java.sql.Blob;
import java.util.Objects;
/**
 * A DTO to facilitate easy transferring reimbursements
 */
public class RbDTO {
    private Integer id;
    private Double amount;
    private String submitted;
    private String resolved;
    private byte[] image;
    private String authorName;
    private String resolverName;
    private String status;
    private String type;
    private String description;

    public RbDTO() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getSubmitted() {
        return submitted;
    }

    public void setSubmitted(String submitted) {
        this.submitted = submitted;
    }

    public String getResolved() {
        return resolved;
    }

    public void setResolved(String resolved) {
        this.resolved = resolved;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getResolverName() {
        return resolverName;
    }

    public void setResolverName(String resolverName) {
        this.resolverName = resolverName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RbDTO)) return false;
        RbDTO rbDTO = (RbDTO) o;
        return Objects.equals(getId(), rbDTO.getId()) &&
                Objects.equals(getAmount(), rbDTO.getAmount()) &&
                Objects.equals(getSubmitted(), rbDTO.getSubmitted()) &&
                Objects.equals(getResolved(), rbDTO.getResolved()) &&
                Objects.equals(getImage(), rbDTO.getImage()) &&
                Objects.equals(getAuthorName(), rbDTO.getAuthorName()) &&
                Objects.equals(getResolverName(), rbDTO.getResolverName()) &&
                Objects.equals(getStatus(), rbDTO.getStatus()) &&
                Objects.equals(getType(), rbDTO.getType()) &&
                Objects.equals(getDescription(), rbDTO.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAmount(), getSubmitted(), getResolved(), getImage(), getAuthorName(), getResolverName(), getStatus(), getType(), getDescription());
    }

    @Override
    public String toString() {
        return "RbDTO{" +
                "id=" + id +
                ", amount=" + amount +
                ", submitted='" + submitted + '\'' +
                ", resolved='" + resolved + '\'' +
                ", image=" + image +
                ", authorName='" + authorName + '\'' +
                ", resolverName='" + resolverName + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
