package com.techwiz.petcare.service.criteria;

import com.techwiz.petcare.domain.enumeration.Gender;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.techwiz.petcare.domain.Pet} entity. This class is used
 * in {@link com.techwiz.petcare.web.rest.PetResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /pets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PetCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Gender
     */
    public static class GenderFilter extends Filter<Gender> {

        public GenderFilter() {}

        public GenderFilter(GenderFilter filter) {
            super(filter);
        }

        @Override
        public GenderFilter copy() {
            return new GenderFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter ownerId;

    private StringFilter name;

    private StringFilter species;

    private StringFilter breed;

    private IntegerFilter age;

    private GenderFilter gender;

    private StringFilter photoUrl;

    private InstantFilter createdAt;

    private Boolean distinct;

    public PetCriteria() {}

    public PetCriteria(PetCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.ownerId = other.optionalOwnerId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.species = other.optionalSpecies().map(StringFilter::copy).orElse(null);
        this.breed = other.optionalBreed().map(StringFilter::copy).orElse(null);
        this.age = other.optionalAge().map(IntegerFilter::copy).orElse(null);
        this.gender = other.optionalGender().map(GenderFilter::copy).orElse(null);
        this.photoUrl = other.optionalPhotoUrl().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PetCriteria copy() {
        return new PetCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getSpecies() {
        return species;
    }

    public Optional<StringFilter> optionalSpecies() {
        return Optional.ofNullable(species);
    }

    public StringFilter species() {
        if (species == null) {
            setSpecies(new StringFilter());
        }
        return species;
    }

    public void setSpecies(StringFilter species) {
        this.species = species;
    }

    public StringFilter getBreed() {
        return breed;
    }

    public Optional<StringFilter> optionalBreed() {
        return Optional.ofNullable(breed);
    }

    public StringFilter breed() {
        if (breed == null) {
            setBreed(new StringFilter());
        }
        return breed;
    }

    public void setBreed(StringFilter breed) {
        this.breed = breed;
    }

    public IntegerFilter getAge() {
        return age;
    }

    public Optional<IntegerFilter> optionalAge() {
        return Optional.ofNullable(age);
    }

    public IntegerFilter age() {
        if (age == null) {
            setAge(new IntegerFilter());
        }
        return age;
    }

    public void setAge(IntegerFilter age) {
        this.age = age;
    }

    public GenderFilter getGender() {
        return gender;
    }

    public Optional<GenderFilter> optionalGender() {
        return Optional.ofNullable(gender);
    }

    public GenderFilter gender() {
        if (gender == null) {
            setGender(new GenderFilter());
        }
        return gender;
    }

    public void setGender(GenderFilter gender) {
        this.gender = gender;
    }

    public StringFilter getPhotoUrl() {
        return photoUrl;
    }

    public Optional<StringFilter> optionalPhotoUrl() {
        return Optional.ofNullable(photoUrl);
    }

    public StringFilter photoUrl() {
        if (photoUrl == null) {
            setPhotoUrl(new StringFilter());
        }
        return photoUrl;
    }

    public void setPhotoUrl(StringFilter photoUrl) {
        this.photoUrl = photoUrl;
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
        final PetCriteria that = (PetCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(ownerId, that.ownerId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(species, that.species) &&
            Objects.equals(breed, that.breed) &&
            Objects.equals(age, that.age) &&
            Objects.equals(gender, that.gender) &&
            Objects.equals(photoUrl, that.photoUrl) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, name, species, breed, age, gender, photoUrl, createdAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PetCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOwnerId().map(f -> "ownerId=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalSpecies().map(f -> "species=" + f + ", ").orElse("") +
            optionalBreed().map(f -> "breed=" + f + ", ").orElse("") +
            optionalAge().map(f -> "age=" + f + ", ").orElse("") +
            optionalGender().map(f -> "gender=" + f + ", ").orElse("") +
            optionalPhotoUrl().map(f -> "photoUrl=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
