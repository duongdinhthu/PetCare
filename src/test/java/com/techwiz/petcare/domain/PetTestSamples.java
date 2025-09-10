package com.techwiz.petcare.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Pet getPetSample1() {
        return new Pet().id(1L).ownerId(1L).name("name1").species("species1").breed("breed1").age(1).photoUrl("photoUrl1");
    }

    public static Pet getPetSample2() {
        return new Pet().id(2L).ownerId(2L).name("name2").species("species2").breed("breed2").age(2).photoUrl("photoUrl2");
    }

    public static Pet getPetRandomSampleGenerator() {
        return new Pet()
            .id(longCount.incrementAndGet())
            .ownerId(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .species(UUID.randomUUID().toString())
            .breed(UUID.randomUUID().toString())
            .age(intCount.incrementAndGet())
            .photoUrl(UUID.randomUUID().toString());
    }
}
