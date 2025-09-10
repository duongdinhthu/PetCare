package com.techwiz.petcare.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.techwiz.petcare.domain.HealthRecord} entity. This class is used
 * in {@link com.techwiz.petcare.web.rest.HealthRecordResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /health-records?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HealthRecordCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter petId;

    private LongFilter vetId;

    private LongFilter apptId;

    private InstantFilter createdAt;

    private Boolean distinct;

    public HealthRecordCriteria() {}

    public HealthRecordCriteria(HealthRecordCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.petId = other.optionalPetId().map(LongFilter::copy).orElse(null);
        this.vetId = other.optionalVetId().map(LongFilter::copy).orElse(null);
        this.apptId = other.optionalApptId().map(LongFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public HealthRecordCriteria copy() {
        return new HealthRecordCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getPetId() {
        return petId;
    }

    public Optional<LongFilter> optionalPetId() {
        return Optional.ofNullable(petId);
    }

    public LongFilter petId() {
        if (petId == null) {
            setPetId(new LongFilter());
        }
        return petId;
    }

    public void setPetId(LongFilter petId) {
        this.petId = petId;
    }

    public LongFilter getVetId() {
        return vetId;
    }

    public Optional<LongFilter> optionalVetId() {
        return Optional.ofNullable(vetId);
    }

    public LongFilter vetId() {
        if (vetId == null) {
            setVetId(new LongFilter());
        }
        return vetId;
    }

    public void setVetId(LongFilter vetId) {
        this.vetId = vetId;
    }

    public LongFilter getApptId() {
        return apptId;
    }

    public Optional<LongFilter> optionalApptId() {
        return Optional.ofNullable(apptId);
    }

    public LongFilter apptId() {
        if (apptId == null) {
            setApptId(new LongFilter());
        }
        return apptId;
    }

    public void setApptId(LongFilter apptId) {
        this.apptId = apptId;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HealthRecordCriteria that = (HealthRecordCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(petId, that.petId) &&
            Objects.equals(vetId, that.vetId) &&
            Objects.equals(apptId, that.apptId) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, petId, vetId, apptId, createdAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HealthRecordCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPetId().map(f -> "petId=" + f + ", ").orElse("") +
            optionalVetId().map(f -> "vetId=" + f + ", ").orElse("") +
            optionalApptId().map(f -> "apptId=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
