package ir.maktab.shortcut.fianlproject.mapper;

import ir.maktab.shortcut.fianlproject.dtos.SpecialistRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.SpecialistResponseDto;
import ir.maktab.shortcut.fianlproject.entity.Specialist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SpecialistMapper {
    Specialist toEntity(SpecialistRequestDto dto);
    @Mapping(target = "fullName", ignore = true)
    void updateEntityFromDto(SpecialistRequestDto dto,
                             @MappingTarget Specialist specialist);
    SpecialistResponseDto toDto(Specialist specialist);
   SpecialistResponseDto toResponseDto(Specialist specialist);
}
