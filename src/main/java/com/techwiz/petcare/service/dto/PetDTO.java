package com.techwiz.petcare.service.dto;

import com.techwiz.petcare.domain.enumeration.Gender;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.techwiz.petcare.domain.Pet} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PetDTO implements Serializable {

    private Long id;

    @NotNull
    private Long ownerId;

    @NotNull
    @Size(max = 100)
    private String name;

    @Size(max = 50)
    private String species;

    @Size(max = 50)
    private String breed;

    private Integer age;

    private Gender gender;

    @Size(max = 255)
    private String photoUrl;

    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
        if (!(o instanceof PetDTO)) {
            return false;
        }

        PetDTO petDTO = (PetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, petDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PetDTO{" +
            "id=" + getId() +
            ", ownerId=" + getOwnerId() +
            ", name='" + getName() + "'" +
            ", species='" + getSpecies() + "'" +
            ", breed='" + getBreed() + "'" +
            ", age=" + getAge() +
            ", gender='" + getGender() + "'" +
            ", photoUrl='" + getPhotoUrl() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
