package com.example.final_project.infrastructure.userRepo;

import com.example.final_project.domain.users.AppUser;
import com.example.final_project.domain.users.UserId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends MongoRepository<AppUser, UserId> {
    boolean existsByEmail(String email);
//TODO HOW methods in this repo should work?!
    Optional<AppUser> findFirstByLogin(String login);

    boolean existsByEmailOrLogin(String email, String login); //TODO IT'S FINE METHOD

    Optional<UserDetails> findUserDetailsByLogin(String login);

    Optional<AppUser> findFirstByEmail(String email);

    Optional<UserDetails> findUserDetailsByLoginOrEmail(String login, String email);

    boolean existsAccountByLogin(String login);
}
