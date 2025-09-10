package com.techwiz.petcare.service;

import com.techwiz.petcare.domain.*; // for static metamodels
import com.techwiz.petcare.domain.HealthRecord;
import com.techwiz.petcare.repository.HealthRecordRepository;
import com.techwiz.petcare.service.criteria.HealthRecordCriteria;
import com.techwiz.petcare.service.dto.HealthRecordDTO;
import com.techwiz.petcare.service.mapper.HealthRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link HealthRecord} entities in the database.
 * The main input is a {@link HealthRecordCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link HealthRecordDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class HealthRecordQueryService extends QueryService<HealthRecord> {

    private static final Logger LOG = LoggerFactory.getLogger(HealthRecordQueryService.class);

    private final HealthRecordRepository healthRecordRepository;

    private final HealthRecordMapper healthRecordMapper;

    public HealthRecordQueryService(HealthRecordRepository healthRecordRepository, HealthRecordMapper healthRecordMapper) {
        this.healthRecordRepository = healthRecordRepository;
        this.healthRecordMapper = healthRecordMapper;
    }

    /**
     * Return a {@link Page} of {@link HealthRecordDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<HealthRecordDTO> findByCriteria(HealthRecordCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<HealthRecord> specification = createSpecification(criteria);
        return healthRecordRepository.findAll(specification, page).map(healthRecordMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(HealthRecordCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<HealthRecord> specification = createSpecification(criteria);
        return healthRecordRepository.count(specification);
    }

    /**
     * Function to convert {@link HealthRecordCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<HealthRecord> createSpecification(HealthRecordCriteria criteria) {
        Specification<HealthRecord> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), HealthRecord_.id),
                buildRangeSpecification(criteria.getPetId(), HealthRecord_.petId),
                buildRangeSpecification(criteria.getVetId(), HealthRecord_.vetId),
                buildRangeSpecification(criteria.getApptId(), HealthRecord_.apptId),
                buildRangeSpecification(criteria.getCreatedAt(), HealthRecord_.createdAt)
            );
        }
        return specification;
    }
}
