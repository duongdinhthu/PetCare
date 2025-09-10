package com.techwiz.petcare.web.rest;

import static com.techwiz.petcare.domain.AppointmentAsserts.*;
import static com.techwiz.petcare.web.rest.TestUtil.createUpdateProxyForBean;
import static com.techwiz.petcare.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techwiz.petcare.IntegrationTest;
import com.techwiz.petcare.domain.Appointment;
import com.techwiz.petcare.domain.enumeration.ApptStatus;
import com.techwiz.petcare.repository.AppointmentRepository;
import com.techwiz.petcare.service.dto.AppointmentDTO;
import com.techwiz.petcare.service.mapper.AppointmentMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link AppointmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AppointmentResourceIT {

    private static final Long DEFAULT_PET_ID = 1L;
    private static final Long UPDATED_PET_ID = 2L;
    private static final Long SMALLER_PET_ID = 1L - 1L;

    private static final Long DEFAULT_OWNER_ID = 1L;
    private static final Long UPDATED_OWNER_ID = 2L;
    private static final Long SMALLER_OWNER_ID = 1L - 1L;

    private static final Long DEFAULT_VET_ID = 1L;
    private static final Long UPDATED_VET_ID = 2L;
    private static final Long SMALLER_VET_ID = 1L - 1L;

    private static final ZonedDateTime DEFAULT_APPT_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_APPT_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_APPT_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ApptStatus DEFAULT_STATUS = ApptStatus.PENDING;
    private static final ApptStatus UPDATED_STATUS = ApptStatus.CONFIRMED;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/appointments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppointmentMockMvc;

    private Appointment appointment;

    private Appointment insertedAppointment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointment createEntity() {
        return new Appointment()
            .petId(DEFAULT_PET_ID)
            .ownerId(DEFAULT_OWNER_ID)
            .vetId(DEFAULT_VET_ID)
            .apptTime(DEFAULT_APPT_TIME)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Appointment createUpdatedEntity() {
        return new Appointment()
            .petId(UPDATED_PET_ID)
            .ownerId(UPDATED_OWNER_ID)
            .vetId(UPDATED_VET_ID)
            .apptTime(UPDATED_APPT_TIME)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        appointment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAppointment != null) {
            appointmentRepository.delete(insertedAppointment);
            insertedAppointment = null;
        }
    }

    @Test
    @Transactional
    void createAppointment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);
        var returnedAppointmentDTO = om.readValue(
            restAppointmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AppointmentDTO.class
        );

        // Validate the Appointment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppointment = appointmentMapper.toEntity(returnedAppointmentDTO);
        assertAppointmentUpdatableFieldsEquals(returnedAppointment, getPersistedAppointment(returnedAppointment));

        insertedAppointment = returnedAppointment;
    }

    @Test
    @Transactional
    void createAppointmentWithExistingId() throws Exception {
        // Create the Appointment with an existing ID
        appointment.setId(1L);
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPetIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appointment.setPetId(null);

        // Create the Appointment, which fails.
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOwnerIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appointment.setOwnerId(null);

        // Create the Appointment, which fails.
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVetIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appointment.setVetId(null);

        // Create the Appointment, which fails.
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkApptTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appointment.setApptTime(null);

        // Create the Appointment, which fails.
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        restAppointmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAppointments() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.getId().intValue())))
            .andExpect(jsonPath("$.[*].petId").value(hasItem(DEFAULT_PET_ID.intValue())))
            .andExpect(jsonPath("$.[*].ownerId").value(hasItem(DEFAULT_OWNER_ID.intValue())))
            .andExpect(jsonPath("$.[*].vetId").value(hasItem(DEFAULT_VET_ID.intValue())))
            .andExpect(jsonPath("$.[*].apptTime").value(hasItem(sameInstant(DEFAULT_APPT_TIME))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getAppointment() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get the appointment
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL_ID, appointment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appointment.getId().intValue()))
            .andExpect(jsonPath("$.petId").value(DEFAULT_PET_ID.intValue()))
            .andExpect(jsonPath("$.ownerId").value(DEFAULT_OWNER_ID.intValue()))
            .andExpect(jsonPath("$.vetId").value(DEFAULT_VET_ID.intValue()))
            .andExpect(jsonPath("$.apptTime").value(sameInstant(DEFAULT_APPT_TIME)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getAppointmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        Long id = appointment.getId();

        defaultAppointmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAppointmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAppointmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAppointmentsByPetIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where petId equals to
        defaultAppointmentFiltering("petId.equals=" + DEFAULT_PET_ID, "petId.equals=" + UPDATED_PET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByPetIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where petId in
        defaultAppointmentFiltering("petId.in=" + DEFAULT_PET_ID + "," + UPDATED_PET_ID, "petId.in=" + UPDATED_PET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByPetIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where petId is not null
        defaultAppointmentFiltering("petId.specified=true", "petId.specified=false");
    }

    @Test
    @Transactional
    void getAllAppointmentsByPetIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where petId is greater than or equal to
        defaultAppointmentFiltering("petId.greaterThanOrEqual=" + DEFAULT_PET_ID, "petId.greaterThanOrEqual=" + UPDATED_PET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByPetIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where petId is less than or equal to
        defaultAppointmentFiltering("petId.lessThanOrEqual=" + DEFAULT_PET_ID, "petId.lessThanOrEqual=" + SMALLER_PET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByPetIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where petId is less than
        defaultAppointmentFiltering("petId.lessThan=" + UPDATED_PET_ID, "petId.lessThan=" + DEFAULT_PET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByPetIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where petId is greater than
        defaultAppointmentFiltering("petId.greaterThan=" + SMALLER_PET_ID, "petId.greaterThan=" + DEFAULT_PET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByOwnerIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where ownerId equals to
        defaultAppointmentFiltering("ownerId.equals=" + DEFAULT_OWNER_ID, "ownerId.equals=" + UPDATED_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByOwnerIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where ownerId in
        defaultAppointmentFiltering("ownerId.in=" + DEFAULT_OWNER_ID + "," + UPDATED_OWNER_ID, "ownerId.in=" + UPDATED_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByOwnerIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where ownerId is not null
        defaultAppointmentFiltering("ownerId.specified=true", "ownerId.specified=false");
    }

    @Test
    @Transactional
    void getAllAppointmentsByOwnerIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where ownerId is greater than or equal to
        defaultAppointmentFiltering("ownerId.greaterThanOrEqual=" + DEFAULT_OWNER_ID, "ownerId.greaterThanOrEqual=" + UPDATED_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByOwnerIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where ownerId is less than or equal to
        defaultAppointmentFiltering("ownerId.lessThanOrEqual=" + DEFAULT_OWNER_ID, "ownerId.lessThanOrEqual=" + SMALLER_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByOwnerIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where ownerId is less than
        defaultAppointmentFiltering("ownerId.lessThan=" + UPDATED_OWNER_ID, "ownerId.lessThan=" + DEFAULT_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByOwnerIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where ownerId is greater than
        defaultAppointmentFiltering("ownerId.greaterThan=" + SMALLER_OWNER_ID, "ownerId.greaterThan=" + DEFAULT_OWNER_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByVetIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where vetId equals to
        defaultAppointmentFiltering("vetId.equals=" + DEFAULT_VET_ID, "vetId.equals=" + UPDATED_VET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByVetIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where vetId in
        defaultAppointmentFiltering("vetId.in=" + DEFAULT_VET_ID + "," + UPDATED_VET_ID, "vetId.in=" + UPDATED_VET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByVetIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where vetId is not null
        defaultAppointmentFiltering("vetId.specified=true", "vetId.specified=false");
    }

    @Test
    @Transactional
    void getAllAppointmentsByVetIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where vetId is greater than or equal to
        defaultAppointmentFiltering("vetId.greaterThanOrEqual=" + DEFAULT_VET_ID, "vetId.greaterThanOrEqual=" + UPDATED_VET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByVetIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where vetId is less than or equal to
        defaultAppointmentFiltering("vetId.lessThanOrEqual=" + DEFAULT_VET_ID, "vetId.lessThanOrEqual=" + SMALLER_VET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByVetIdIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where vetId is less than
        defaultAppointmentFiltering("vetId.lessThan=" + UPDATED_VET_ID, "vetId.lessThan=" + DEFAULT_VET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByVetIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where vetId is greater than
        defaultAppointmentFiltering("vetId.greaterThan=" + SMALLER_VET_ID, "vetId.greaterThan=" + DEFAULT_VET_ID);
    }

    @Test
    @Transactional
    void getAllAppointmentsByApptTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where apptTime equals to
        defaultAppointmentFiltering("apptTime.equals=" + DEFAULT_APPT_TIME, "apptTime.equals=" + UPDATED_APPT_TIME);
    }

    @Test
    @Transactional
    void getAllAppointmentsByApptTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where apptTime in
        defaultAppointmentFiltering("apptTime.in=" + DEFAULT_APPT_TIME + "," + UPDATED_APPT_TIME, "apptTime.in=" + UPDATED_APPT_TIME);
    }

    @Test
    @Transactional
    void getAllAppointmentsByApptTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where apptTime is not null
        defaultAppointmentFiltering("apptTime.specified=true", "apptTime.specified=false");
    }

    @Test
    @Transactional
    void getAllAppointmentsByApptTimeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where apptTime is greater than or equal to
        defaultAppointmentFiltering("apptTime.greaterThanOrEqual=" + DEFAULT_APPT_TIME, "apptTime.greaterThanOrEqual=" + UPDATED_APPT_TIME);
    }

    @Test
    @Transactional
    void getAllAppointmentsByApptTimeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where apptTime is less than or equal to
        defaultAppointmentFiltering("apptTime.lessThanOrEqual=" + DEFAULT_APPT_TIME, "apptTime.lessThanOrEqual=" + SMALLER_APPT_TIME);
    }

    @Test
    @Transactional
    void getAllAppointmentsByApptTimeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where apptTime is less than
        defaultAppointmentFiltering("apptTime.lessThan=" + UPDATED_APPT_TIME, "apptTime.lessThan=" + DEFAULT_APPT_TIME);
    }

    @Test
    @Transactional
    void getAllAppointmentsByApptTimeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where apptTime is greater than
        defaultAppointmentFiltering("apptTime.greaterThan=" + SMALLER_APPT_TIME, "apptTime.greaterThan=" + DEFAULT_APPT_TIME);
    }

    @Test
    @Transactional
    void getAllAppointmentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where status equals to
        defaultAppointmentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAppointmentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where status in
        defaultAppointmentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAppointmentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where status is not null
        defaultAppointmentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllAppointmentsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where createdAt equals to
        defaultAppointmentFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAppointmentsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where createdAt in
        defaultAppointmentFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllAppointmentsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        // Get all the appointmentList where createdAt is not null
        defaultAppointmentFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    private void defaultAppointmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAppointmentShouldBeFound(shouldBeFound);
        defaultAppointmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppointmentShouldBeFound(String filter) throws Exception {
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.getId().intValue())))
            .andExpect(jsonPath("$.[*].petId").value(hasItem(DEFAULT_PET_ID.intValue())))
            .andExpect(jsonPath("$.[*].ownerId").value(hasItem(DEFAULT_OWNER_ID.intValue())))
            .andExpect(jsonPath("$.[*].vetId").value(hasItem(DEFAULT_VET_ID.intValue())))
            .andExpect(jsonPath("$.[*].apptTime").value(hasItem(sameInstant(DEFAULT_APPT_TIME))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAppointmentShouldNotBeFound(String filter) throws Exception {
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppointmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAppointment() throws Exception {
        // Get the appointment
        restAppointmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAppointment() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appointment
        Appointment updatedAppointment = appointmentRepository.findById(appointment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAppointment are not directly saved in db
        em.detach(updatedAppointment);
        updatedAppointment
            .petId(UPDATED_PET_ID)
            .ownerId(UPDATED_OWNER_ID)
            .vetId(UPDATED_VET_ID)
            .apptTime(UPDATED_APPT_TIME)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(updatedAppointment);

        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appointmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appointmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppointmentToMatchAllProperties(updatedAppointment);
    }

    @Test
    @Transactional
    void putNonExistingAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appointmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appointmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appointmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppointmentWithPatch() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appointment using partial update
        Appointment partialUpdatedAppointment = new Appointment();
        partialUpdatedAppointment.setId(appointment.getId());

        partialUpdatedAppointment
            .petId(UPDATED_PET_ID)
            .ownerId(UPDATED_OWNER_ID)
            .vetId(UPDATED_VET_ID)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);

        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppointment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppointment))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppointmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAppointment, appointment),
            getPersistedAppointment(appointment)
        );
    }

    @Test
    @Transactional
    void fullUpdateAppointmentWithPatch() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appointment using partial update
        Appointment partialUpdatedAppointment = new Appointment();
        partialUpdatedAppointment.setId(appointment.getId());

        partialUpdatedAppointment
            .petId(UPDATED_PET_ID)
            .ownerId(UPDATED_OWNER_ID)
            .vetId(UPDATED_VET_ID)
            .apptTime(UPDATED_APPT_TIME)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT);

        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppointment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppointment))
            )
            .andExpect(status().isOk());

        // Validate the Appointment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppointmentUpdatableFieldsEquals(partialUpdatedAppointment, getPersistedAppointment(partialUpdatedAppointment));
    }

    @Test
    @Transactional
    void patchNonExistingAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appointmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appointmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appointmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppointment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appointment.setId(longCount.incrementAndGet());

        // Create the Appointment
        AppointmentDTO appointmentDTO = appointmentMapper.toDto(appointment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(appointmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Appointment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppointment() throws Exception {
        // Initialize the database
        insertedAppointment = appointmentRepository.saveAndFlush(appointment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the appointment
        restAppointmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, appointment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return appointmentRepository.count();
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

    protected Appointment getPersistedAppointment(Appointment appointment) {
        return appointmentRepository.findById(appointment.getId()).orElseThrow();
    }

    protected void assertPersistedAppointmentToMatchAllProperties(Appointment expectedAppointment) {
        assertAppointmentAllPropertiesEquals(expectedAppointment, getPersistedAppointment(expectedAppointment));
    }

    protected void assertPersistedAppointmentToMatchUpdatableProperties(Appointment expectedAppointment) {
        assertAppointmentAllUpdatablePropertiesEquals(expectedAppointment, getPersistedAppointment(expectedAppointment));
    }
}
