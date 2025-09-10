package com.techwiz.petcare.web.rest;

import static com.techwiz.petcare.domain.HealthRecordAsserts.*;
import static com.techwiz.petcare.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techwiz.petcare.IntegrationTest;
import com.techwiz.petcare.domain.HealthRecord;
import com.techwiz.petcare.repository.HealthRecordRepository;
import com.techwiz.petcare.service.dto.HealthRecordDTO;
import com.techwiz.petcare.service.mapper.HealthRecordMapper;
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
 * Integration tests for the {@link HealthRecordResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HealthRecordResourceIT {

    private static final Long DEFAULT_PET_ID = 1L;
    private static final Long UPDATED_PET_ID = 2L;
    private static final Long SMALLER_PET_ID = 1L - 1L;

    private static final Long DEFAULT_VET_ID = 1L;
    private static final Long UPDATED_VET_ID = 2L;
    private static final Long SMALLER_VET_ID = 1L - 1L;

    private static final Long DEFAULT_APPT_ID = 1L;
    private static final Long UPDATED_APPT_ID = 2L;
    private static final Long SMALLER_APPT_ID = 1L - 1L;

    private static final String DEFAULT_DIAGNOSIS = "AAAAAAAAAA";
    private static final String UPDATED_DIAGNOSIS = "BBBBBBBBBB";

    private static final String DEFAULT_TREATMENT = "AAAAAAAAAA";
    private static final String UPDATED_TREATMENT = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/health-records";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private HealthRecordMapper healthRecordMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHealthRecordMockMvc;

    private HealthRecord healthRecord;

    private HealthRecord insertedHealthRecord;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HealthRecord createEntity() {
        return new HealthRecord()
            .petId(DEFAULT_PET_ID)
            .vetId(DEFAULT_VET_ID)
            .apptId(DEFAULT_APPT_ID)
            .diagnosis(DEFAULT_DIAGNOSIS)
            .treatment(DEFAULT_TREATMENT)
            .notes(DEFAULT_NOTES)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HealthRecord createUpdatedEntity() {
        return new HealthRecord()
            .petId(UPDATED_PET_ID)
            .vetId(UPDATED_VET_ID)
            .apptId(UPDATED_APPT_ID)
            .diagnosis(UPDATED_DIAGNOSIS)
            .treatment(UPDATED_TREATMENT)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        healthRecord = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedHealthRecord != null) {
            healthRecordRepository.delete(insertedHealthRecord);
            insertedHealthRecord = null;
        }
    }

    @Test
    @Transactional
    void createHealthRecord() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the HealthRecord
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(healthRecord);
        var returnedHealthRecordDTO = om.readValue(
            restHealthRecordMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(healthRecordDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HealthRecordDTO.class
        );

        // Validate the HealthRecord in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHealthRecord = healthRecordMapper.toEntity(returnedHealthRecordDTO);
        assertHealthRecordUpdatableFieldsEquals(returnedHealthRecord, getPersistedHealthRecord(returnedHealthRecord));

        insertedHealthRecord = returnedHealthRecord;
    }

    @Test
    @Transactional
    void createHealthRecordWithExistingId() throws Exception {
        // Create the HealthRecord with an existing ID
        healthRecord.setId(1L);
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(healthRecord);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHealthRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(healthRecordDTO)))
            .andExpect(status().isBadRequest());

        // Validate the HealthRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPetIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        healthRecord.setPetId(null);

        // Create the HealthRecord, which fails.
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(healthRecord);

        restHealthRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(healthRecordDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVetIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        healthRecord.setVetId(null);

        // Create the HealthRecord, which fails.
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(healthRecord);

        restHealthRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(healthRecordDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkApptIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        healthRecord.setApptId(null);

        // Create the HealthRecord, which fails.
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(healthRecord);

        restHealthRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(healthRecordDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHealthRecords() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList
        restHealthRecordMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(healthRecord.getId().intValue())))
            .andExpect(jsonPath("$.[*].petId").value(hasItem(DEFAULT_PET_ID.intValue())))
            .andExpect(jsonPath("$.[*].vetId").value(hasItem(DEFAULT_VET_ID.intValue())))
            .andExpect(jsonPath("$.[*].apptId").value(hasItem(DEFAULT_APPT_ID.intValue())))
            .andExpect(jsonPath("$.[*].diagnosis").value(hasItem(DEFAULT_DIAGNOSIS)))
            .andExpect(jsonPath("$.[*].treatment").value(hasItem(DEFAULT_TREATMENT)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getHealthRecord() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get the healthRecord
        restHealthRecordMockMvc
            .perform(get(ENTITY_API_URL_ID, healthRecord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(healthRecord.getId().intValue()))
            .andExpect(jsonPath("$.petId").value(DEFAULT_PET_ID.intValue()))
            .andExpect(jsonPath("$.vetId").value(DEFAULT_VET_ID.intValue()))
            .andExpect(jsonPath("$.apptId").value(DEFAULT_APPT_ID.intValue()))
            .andExpect(jsonPath("$.diagnosis").value(DEFAULT_DIAGNOSIS))
            .andExpect(jsonPath("$.treatment").value(DEFAULT_TREATMENT))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getHealthRecordsByIdFiltering() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        Long id = healthRecord.getId();

        defaultHealthRecordFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultHealthRecordFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultHealthRecordFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByPetIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where petId equals to
        defaultHealthRecordFiltering("petId.equals=" + DEFAULT_PET_ID, "petId.equals=" + UPDATED_PET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByPetIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where petId in
        defaultHealthRecordFiltering("petId.in=" + DEFAULT_PET_ID + "," + UPDATED_PET_ID, "petId.in=" + UPDATED_PET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByPetIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where petId is not null
        defaultHealthRecordFiltering("petId.specified=true", "petId.specified=false");
    }

    @Test
    @Transactional
    void getAllHealthRecordsByPetIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where petId is greater than or equal to
        defaultHealthRecordFiltering("petId.greaterThanOrEqual=" + DEFAULT_PET_ID, "petId.greaterThanOrEqual=" + UPDATED_PET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByPetIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where petId is less than or equal to
        defaultHealthRecordFiltering("petId.lessThanOrEqual=" + DEFAULT_PET_ID, "petId.lessThanOrEqual=" + SMALLER_PET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByPetIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where petId is less than
        defaultHealthRecordFiltering("petId.lessThan=" + UPDATED_PET_ID, "petId.lessThan=" + DEFAULT_PET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByPetIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where petId is greater than
        defaultHealthRecordFiltering("petId.greaterThan=" + SMALLER_PET_ID, "petId.greaterThan=" + DEFAULT_PET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByVetIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where vetId equals to
        defaultHealthRecordFiltering("vetId.equals=" + DEFAULT_VET_ID, "vetId.equals=" + UPDATED_VET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByVetIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where vetId in
        defaultHealthRecordFiltering("vetId.in=" + DEFAULT_VET_ID + "," + UPDATED_VET_ID, "vetId.in=" + UPDATED_VET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByVetIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where vetId is not null
        defaultHealthRecordFiltering("vetId.specified=true", "vetId.specified=false");
    }

    @Test
    @Transactional
    void getAllHealthRecordsByVetIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where vetId is greater than or equal to
        defaultHealthRecordFiltering("vetId.greaterThanOrEqual=" + DEFAULT_VET_ID, "vetId.greaterThanOrEqual=" + UPDATED_VET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByVetIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where vetId is less than or equal to
        defaultHealthRecordFiltering("vetId.lessThanOrEqual=" + DEFAULT_VET_ID, "vetId.lessThanOrEqual=" + SMALLER_VET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByVetIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where vetId is less than
        defaultHealthRecordFiltering("vetId.lessThan=" + UPDATED_VET_ID, "vetId.lessThan=" + DEFAULT_VET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByVetIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where vetId is greater than
        defaultHealthRecordFiltering("vetId.greaterThan=" + SMALLER_VET_ID, "vetId.greaterThan=" + DEFAULT_VET_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByApptIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where apptId equals to
        defaultHealthRecordFiltering("apptId.equals=" + DEFAULT_APPT_ID, "apptId.equals=" + UPDATED_APPT_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByApptIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where apptId in
        defaultHealthRecordFiltering("apptId.in=" + DEFAULT_APPT_ID + "," + UPDATED_APPT_ID, "apptId.in=" + UPDATED_APPT_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByApptIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where apptId is not null
        defaultHealthRecordFiltering("apptId.specified=true", "apptId.specified=false");
    }

    @Test
    @Transactional
    void getAllHealthRecordsByApptIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where apptId is greater than or equal to
        defaultHealthRecordFiltering("apptId.greaterThanOrEqual=" + DEFAULT_APPT_ID, "apptId.greaterThanOrEqual=" + UPDATED_APPT_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByApptIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where apptId is less than or equal to
        defaultHealthRecordFiltering("apptId.lessThanOrEqual=" + DEFAULT_APPT_ID, "apptId.lessThanOrEqual=" + SMALLER_APPT_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByApptIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where apptId is less than
        defaultHealthRecordFiltering("apptId.lessThan=" + UPDATED_APPT_ID, "apptId.lessThan=" + DEFAULT_APPT_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByApptIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where apptId is greater than
        defaultHealthRecordFiltering("apptId.greaterThan=" + SMALLER_APPT_ID, "apptId.greaterThan=" + DEFAULT_APPT_ID);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where createdAt equals to
        defaultHealthRecordFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where createdAt in
        defaultHealthRecordFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllHealthRecordsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        // Get all the healthRecordList where createdAt is not null
        defaultHealthRecordFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    private void defaultHealthRecordFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultHealthRecordShouldBeFound(shouldBeFound);
        defaultHealthRecordShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultHealthRecordShouldBeFound(String filter) throws Exception {
        restHealthRecordMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(healthRecord.getId().intValue())))
            .andExpect(jsonPath("$.[*].petId").value(hasItem(DEFAULT_PET_ID.intValue())))
            .andExpect(jsonPath("$.[*].vetId").value(hasItem(DEFAULT_VET_ID.intValue())))
            .andExpect(jsonPath("$.[*].apptId").value(hasItem(DEFAULT_APPT_ID.intValue())))
            .andExpect(jsonPath("$.[*].diagnosis").value(hasItem(DEFAULT_DIAGNOSIS)))
            .andExpect(jsonPath("$.[*].treatment").value(hasItem(DEFAULT_TREATMENT)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restHealthRecordMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultHealthRecordShouldNotBeFound(String filter) throws Exception {
        restHealthRecordMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restHealthRecordMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingHealthRecord() throws Exception {
        // Get the healthRecord
        restHealthRecordMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHealthRecord() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the healthRecord
        HealthRecord updatedHealthRecord = healthRecordRepository.findById(healthRecord.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHealthRecord are not directly saved in db
        em.detach(updatedHealthRecord);
        updatedHealthRecord
            .petId(UPDATED_PET_ID)
            .vetId(UPDATED_VET_ID)
            .apptId(UPDATED_APPT_ID)
            .diagnosis(UPDATED_DIAGNOSIS)
            .treatment(UPDATED_TREATMENT)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT);
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(updatedHealthRecord);

        restHealthRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, healthRecordDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(healthRecordDTO))
            )
            .andExpect(status().isOk());

        // Validate the HealthRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHealthRecordToMatchAllProperties(updatedHealthRecord);
    }

    @Test
    @Transactional
    void putNonExistingHealthRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        healthRecord.setId(longCount.incrementAndGet());

        // Create the HealthRecord
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(healthRecord);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHealthRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, healthRecordDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(healthRecordDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HealthRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHealthRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        healthRecord.setId(longCount.incrementAndGet());

        // Create the HealthRecord
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(healthRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHealthRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(healthRecordDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HealthRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHealthRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        healthRecord.setId(longCount.incrementAndGet());

        // Create the HealthRecord
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(healthRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHealthRecordMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(healthRecordDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HealthRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHealthRecordWithPatch() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the healthRecord using partial update
        HealthRecord partialUpdatedHealthRecord = new HealthRecord();
        partialUpdatedHealthRecord.setId(healthRecord.getId());

        partialUpdatedHealthRecord
            .petId(UPDATED_PET_ID)
            .vetId(UPDATED_VET_ID)
            .apptId(UPDATED_APPT_ID)
            .diagnosis(UPDATED_DIAGNOSIS)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT);

        restHealthRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHealthRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHealthRecord))
            )
            .andExpect(status().isOk());

        // Validate the HealthRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHealthRecordUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedHealthRecord, healthRecord),
            getPersistedHealthRecord(healthRecord)
        );
    }

    @Test
    @Transactional
    void fullUpdateHealthRecordWithPatch() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the healthRecord using partial update
        HealthRecord partialUpdatedHealthRecord = new HealthRecord();
        partialUpdatedHealthRecord.setId(healthRecord.getId());

        partialUpdatedHealthRecord
            .petId(UPDATED_PET_ID)
            .vetId(UPDATED_VET_ID)
            .apptId(UPDATED_APPT_ID)
            .diagnosis(UPDATED_DIAGNOSIS)
            .treatment(UPDATED_TREATMENT)
            .notes(UPDATED_NOTES)
            .createdAt(UPDATED_CREATED_AT);

        restHealthRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHealthRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHealthRecord))
            )
            .andExpect(status().isOk());

        // Validate the HealthRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHealthRecordUpdatableFieldsEquals(partialUpdatedHealthRecord, getPersistedHealthRecord(partialUpdatedHealthRecord));
    }

    @Test
    @Transactional
    void patchNonExistingHealthRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        healthRecord.setId(longCount.incrementAndGet());

        // Create the HealthRecord
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(healthRecord);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHealthRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, healthRecordDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(healthRecordDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HealthRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHealthRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        healthRecord.setId(longCount.incrementAndGet());

        // Create the HealthRecord
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(healthRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHealthRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(healthRecordDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HealthRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHealthRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        healthRecord.setId(longCount.incrementAndGet());

        // Create the HealthRecord
        HealthRecordDTO healthRecordDTO = healthRecordMapper.toDto(healthRecord);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHealthRecordMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(healthRecordDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HealthRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHealthRecord() throws Exception {
        // Initialize the database
        insertedHealthRecord = healthRecordRepository.saveAndFlush(healthRecord);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the healthRecord
        restHealthRecordMockMvc
            .perform(delete(ENTITY_API_URL_ID, healthRecord.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return healthRecordRepository.count();
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

    protected HealthRecord getPersistedHealthRecord(HealthRecord healthRecord) {
        return healthRecordRepository.findById(healthRecord.getId()).orElseThrow();
    }

    protected void assertPersistedHealthRecordToMatchAllProperties(HealthRecord expectedHealthRecord) {
        assertHealthRecordAllPropertiesEquals(expectedHealthRecord, getPersistedHealthRecord(expectedHealthRecord));
    }

    protected void assertPersistedHealthRecordToMatchUpdatableProperties(HealthRecord expectedHealthRecord) {
        assertHealthRecordAllUpdatablePropertiesEquals(expectedHealthRecord, getPersistedHealthRecord(expectedHealthRecord));
    }
}
