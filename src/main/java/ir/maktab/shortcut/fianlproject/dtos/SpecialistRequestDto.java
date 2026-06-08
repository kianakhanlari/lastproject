package ir.maktab.shortcut.fianlproject.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record   SpecialistRequestDto(

        @NotBlank(groups = ValidationGroups.OnCreate.class)
        String fullName,

        byte[]  photo,

        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
                message = "Password must contain both letters and numbers"
        )
        String password,

        @NotBlank
        @Email(message = "Invalid email format")
        String email,

        @NotBlank
        String expert
) { }
