package ir.maktab.shortcut.fianlproject.mapper;

import ir.maktab.shortcut.fianlproject.dtos.OrderRequestDto;
import ir.maktab.shortcut.fianlproject.dtos.response.OrderResponseDto;
import ir.maktab.shortcut.fianlproject.entity.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toEntity(OrderRequestDto orderRequestDto);
    List<OrderResponseDto> toListOrder(List<Order> listOrder);
}
