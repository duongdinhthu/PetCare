package com.techwiz.petcare.domain;

import static com.techwiz.petcare.domain.PetTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.techwiz.petcare.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pet.class);
        Pet pet1 = getPetSample1();
        Pet pet2 = new Pet();
        assertThat(pet1).isNotEqualTo(pet2);

        pet2.setId(pet1.getId());
        assertThat(pet1).isEqualTo(pet2);

        pet2 = getPetSample2();
        assertThat(pet1).isNotEqualTo(pet2);
    }
}
