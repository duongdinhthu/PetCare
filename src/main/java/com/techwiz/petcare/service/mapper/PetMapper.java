package com.techwiz.petcare.service.mapper;

import com.techwiz.petcare.domain.Pet;
import com.techwiz.petcare.service.dto.PetDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pet} and its DTO {@link PetDTO}.
 */
@Mapper(componentModel = "spring")
public interface PetMapper extends EntityMapper<PetDTO, Pet> {}
