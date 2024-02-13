package com.example.final_project.infrastructure.userRepo;

import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.UserId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<AppUser, UserId> {
    boolean existsByEmail(String email);

    Optional<AppUser> findByLogin(String login);

    Optional<AppUser> findByEmail(String email);

    boolean existsByLogin(String login);

}
