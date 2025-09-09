package com.techwiz.petcare.service.mapper;

import com.techwiz.petcare.domain.Appointment;
import com.techwiz.petcare.service.dto.AppointmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Appointment} and its DTO {@link AppointmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper extends EntityMapper<AppointmentDTO, Appointment> {}
