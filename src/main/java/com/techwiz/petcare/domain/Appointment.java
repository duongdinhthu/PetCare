package com.techwiz.petcare.domain;

import com.techwiz.petcare.domain.enumeration.ApptStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Appointment.
 */
@Entity
@Table(name = "appointment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @NotNull
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @NotNull
    @Column(name = "vet_id", nullable = false)
    private Long vetId;

    @NotNull
    @Column(name = "appt_time", nullable = false)
    private ZonedDateTime apptTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApptStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Appointment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPetId() {
        return this.petId;
    }

    public Appointment petId(Long petId) {
        this.setPetId(petId);
        return this;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public Appointment ownerId(Long ownerId) {
        this.setOwnerId(ownerId);
        return this;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getVetId() {
        return this.vetId;
    }

    public Appointment vetId(Long vetId) {
        this.setVetId(vetId);
        return this;
    }

    public void setVetId(Long vetId) {
        this.vetId = vetId;
    }

    public ZonedDateTime getApptTime() {
        return this.apptTime;
    }

    public Appointment apptTime(ZonedDateTime apptTime) {
        this.setApptTime(apptTime);
        return this;
    }

    public void setApptTime(ZonedDateTime apptTime) {
        this.apptTime = apptTime;
    }

    public ApptStatus getStatus() {
        return this.status;
    }

    public Appointment status(ApptStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ApptStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Appointment createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appointment)) {
            return false;
        }
        return getId() != null && getId().equals(((Appointment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Appointment{" +
            "id=" + getId() +
            ", petId=" + getPetId() +
            ", ownerId=" + getOwnerId() +
            ", vetId=" + getVetId() +
            ", apptTime='" + getApptTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
