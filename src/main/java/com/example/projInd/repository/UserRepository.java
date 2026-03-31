package com.example.projInd.repository;


import com.example.projInd.entity.User;
import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends DatastoreRepository<User, String> { //sec parameter must match Id
}
