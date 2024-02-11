package com.example.final_project.infrastructure.userRepo;

import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.UserId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends MongoRepository<AppUser, UserId> {
    boolean existsAppUserByEmail(String email);

    Optional<AppUser> findAppUserByUsername(String username);

    boolean existsByEmailOrUsername(String email, String username);

    Optional<UserDetails> findUserDetailsByUsername(String username);

    Optional<AppUser> findFirstByEmail(String email);

    Optional<UserDetails> findUserDetailsByUsernameOrEmail(String username, String email);

    boolean existsAccountByUsername(String username);
}
