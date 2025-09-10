package com.techwiz.petcare.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class HealthRecordTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static HealthRecord getHealthRecordSample1() {
        return new HealthRecord().id(1L).petId(1L).vetId(1L).apptId(1L);
    }

    public static HealthRecord getHealthRecordSample2() {
        return new HealthRecord().id(2L).petId(2L).vetId(2L).apptId(2L);
    }

    public static HealthRecord getHealthRecordRandomSampleGenerator() {
        return new HealthRecord()
            .id(longCount.incrementAndGet())
            .petId(longCount.incrementAndGet())
            .vetId(longCount.incrementAndGet())
            .apptId(longCount.incrementAndGet());
    }
}
