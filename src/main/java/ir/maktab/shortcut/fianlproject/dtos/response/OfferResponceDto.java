package ir.maktab.shortcut.fianlproject.dtos.response;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public record OfferResponceDto(

        BigDecimal proposedPrice,


        LocalDateTime appointmentTime,

        Duration workDuration,

        Long orderId) {
}
