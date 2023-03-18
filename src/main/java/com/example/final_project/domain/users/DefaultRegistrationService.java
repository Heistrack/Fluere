package com.example.final_project.domain.users;

import com.example.final_project.infrastructure.userRepo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class DefaultRegistrationService implements UserRegistrationService{
    private final UserRepository userRepository;
    private final Supplier<UserId> userIdSupplier;
    private final PasswordEncoder encoder;

    public DefaultRegistrationService(UserRepository userRepository, Supplier<UserId> userIdSupplier, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.userIdSupplier = userIdSupplier;
        this.encoder = encoder;
    }




    @Override
    public FluereAppUser registerNewUser(String username, String rawPassword, String email, List<String> roles) {

        if (userRepository.existsByEmailOrUserName(email, username)){
            throw new UnableToRegisterException();
        }
        FluereAppUser fluereAppUser = new FluereAppUser(userIdSupplier.get(), username, email, encoder.encode(rawPassword), roles, true);
        return userRepository.save(fluereAppUser);
    }
}
