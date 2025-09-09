package com.techwiz.petcare.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.techwiz.petcare.domain.HealthRecord} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HealthRecordDTO implements Serializable {

    private Long id;

    @NotNull
    private Long petId;

    @NotNull
    private Long vetId;

    @NotNull
    private Long apptId;

    @Lob
    private String diagnosis;

    @Lob
    private String treatment;

    @Lob
    private String notes;

    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public Long getVetId() {
        return vetId;
    }

    public void setVetId(Long vetId) {
        this.vetId = vetId;
    }

    public Long getApptId() {
        return apptId;
    }

    public void setApptId(Long apptId) {
        this.apptId = apptId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HealthRecordDTO)) {
            return false;
        }

        HealthRecordDTO healthRecordDTO = (HealthRecordDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, healthRecordDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HealthRecordDTO{" +
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
