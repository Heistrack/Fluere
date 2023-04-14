package com.example.final_project.infrastructure.userRepo;

import com.example.final_project.domain.users.FluereAppUser;
import org.apache.catalina.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Component
class InMemoryUserRepository implements UserRepository  {

    private final Map<String, FluereAppUser> storage = new HashMap<>();


    @Override
    public Optional<FluereAppUser> findByUserName(String userName) {
        return Optional.ofNullable(storage.get(userName));
    }

    @Override
    public FluereAppUser save(FluereAppUser fluereAppUser) {
        storage.put(fluereAppUser.getUsername(), fluereAppUser);
        return fluereAppUser;
    }

    @Override
    public boolean existsByEmailOrUserName(String email, String userName) {
        return storage.entrySet()
                .stream()
                .anyMatch(userAlreadyExistsPredicate(email, userName));
    }

    private static Predicate<Map.Entry<String, FluereAppUser>> userAlreadyExistsPredicate(String email, String name) {
        return entry -> Objects.equals(entry.getValue().email(), email) || Objects.equals(entry.getValue().userName(), name);
    }
}
