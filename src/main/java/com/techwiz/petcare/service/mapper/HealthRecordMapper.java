package com.techwiz.petcare.service.mapper;

import com.techwiz.petcare.domain.HealthRecord;
import com.techwiz.petcare.service.dto.HealthRecordDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link HealthRecord} and its DTO {@link HealthRecordDTO}.
 */
@Mapper(componentModel = "spring")
public interface HealthRecordMapper extends EntityMapper<HealthRecordDTO, HealthRecord> {}
