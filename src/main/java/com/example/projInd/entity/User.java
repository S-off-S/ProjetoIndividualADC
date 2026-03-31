package com.example.projInd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.*;

@Entity(name = "users")
@Getter
@Setter
public class User {
    @Id
    @NotBlank
    @Email
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8)
    @NotBlank
    private String password;

    @Size(max = 9, min = 9)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String phone;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String address;
    private Role role;

    public User() {}

    public User(String username, String password, String phone, String address, Role role) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }
}
