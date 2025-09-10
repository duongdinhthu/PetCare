package com.techwiz.petcare.web.rest;

import static com.techwiz.petcare.domain.PetAsserts.*;
import static com.techwiz.petcare.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techwiz.petcare.IntegrationTest;
import com.techwiz.petcare.domain.Pet;
import com.techwiz.petcare.domain.enumeration.Gender;
import com.techwiz.petcare.repository.PetRepository;
import com.techwiz.petcare.service.dto.PetDTO;
import com.techwiz.petcare.service.mapper.PetMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PetResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PetResourceIT {

    private static final Long DEFAULT_OWNER_ID = 1L;
    private static final Long UPDATED_OWNER_ID = 2L;
    private static final Long SMALLER_OWNER_ID = 1L - 1L;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SPECIES = "AAAAAAAAAA";
    private static final String UPDATED_SPECIES = "BBBBBBBBBB";

    private static final String DEFAULT_BREED = "AAAAAAAAAA";
    private static final String UPDATED_BREED = "BBBBBBBBBB";

    private static final Integer DEFAULT_AGE = 1;
    private static final Integer UPDATED_AGE = 2;
    private static final Integer SMALLER_AGE = 1 - 1;

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final String DEFAULT_PHOTO_URL = "AAAAAAAAAA";
    private static final String UPDATED_PHOTO_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/pets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPetMockMvc;

    private Pet pet;

    private Pet insertedPet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pet createEntity() {
        return new Pet()
            .ownerId(DEFAULT_OWNER_ID)
            .name(DEFAULT_NAME)
            .species(DEFAULT_SPECIES)
            .breed(DEFAULT_BREED)
            .age(DEFAULT_AGE)
            .gender(DEFAULT_GENDER)
            .photoUrl(DEFAULT_PHOTO_URL)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pet createUpdatedEntity() {
        return new Pet()
            .ownerId(UPDATED_OWNER_ID)
            .name(UPDATED_NAME)
            .species(UPDATED_SPECIES)
            .breed(UPDATED_BREED)
            .age(UPDATED_AGE)
            .gender(UPDATED_GENDER)
            .photoUrl(UPDATED_PHOTO_URL)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        pet = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPet != null) {
            petRepository.delete(insertedPet);
            insertedPet = null;
        }
    }

    @Test
    @Transactional
    void createPet() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Pet
        PetDTO petDTO = petMapper.toDto(pet);
        var returnedPetDTO = om.readValue(
            restPetMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(petDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PetDTO.class
        );

        // Validate the Pet in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPet = petMapper.toEntity(returnedPetDTO);
        assertPetUpdatableFieldsEquals(returnedPet, getPersistedPet(returnedPet));

        insertedPet = returnedPet;
    }

    @Test
    @Transactional
    void createPetWithExistingId() throws Exception {
        // Create the Pet with an existing ID
        pet.setId(1L);
        PetDTO petDTO = petMapper.toDto(pet);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(petDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pet in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOwnerIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pet.setOwnerId(null);

        // Create the Pet, which fails.
        PetDTO petDTO = petMapper.toDto(pet);

        restPetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(petDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pet.setName(null);

        // Create the Pet, which fails.
        PetDTO petDTO = petMapper.toDto(pet);

        restPetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(petDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPets() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList
        restPetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pet.getId().intValue())))
            .andExpect(jsonPath("$.[*].ownerId").value(hasItem(DEFAULT_OWNER_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].species").value(hasItem(DEFAULT_SPECIES)))
            .andExpect(jsonPath("$.[*].breed").value(hasItem(DEFAULT_BREED)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].photoUrl").value(hasItem(DEFAULT_PHOTO_URL)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPet() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get the pet
        restPetMockMvc
            .perform(get(ENTITY_API_URL_ID, pet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pet.getId().intValue()))
            .andExpect(jsonPath("$.ownerId").value(DEFAULT_OWNER_ID.intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.species").value(DEFAULT_SPECIES))
            .andExpect(jsonPath("$.breed").value(DEFAULT_BREED))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.photoUrl").value(DEFAULT_PHOTO_URL))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getPetsByIdFiltering() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        Long id = pet.getId();

        defaultPetFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPetFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPetFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPetsByOwnerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where ownerId equals to
        defaultPetFiltering("ownerId.equals=" + DEFAULT_OWNER_ID, "ownerId.equals=" + UPDATED_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllPetsByOwnerIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where ownerId in
        defaultPetFiltering("ownerId.in=" + DEFAULT_OWNER_ID + "," + UPDATED_OWNER_ID, "ownerId.in=" + UPDATED_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllPetsByOwnerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where ownerId is not null
        defaultPetFiltering("ownerId.specified=true", "ownerId.specified=false");
    }

    @Test
    @Transactional
    void getAllPetsByOwnerIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where ownerId is greater than or equal to
        defaultPetFiltering("ownerId.greaterThanOrEqual=" + DEFAULT_OWNER_ID, "ownerId.greaterThanOrEqual=" + UPDATED_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllPetsByOwnerIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where ownerId is less than or equal to
        defaultPetFiltering("ownerId.lessThanOrEqual=" + DEFAULT_OWNER_ID, "ownerId.lessThanOrEqual=" + SMALLER_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllPetsByOwnerIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where ownerId is less than
        defaultPetFiltering("ownerId.lessThan=" + UPDATED_OWNER_ID, "ownerId.lessThan=" + DEFAULT_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllPetsByOwnerIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where ownerId is greater than
        defaultPetFiltering("ownerId.greaterThan=" + SMALLER_OWNER_ID, "ownerId.greaterThan=" + DEFAULT_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllPetsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where name equals to
        defaultPetFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPetsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where name in
        defaultPetFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPetsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where name is not null
        defaultPetFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllPetsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where name contains
        defaultPetFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPetsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where name does not contain
        defaultPetFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllPetsBySpeciesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where species equals to
        defaultPetFiltering("species.equals=" + DEFAULT_SPECIES, "species.equals=" + UPDATED_SPECIES);
    }

    @Test
    @Transactional
    void getAllPetsBySpeciesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where species in
        defaultPetFiltering("species.in=" + DEFAULT_SPECIES + "," + UPDATED_SPECIES, "species.in=" + UPDATED_SPECIES);
    }

    @Test
    @Transactional
    void getAllPetsBySpeciesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where species is not null
        defaultPetFiltering("species.specified=true", "species.specified=false");
    }

    @Test
    @Transactional
    void getAllPetsBySpeciesContainsSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where species contains
        defaultPetFiltering("species.contains=" + DEFAULT_SPECIES, "species.contains=" + UPDATED_SPECIES);
    }

    @Test
    @Transactional
    void getAllPetsBySpeciesNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where species does not contain
        defaultPetFiltering("species.doesNotContain=" + UPDATED_SPECIES, "species.doesNotContain=" + DEFAULT_SPECIES);
    }

    @Test
    @Transactional
    void getAllPetsByBreedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where breed equals to
        defaultPetFiltering("breed.equals=" + DEFAULT_BREED, "breed.equals=" + UPDATED_BREED);
    }

    @Test
    @Transactional
    void getAllPetsByBreedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where breed in
        defaultPetFiltering("breed.in=" + DEFAULT_BREED + "," + UPDATED_BREED, "breed.in=" + UPDATED_BREED);
    }

    @Test
    @Transactional
    void getAllPetsByBreedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where breed is not null
        defaultPetFiltering("breed.specified=true", "breed.specified=false");
    }

    @Test
    @Transactional
    void getAllPetsByBreedContainsSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where breed contains
        defaultPetFiltering("breed.contains=" + DEFAULT_BREED, "breed.contains=" + UPDATED_BREED);
    }

    @Test
    @Transactional
    void getAllPetsByBreedNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where breed does not contain
        defaultPetFiltering("breed.doesNotContain=" + UPDATED_BREED, "breed.doesNotContain=" + DEFAULT_BREED);
    }

    @Test
    @Transactional
    void getAllPetsByAgeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where age equals to
        defaultPetFiltering("age.equals=" + DEFAULT_AGE, "age.equals=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    void getAllPetsByAgeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where age in
        defaultPetFiltering("age.in=" + DEFAULT_AGE + "," + UPDATED_AGE, "age.in=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    void getAllPetsByAgeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where age is not null
        defaultPetFiltering("age.specified=true", "age.specified=false");
    }

    @Test
    @Transactional
    void getAllPetsByAgeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where age is greater than or equal to
        defaultPetFiltering("age.greaterThanOrEqual=" + DEFAULT_AGE, "age.greaterThanOrEqual=" + UPDATED_AGE);
    }

    @Test
    @Transactional
    void getAllPetsByAgeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where age is less than or equal to
        defaultPetFiltering("age.lessThanOrEqual=" + DEFAULT_AGE, "age.lessThanOrEqual=" + SMALLER_AGE);
    }

    @Test
    @Transactional
    void getAllPetsByAgeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where age is less than
        defaultPetFiltering("age.lessThan=" + UPDATED_AGE, "age.lessThan=" + DEFAULT_AGE);
    }

    @Test
    @Transactional
    void getAllPetsByAgeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where age is greater than
        defaultPetFiltering("age.greaterThan=" + SMALLER_AGE, "age.greaterThan=" + DEFAULT_AGE);
    }

    @Test
    @Transactional
    void getAllPetsByGenderIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where gender equals to
        defaultPetFiltering("gender.equals=" + DEFAULT_GENDER, "gender.equals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllPetsByGenderIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where gender in
        defaultPetFiltering("gender.in=" + DEFAULT_GENDER + "," + UPDATED_GENDER, "gender.in=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllPetsByGenderIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where gender is not null
        defaultPetFiltering("gender.specified=true", "gender.specified=false");
    }

    @Test
    @Transactional
    void getAllPetsByPhotoUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where photoUrl equals to
        defaultPetFiltering("photoUrl.equals=" + DEFAULT_PHOTO_URL, "photoUrl.equals=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllPetsByPhotoUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where photoUrl in
        defaultPetFiltering("photoUrl.in=" + DEFAULT_PHOTO_URL + "," + UPDATED_PHOTO_URL, "photoUrl.in=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllPetsByPhotoUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where photoUrl is not null
        defaultPetFiltering("photoUrl.specified=true", "photoUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllPetsByPhotoUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where photoUrl contains
        defaultPetFiltering("photoUrl.contains=" + DEFAULT_PHOTO_URL, "photoUrl.contains=" + UPDATED_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllPetsByPhotoUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where photoUrl does not contain
        defaultPetFiltering("photoUrl.doesNotContain=" + UPDATED_PHOTO_URL, "photoUrl.doesNotContain=" + DEFAULT_PHOTO_URL);
    }

    @Test
    @Transactional
    void getAllPetsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where createdAt equals to
        defaultPetFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPetsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where createdAt in
        defaultPetFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllPetsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        // Get all the petList where createdAt is not null
        defaultPetFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    private void defaultPetFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPetShouldBeFound(shouldBeFound);
        defaultPetShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPetShouldBeFound(String filter) throws Exception {
        restPetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pet.getId().intValue())))
            .andExpect(jsonPath("$.[*].ownerId").value(hasItem(DEFAULT_OWNER_ID.intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].species").value(hasItem(DEFAULT_SPECIES)))
            .andExpect(jsonPath("$.[*].breed").value(hasItem(DEFAULT_BREED)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].photoUrl").value(hasItem(DEFAULT_PHOTO_URL)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restPetMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPetShouldNotBeFound(String filter) throws Exception {
        restPetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPetMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPet() throws Exception {
        // Get the pet
        restPetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPet() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pet
        Pet updatedPet = petRepository.findById(pet.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPet are not directly saved in db
        em.detach(updatedPet);
        updatedPet
            .ownerId(UPDATED_OWNER_ID)
            .name(UPDATED_NAME)
            .species(UPDATED_SPECIES)
            .breed(UPDATED_BREED)
            .age(UPDATED_AGE)
            .gender(UPDATED_GENDER)
            .photoUrl(UPDATED_PHOTO_URL)
            .createdAt(UPDATED_CREATED_AT);
        PetDTO petDTO = petMapper.toDto(updatedPet);

        restPetMockMvc
            .perform(put(ENTITY_API_URL_ID, petDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(petDTO)))
            .andExpect(status().isOk());

        // Validate the Pet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPetToMatchAllProperties(updatedPet);
    }

    @Test
    @Transactional
    void putNonExistingPet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pet.setId(longCount.incrementAndGet());

        // Create the Pet
        PetDTO petDTO = petMapper.toDto(pet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPetMockMvc
            .perform(put(ENTITY_API_URL_ID, petDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(petDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Pet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pet.setId(longCount.incrementAndGet());

        // Create the Pet
        PetDTO petDTO = petMapper.toDto(pet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(petDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pet.setId(longCount.incrementAndGet());

        // Create the Pet
        PetDTO petDTO = petMapper.toDto(pet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(petDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePetWithPatch() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pet using partial update
        Pet partialUpdatedPet = new Pet();
        partialUpdatedPet.setId(pet.getId());

        partialUpdatedPet
            .species(UPDATED_SPECIES)
            .breed(UPDATED_BREED)
            .gender(UPDATED_GENDER)
            .photoUrl(UPDATED_PHOTO_URL)
            .createdAt(UPDATED_CREATED_AT);

        restPetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPet))
            )
            .andExpect(status().isOk());

        // Validate the Pet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPetUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPet, pet), getPersistedPet(pet));
    }

    @Test
    @Transactional
    void fullUpdatePetWithPatch() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pet using partial update
        Pet partialUpdatedPet = new Pet();
        partialUpdatedPet.setId(pet.getId());

        partialUpdatedPet
            .ownerId(UPDATED_OWNER_ID)
            .name(UPDATED_NAME)
            .species(UPDATED_SPECIES)
            .breed(UPDATED_BREED)
            .age(UPDATED_AGE)
            .gender(UPDATED_GENDER)
            .photoUrl(UPDATED_PHOTO_URL)
            .createdAt(UPDATED_CREATED_AT);

        restPetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPet))
            )
            .andExpect(status().isOk());

        // Validate the Pet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPetUpdatableFieldsEquals(partialUpdatedPet, getPersistedPet(partialUpdatedPet));
    }

    @Test
    @Transactional
    void patchNonExistingPet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pet.setId(longCount.incrementAndGet());

        // Create the Pet
        PetDTO petDTO = petMapper.toDto(pet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, petDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(petDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pet.setId(longCount.incrementAndGet());

        // Create the Pet
        PetDTO petDTO = petMapper.toDto(pet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(petDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Pet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pet.setId(longCount.incrementAndGet());

        // Create the Pet
        PetDTO petDTO = petMapper.toDto(pet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPetMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(petDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Pet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePet() throws Exception {
        // Initialize the database
        insertedPet = petRepository.saveAndFlush(pet);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pet
        restPetMockMvc.perform(delete(ENTITY_API_URL_ID, pet.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return petRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Pet getPersistedPet(Pet pet) {
        return petRepository.findById(pet.getId()).orElseThrow();
    }

    protected void assertPersistedPetToMatchAllProperties(Pet expectedPet) {
        assertPetAllPropertiesEquals(expectedPet, getPersistedPet(expectedPet));
    }

    protected void assertPersistedPetToMatchUpdatableProperties(Pet expectedPet) {
        assertPetAllUpdatablePropertiesEquals(expectedPet, getPersistedPet(expectedPet));
    }
}
