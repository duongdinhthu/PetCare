package com.techwiz.petcare.service;

import com.techwiz.petcare.domain.*; // for static metamodels
import com.techwiz.petcare.domain.Pet;
import com.techwiz.petcare.repository.PetRepository;
import com.techwiz.petcare.service.criteria.PetCriteria;
import com.techwiz.petcare.service.dto.PetDTO;
import com.techwiz.petcare.service.mapper.PetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Pet} entities in the database.
 * The main input is a {@link PetCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PetDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PetQueryService extends QueryService<Pet> {

    private static final Logger LOG = LoggerFactory.getLogger(PetQueryService.class);

    private final PetRepository petRepository;

    private final PetMapper petMapper;

    public PetQueryService(PetRepository petRepository, PetMapper petMapper) {
        this.petRepository = petRepository;
        this.petMapper = petMapper;
    }

    /**
     * Return a {@link Page} of {@link PetDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PetDTO> findByCriteria(PetCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Pet> specification = createSpecification(criteria);
        return petRepository.findAll(specification, page).map(petMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PetCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Pet> specification = createSpecification(criteria);
        return petRepository.count(specification);
    }

    /**
     * Function to convert {@link PetCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Pet> createSpecification(PetCriteria criteria) {
        Specification<Pet> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Pet_.id),
                buildRangeSpecification(criteria.getOwnerId(), Pet_.ownerId),
                buildStringSpecification(criteria.getName(), Pet_.name),
                buildStringSpecification(criteria.getSpecies(), Pet_.species),
                buildStringSpecification(criteria.getBreed(), Pet_.breed),
                buildRangeSpecification(criteria.getAge(), Pet_.age),
                buildSpecification(criteria.getGender(), Pet_.gender),
                buildStringSpecification(criteria.getPhotoUrl(), Pet_.photoUrl),
                buildRangeSpecification(criteria.getCreatedAt(), Pet_.createdAt)
            );
        }
        return specification;
    }
}
