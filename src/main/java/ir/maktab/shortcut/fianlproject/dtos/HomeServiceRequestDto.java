package ir.maktab.shortcut.fianlproject.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record HomeServiceRequestDto(

        @NotBlank(message = "Service name must not be blank")

        String name,

        @NotNull(message = "Base price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than zero")
        BigDecimal basePrice,

        @NotBlank(message = "Description must not be blank")
        String description,

        Long parentId

) {
}