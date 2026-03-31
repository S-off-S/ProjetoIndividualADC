package com.example.projInd.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Getter
@Setter
@Entity
public class AuthToken {
    private static final long EXPIRATION_TIME = 1000*900; //15min

    @Id
    private String tokenId;
    private String userId;
    private Role role;
    private long issuedAt;
    private long expiresAt;

    public AuthToken() {

    }

    public AuthToken(String username, Role role) {
        this.userId = username;
        this.tokenId = UUID.randomUUID().toString();
        this.role = role;
        this.issuedAt = System.currentTimeMillis();
        this.expiresAt = this.issuedAt + EXPIRATION_TIME;
    }

}

