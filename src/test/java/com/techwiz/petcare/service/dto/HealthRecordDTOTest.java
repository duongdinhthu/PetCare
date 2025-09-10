package com.techwiz.petcare.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.techwiz.petcare.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HealthRecordDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HealthRecordDTO.class);
        HealthRecordDTO healthRecordDTO1 = new HealthRecordDTO();
        healthRecordDTO1.setId(1L);
        HealthRecordDTO healthRecordDTO2 = new HealthRecordDTO();
        assertThat(healthRecordDTO1).isNotEqualTo(healthRecordDTO2);
        healthRecordDTO2.setId(healthRecordDTO1.getId());
        assertThat(healthRecordDTO1).isEqualTo(healthRecordDTO2);
        healthRecordDTO2.setId(2L);
        assertThat(healthRecordDTO1).isNotEqualTo(healthRecordDTO2);
        healthRecordDTO1.setId(null);
        assertThat(healthRecordDTO1).isNotEqualTo(healthRecordDTO2);
    }
}
