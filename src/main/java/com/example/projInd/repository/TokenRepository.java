package com.example.projInd.repository;

import com.example.projInd.entity.AuthToken;
import com.example.projInd.entity.User;
import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends DatastoreRepository<AuthToken, String> {
    void deleteByUserId(String sToken);
    List<AuthToken> findByUserId(String username);
    @Override
    @NonNull
    List<AuthToken> findAll();
}
