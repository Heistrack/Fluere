package com.example.final_project.userentity.repository;

import com.example.final_project.userentity.model.AppUser;
import com.example.final_project.userentity.model.UserIdWrapper;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends MongoRepository<AppUser, UserIdWrapper> {
    boolean existsByEmail(String email);

    Optional<AppUser> findByLogin(String login);

    Optional<AppUser> findByEmail(String email);

    boolean existsByLogin(String login);
}
