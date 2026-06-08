package ir.maktab.shortcut.fianlproject.mapper;

import ir.maktab.shortcut.fianlproject.dtos.OfferDto;
import ir.maktab.shortcut.fianlproject.dtos.response.OfferResponceDto;
import ir.maktab.shortcut.fianlproject.entity.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OfferMapper {



    @Mapping(target = "appointmentTime", source = "startWorkTime")
    @Mapping(target = "workDuration", source = "durationInHours")
    Offer toEntity(OfferDto dto);

    List<OfferResponceDto> toDto(List<Offer> offer);
}
