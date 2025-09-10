package com.techwiz.petcare.domain;

import static com.techwiz.petcare.domain.HealthRecordTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.techwiz.petcare.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HealthRecordTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HealthRecord.class);
        HealthRecord healthRecord1 = getHealthRecordSample1();
        HealthRecord healthRecord2 = new HealthRecord();
        assertThat(healthRecord1).isNotEqualTo(healthRecord2);

        healthRecord2.setId(healthRecord1.getId());
        assertThat(healthRecord1).isEqualTo(healthRecord2);

        healthRecord2 = getHealthRecordSample2();
        assertThat(healthRecord1).isNotEqualTo(healthRecord2);
    }
}
