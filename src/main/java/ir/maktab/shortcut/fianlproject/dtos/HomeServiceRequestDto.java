package ir.maktab.shortcut.fianlproject.dtos;

import java.math.BigDecimal;

public record HomeServiceRequestDto(
        String nameService,
        BigDecimal basePrice,
        String description

) {
}
