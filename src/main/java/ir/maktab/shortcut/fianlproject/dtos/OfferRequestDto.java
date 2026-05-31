package ir.maktab.shortcut.fianlproject.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OfferRequestDto(
        BigDecimal offerPrice,
        LocalDateTime startWorkTime,
        Integer durationInHours
) {
}
