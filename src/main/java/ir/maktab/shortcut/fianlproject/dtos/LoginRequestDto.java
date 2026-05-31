package ir.maktab.shortcut.fianlproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDto {

    private String email;
    private String password;

}
