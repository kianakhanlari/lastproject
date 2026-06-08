package ir.maktab.shortcut.fianlproject.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public record OfferDto(
        @NotNull
        @PositiveOrZero
         BigDecimal proposedPrice,

        @NotNull
        @Future
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startWorkTime,

       Duration durationInHours

) {
}
