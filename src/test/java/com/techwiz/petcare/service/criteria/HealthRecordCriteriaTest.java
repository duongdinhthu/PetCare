package com.techwiz.petcare.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class HealthRecordCriteriaTest {

    @Test
    void newHealthRecordCriteriaHasAllFiltersNullTest() {
        var healthRecordCriteria = new HealthRecordCriteria();
        assertThat(healthRecordCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void healthRecordCriteriaFluentMethodsCreatesFiltersTest() {
        var healthRecordCriteria = new HealthRecordCriteria();

        setAllFilters(healthRecordCriteria);

        assertThat(healthRecordCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void healthRecordCriteriaCopyCreatesNullFilterTest() {
        var healthRecordCriteria = new HealthRecordCriteria();
        var copy = healthRecordCriteria.copy();

        assertThat(healthRecordCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(healthRecordCriteria)
        );
    }

    @Test
    void healthRecordCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var healthRecordCriteria = new HealthRecordCriteria();
        setAllFilters(healthRecordCriteria);

        var copy = healthRecordCriteria.copy();

        assertThat(healthRecordCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(healthRecordCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var healthRecordCriteria = new HealthRecordCriteria();

        assertThat(healthRecordCriteria).hasToString("HealthRecordCriteria{}");
    }

    private static void setAllFilters(HealthRecordCriteria healthRecordCriteria) {
        healthRecordCriteria.id();
        healthRecordCriteria.petId();
        healthRecordCriteria.vetId();
        healthRecordCriteria.apptId();
        healthRecordCriteria.createdAt();
        healthRecordCriteria.distinct();
    }

    private static Condition<HealthRecordCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPetId()) &&
                condition.apply(criteria.getVetId()) &&
                condition.apply(criteria.getApptId()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<HealthRecordCriteria> copyFiltersAre(
        HealthRecordCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPetId(), copy.getPetId()) &&
                condition.apply(criteria.getVetId(), copy.getVetId()) &&
                condition.apply(criteria.getApptId(), copy.getApptId()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
