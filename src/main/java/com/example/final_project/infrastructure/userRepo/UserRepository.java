package com.example.final_project.infrastructure.userRepo;

import com.example.final_project.domain.users.FluereAppUser;
import com.example.final_project.domain.users.UserId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<FluereAppUser, UserId> {

    Optional<FluereAppUser> findByUserName(String userName);

    boolean existsByEmailOrUserName(String email, String userName);
}
