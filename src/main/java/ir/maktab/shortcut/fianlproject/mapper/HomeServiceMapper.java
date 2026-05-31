package ir.maktab.shortcut.fianlproject.mapper;

import ir.maktab.shortcut.fianlproject.dtos.HomeServiceRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.HomeServiceResponseDto;
import ir.maktab.shortcut.fianlproject.entity.HomeService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HomeServiceMapper {
    HomeServiceResponseDto toDto(HomeService homeService);
    HomeService toEntity(HomeServiceRequestDto dto);
    void updateEntityFromDto(HomeServiceRequestDto dto, @MappingTarget HomeService entity);
}
