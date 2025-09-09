package com.techwiz.petcare.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A HealthRecord.
 */
@Entity
@Table(name = "health_record")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HealthRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @NotNull
    @Column(name = "vet_id", nullable = false)
    private Long vetId;

    @NotNull
    @Column(name = "appt_id", nullable = false)
    private Long apptId;

    @Lob
    @Column(name = "diagnosis")
    private String diagnosis;

    @Lob
    @Column(name = "treatment")
    private String treatment;

    @Lob
    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private Instant createdAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HealthRecord id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPetId() {
        return this.petId;
    }

    public HealthRecord petId(Long petId) {
        this.setPetId(petId);
        return this;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getVetId() {
        return this.vetId;
    }

    public HealthRecord vetId(Long vetId) {
        this.setVetId(vetId);
        return this;
    }

    public void setVetId(Long vetId) {
        this.vetId = vetId;
    }

    public Long getApptId() {
        return this.apptId;
    }

    public HealthRecord apptId(Long apptId) {
        this.setApptId(apptId);
        return this;
    }

    public void setApptId(Long apptId) {
        this.apptId = apptId;
    }

    public String getDiagnosis() {
        return this.diagnosis;
    }

    public HealthRecord diagnosis(String diagnosis) {
        this.setDiagnosis(diagnosis);
        return this;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return this.treatment;
    }

    public HealthRecord treatment(String treatment) {
        this.setTreatment(treatment);
        return this;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getNotes() {
        return this.notes;
    }

    public HealthRecord notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public HealthRecord createdAt(Instant createdAt) {
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
        if (!(o instanceof HealthRecord)) {
            return false;
        }
        return getId() != null && getId().equals(((HealthRecord) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HealthRecord{" +
            "id=" + getId() +
            ", petId=" + getPetId() +
            ", vetId=" + getVetId() +
            ", apptId=" + getApptId() +
            ", diagnosis='" + getDiagnosis() + "'" +
            ", treatment='" + getTreatment() + "'" +
            ", notes='" + getNotes() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
