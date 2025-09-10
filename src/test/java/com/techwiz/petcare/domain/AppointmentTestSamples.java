package com.techwiz.petcare.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AppointmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Appointment getAppointmentSample1() {
        return new Appointment().id(1L).petId(1L).ownerId(1L).vetId(1L);
    }

    public static Appointment getAppointmentSample2() {
        return new Appointment().id(2L).petId(2L).ownerId(2L).vetId(2L);
    }

    public static Appointment getAppointmentRandomSampleGenerator() {
        return new Appointment()
            .id(longCount.incrementAndGet())
            .petId(longCount.incrementAndGet())
            .ownerId(longCount.incrementAndGet())
            .vetId(longCount.incrementAndGet());
    }
}
