package com.example.projInd.DTO;

import com.example.projInd.entity.AuthToken;
import com.example.projInd.entity.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public class UserDTOS {
    public record InputUser(@Email @NotBlank String username,
                            @NotBlank @Size(min = 8) String password,
                            @NotBlank String confirmation,
                            @Size(max = 9, min = 9) String phone,
                            String address,
                            @NotNull Role role) {}

    public record InputPassword(@Email @NotBlank String username,
                                @NotBlank @Size(min = 8) String oldPassword,
                                @NotBlank @Size(min = 8) String newPassword) {}

    public record InputRole(@Email @NotBlank String username, @NotNull Role newRole) {}
    public record InputLogin(@Email @NotBlank String username, @NotBlank @Size(min = 8) String password) {}
    public record InputAttributes(@Email @NotBlank String username, Map<String, Object> attributes) {}
    public record InputUsername(@Email @NotBlank String username) {}
    public record OutputToken(String tokenId, String userId, Role role, long expiresAt) {}
    public record Response<T>(String status, T data) {}
    public record Request<T>(@Valid T input, AuthToken token) {}
}

