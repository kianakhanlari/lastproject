package ir.maktab.shortcut.fianlproject.mapper;

import ir.maktab.shortcut.fianlproject.dtos.HomeServiceRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.HomeServiceDetailsResponseDto;
import ir.maktab.shortcut.fianlproject.entity.HomeService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HomeServiceMapper {
    HomeServiceDetailsResponseDto toDto(HomeService homeService);
    HomeService toEntity(HomeServiceRequestDto dto);
    void updateEntityFromDto(HomeServiceRequestDto dto, @MappingTarget HomeService entity);
}
