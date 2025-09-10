package com.techwiz.petcare.service.mapper;

import static com.techwiz.petcare.domain.HealthRecordAsserts.*;
import static com.techwiz.petcare.domain.HealthRecordTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HealthRecordMapperTest {

    private HealthRecordMapper healthRecordMapper;

    @BeforeEach
    void setUp() {
        healthRecordMapper = new HealthRecordMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getHealthRecordSample1();
        var actual = healthRecordMapper.toEntity(healthRecordMapper.toDto(expected));
        assertHealthRecordAllPropertiesEquals(expected, actual);
    }
}
