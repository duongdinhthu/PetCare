package com.techwiz.petcare.web.rest;

import com.techwiz.petcare.repository.HealthRecordRepository;
import com.techwiz.petcare.service.HealthRecordQueryService;
import com.techwiz.petcare.service.HealthRecordService;
import com.techwiz.petcare.service.criteria.HealthRecordCriteria;
import com.techwiz.petcare.service.dto.HealthRecordDTO;
import com.techwiz.petcare.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.techwiz.petcare.domain.HealthRecord}.
 */
@RestController
@RequestMapping("/api/health-records")
public class HealthRecordResource {

    private static final Logger LOG = LoggerFactory.getLogger(HealthRecordResource.class);

    private static final String ENTITY_NAME = "healthRecord";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HealthRecordService healthRecordService;

    private final HealthRecordRepository healthRecordRepository;

    private final HealthRecordQueryService healthRecordQueryService;

    public HealthRecordResource(
        HealthRecordService healthRecordService,
        HealthRecordRepository healthRecordRepository,
        HealthRecordQueryService healthRecordQueryService
    ) {
        this.healthRecordService = healthRecordService;
        this.healthRecordRepository = healthRecordRepository;
        this.healthRecordQueryService = healthRecordQueryService;
    }

    /**
     * {@code POST  /health-records} : Create a new healthRecord.
     *
     * @param healthRecordDTO the healthRecordDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new healthRecordDTO, or with status {@code 400 (Bad Request)} if the healthRecord has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<HealthRecordDTO> createHealthRecord(@Valid @RequestBody HealthRecordDTO healthRecordDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save HealthRecord : {}", healthRecordDTO);
        if (healthRecordDTO.getId() != null) {
            throw new BadRequestAlertException("A new healthRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        healthRecordDTO = healthRecordService.save(healthRecordDTO);
        return ResponseEntity.created(new URI("/api/health-records/" + healthRecordDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, healthRecordDTO.getId().toString()))
            .body(healthRecordDTO);
    }

    /**
     * {@code PUT  /health-records/:id} : Updates an existing healthRecord.
     *
     * @param id the id of the healthRecordDTO to save.
     * @param healthRecordDTO the healthRecordDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated healthRecordDTO,
     * or with status {@code 400 (Bad Request)} if the healthRecordDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the healthRecordDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HealthRecordDTO> updateHealthRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HealthRecordDTO healthRecordDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update HealthRecord : {}, {}", id, healthRecordDTO);
        if (healthRecordDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, healthRecordDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!healthRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        healthRecordDTO = healthRecordService.update(healthRecordDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, healthRecordDTO.getId().toString()))
            .body(healthRecordDTO);
    }

    /**
     * {@code PATCH  /health-records/:id} : Partial updates given fields of an existing healthRecord, field will ignore if it is null
     *
     * @param id the id of the healthRecordDTO to save.
     * @param healthRecordDTO the healthRecordDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated healthRecordDTO,
     * or with status {@code 400 (Bad Request)} if the healthRecordDTO is not valid,
     * or with status {@code 404 (Not Found)} if the healthRecordDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the healthRecordDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HealthRecordDTO> partialUpdateHealthRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HealthRecordDTO healthRecordDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update HealthRecord partially : {}, {}", id, healthRecordDTO);
        if (healthRecordDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, healthRecordDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!healthRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HealthRecordDTO> result = healthRecordService.partialUpdate(healthRecordDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, healthRecordDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /health-records} : get all the healthRecords.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of healthRecords in body.
     */
    @GetMapping("")
    public ResponseEntity<List<HealthRecordDTO>> getAllHealthRecords(
        HealthRecordCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get HealthRecords by criteria: {}", criteria);

        Page<HealthRecordDTO> page = healthRecordQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /health-records/count} : count all the healthRecords.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countHealthRecords(HealthRecordCriteria criteria) {
        LOG.debug("REST request to count HealthRecords by criteria: {}", criteria);
        return ResponseEntity.ok().body(healthRecordQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /health-records/:id} : get the "id" healthRecord.
     *
     * @param id the id of the healthRecordDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the healthRecordDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HealthRecordDTO> getHealthRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to get HealthRecord : {}", id);
        Optional<HealthRecordDTO> healthRecordDTO = healthRecordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(healthRecordDTO);
    }

    /**
     * {@code DELETE  /health-records/:id} : delete the "id" healthRecord.
     *
     * @param id the id of the healthRecordDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete HealthRecord : {}", id);
        healthRecordService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
