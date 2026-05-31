package ir.maktab.shortcut.fianlproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class CustomerRequestDto {
    private String fullName;
    private String email;
    private String password;
}
