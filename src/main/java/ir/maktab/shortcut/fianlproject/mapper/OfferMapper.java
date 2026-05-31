package ir.maktab.shortcut.fianlproject.mapper;

import ir.maktab.shortcut.fianlproject.dtos.OfferRequestDto;
import ir.maktab.shortcut.fianlproject.entity.Offer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OfferMapper {
    Offer toEntity( OfferRequestDto dto);
}
