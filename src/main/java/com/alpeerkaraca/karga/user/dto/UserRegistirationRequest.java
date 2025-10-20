package com.alpeerkaraca.karga.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistirationRequest(
        @NotBlank(message = "İsim boş olamaz.")
        @Size(min = 2, max = 64)
        String firstName,
        @NotBlank(message = "Soyisim boş olamaz.")
        @Size(min = 2, max = 64)
        String lastName,
        @NotBlank(message = "E-Posta boş olamaz.")
        @Size(min = 2, max = 256)
        @Email
        String email,
        @NotBlank(message = "Parola boş olamaz.")
        @Size(min = 8, max = 64, message = "Parola 8 - 64 karakter uzunluğu arasında olmalıdır.")
        String password,
        @NotBlank
        @Size(min = 13, max = 15, message = "Telefon numaranızda ülke kodu yer almalıdır.")
        String phoneNumber
){}
