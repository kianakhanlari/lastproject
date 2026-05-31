package ir.maktab.shortcut.fianlproject.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SpecialistResponseDto {

    private Long specialistId;

    private String fullName;

    private String email;

    private String expert;
}
