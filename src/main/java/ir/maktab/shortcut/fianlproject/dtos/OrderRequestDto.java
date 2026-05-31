package ir.maktab.shortcut.fianlproject.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
public class OrderRequestDto {

    private String description;
    private BigDecimal priceOffer;
    private String Address;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;
    private Long serviceId;

}
