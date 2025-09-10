package com.techwiz.petcare.service.criteria;

import com.techwiz.petcare.domain.enumeration.ApptStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.techwiz.petcare.domain.Appointment} entity. This class is used
 * in {@link com.techwiz.petcare.web.rest.AppointmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /appointments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppointmentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ApptStatus
     */
    public static class ApptStatusFilter extends Filter<ApptStatus> {

        public ApptStatusFilter() {}

        public ApptStatusFilter(ApptStatusFilter filter) {
            super(filter);
        }

        @Override
        public ApptStatusFilter copy() {
            return new ApptStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter petId;

    private LongFilter ownerId;

    private LongFilter vetId;

    private ZonedDateTimeFilter apptTime;

    private ApptStatusFilter status;

    private InstantFilter createdAt;

    private Boolean distinct;

    public AppointmentCriteria() {}

    public AppointmentCriteria(AppointmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.petId = other.optionalPetId().map(LongFilter::copy).orElse(null);
        this.ownerId = other.optionalOwnerId().map(LongFilter::copy).orElse(null);
        this.vetId = other.optionalVetId().map(LongFilter::copy).orElse(null);
        this.apptTime = other.optionalApptTime().map(ZonedDateTimeFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ApptStatusFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AppointmentCriteria copy() {
        return new AppointmentCriteria(this);
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

    public LongFilter getOwnerId() {
        return ownerId;
    }

    public Optional<LongFilter> optionalOwnerId() {
        return Optional.ofNullable(ownerId);
    }

    public LongFilter ownerId() {
        if (ownerId == null) {
            setOwnerId(new LongFilter());
        }
        return ownerId;
    }

    public void setOwnerId(LongFilter ownerId) {
        this.ownerId = ownerId;
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

    public ZonedDateTimeFilter getApptTime() {
        return apptTime;
    }

    public Optional<ZonedDateTimeFilter> optionalApptTime() {
        return Optional.ofNullable(apptTime);
    }

    public ZonedDateTimeFilter apptTime() {
        if (apptTime == null) {
            setApptTime(new ZonedDateTimeFilter());
        }
        return apptTime;
    }

    public void setApptTime(ZonedDateTimeFilter apptTime) {
        this.apptTime = apptTime;
    }

    public ApptStatusFilter getStatus() {
        return status;
    }

    public Optional<ApptStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ApptStatusFilter status() {
        if (status == null) {
            setStatus(new ApptStatusFilter());
        }
        return status;
    }

    public void setStatus(ApptStatusFilter status) {
        this.status = status;
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
        final AppointmentCriteria that = (AppointmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(petId, that.petId) &&
            Objects.equals(ownerId, that.ownerId) &&
            Objects.equals(vetId, that.vetId) &&
            Objects.equals(apptTime, that.apptTime) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, petId, ownerId, vetId, apptTime, status, createdAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppointmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPetId().map(f -> "petId=" + f + ", ").orElse("") +
            optionalOwnerId().map(f -> "ownerId=" + f + ", ").orElse("") +
            optionalVetId().map(f -> "vetId=" + f + ", ").orElse("") +
            optionalApptTime().map(f -> "apptTime=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
