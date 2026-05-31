package ir.maktab.shortcut.fianlproject.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor

public class HomeServiceResponseDto {

    private Long homeServiceId;

    private String nameService;

    private BigDecimal basePrice;

    private String description;

}
