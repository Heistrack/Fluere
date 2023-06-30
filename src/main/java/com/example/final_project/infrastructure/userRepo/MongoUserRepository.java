package com.example.final_project.infrastructure.userRepo;

import com.example.final_project.domain.users.FluereAppUser;
import com.example.final_project.domain.users.UserId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface MongoUserRepository extends MongoRepository<FluereAppUser, UserId> {
    boolean existsAccountByEmail(String email);

    Optional<FluereAppUser> findAppUserByEmail(String email);

    Optional<FluereAppUser> findByUserName(String userName);

    boolean existsByEmailOrUserName(String email, String userName);

    UserDetails findUserDetailsByUserName(String username);

    Optional<FluereAppUser> findAppUserByUserName(String name);

    Optional<FluereAppUser> findAppUserByUserId(UserId userId);


    Optional<UserDetails> findUserDetailsByUserNameOrEmail(String usernameOrEmail, String usernameOrEmail1);

    boolean existsAccountByUserName(String name);
}
