package com.example.final_project.infrastructure.appuserrepo;

import com.example.final_project.domain.users.appusers.AppUser;
import com.example.final_project.domain.users.appusers.UserIdWrapper;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AppUserRepository extends MongoRepository<AppUser, UserIdWrapper> {
    boolean existsByEmail(String email);

    Optional<AppUser> findByLogin(String login);

    Optional<AppUser> findByEmail(String email);

    boolean existsByLogin(String login);
}
