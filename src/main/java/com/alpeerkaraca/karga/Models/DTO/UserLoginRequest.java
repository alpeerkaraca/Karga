package com.alpeerkaraca.karga.Models.DTO;

public record UserLoginRequest(
        String email,
        String password
) {
}
