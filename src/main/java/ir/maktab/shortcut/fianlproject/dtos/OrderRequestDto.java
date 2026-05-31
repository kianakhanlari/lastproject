package ir.maktab.shortcut.fianlproject.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
public class OrderRequestDto {

    @NotNull
    private Long homeServiceId;

    @NotBlank
    private String description;

    @NotNull
    @PositiveOrZero
    private BigDecimal proposedPrice;

    @NotBlank
    private String address;
    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;


}
