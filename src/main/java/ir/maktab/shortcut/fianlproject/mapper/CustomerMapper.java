package ir.maktab.shortcut.fianlproject.mapper;

import ir.maktab.shortcut.fianlproject.dtos.CustomerRequestDto;
import ir.maktab.shortcut.fianlproject.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerRequestDto toDto(Customer customer);

    Customer toEntity(CustomerRequestDto dto);

    void updateEntityFromDto( CustomerRequestDto dto,
                             @MappingTarget Customer customer);


}
