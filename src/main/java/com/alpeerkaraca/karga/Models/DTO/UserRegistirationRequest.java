package com.alpeerkaraca.karga.Models.DTO;

public record UserRegistirationRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber
){}
