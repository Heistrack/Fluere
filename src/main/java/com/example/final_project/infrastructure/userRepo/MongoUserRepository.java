package com.example.final_project.infrastructure.userRepo;

import com.example.final_project.AppProfiles;
import com.example.final_project.domain.users.FluereAppUser;
import com.example.final_project.domain.users.UserId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
@Profile(AppProfiles.MONGO_SECURITY_SOURCE)
public interface MongoUserRepository extends MongoRepository<FluereAppUser, UserId>, UserRepository {
}
