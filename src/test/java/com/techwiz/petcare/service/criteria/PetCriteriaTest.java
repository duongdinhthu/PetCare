package com.techwiz.petcare.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PetCriteriaTest {

    @Test
    void newPetCriteriaHasAllFiltersNullTest() {
        var petCriteria = new PetCriteria();
        assertThat(petCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void petCriteriaFluentMethodsCreatesFiltersTest() {
        var petCriteria = new PetCriteria();

        setAllFilters(petCriteria);

        assertThat(petCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void petCriteriaCopyCreatesNullFilterTest() {
        var petCriteria = new PetCriteria();
        var copy = petCriteria.copy();

        assertThat(petCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(petCriteria)
        );
    }

    @Test
    void petCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var petCriteria = new PetCriteria();
        setAllFilters(petCriteria);

        var copy = petCriteria.copy();

        assertThat(petCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(petCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var petCriteria = new PetCriteria();

        assertThat(petCriteria).hasToString("PetCriteria{}");
    }

    private static void setAllFilters(PetCriteria petCriteria) {
        petCriteria.id();
        petCriteria.ownerId();
        petCriteria.name();
        petCriteria.species();
        petCriteria.breed();
        petCriteria.age();
        petCriteria.gender();
        petCriteria.photoUrl();
        petCriteria.createdAt();
        petCriteria.distinct();
    }

    private static Condition<PetCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOwnerId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getSpecies()) &&
                condition.apply(criteria.getBreed()) &&
                condition.apply(criteria.getAge()) &&
                condition.apply(criteria.getGender()) &&
                condition.apply(criteria.getPhotoUrl()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PetCriteria> copyFiltersAre(PetCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getOwnerId(), copy.getOwnerId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getSpecies(), copy.getSpecies()) &&
                condition.apply(criteria.getBreed(), copy.getBreed()) &&
                condition.apply(criteria.getAge(), copy.getAge()) &&
                condition.apply(criteria.getGender(), copy.getGender()) &&
                condition.apply(criteria.getPhotoUrl(), copy.getPhotoUrl()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
