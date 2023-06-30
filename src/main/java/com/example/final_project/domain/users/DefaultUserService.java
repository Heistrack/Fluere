package com.example.final_project.domain.users;

import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.infrastructure.userRepo.MongoUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class DefaultUserService {

    private final MongoUserRepository userRepository;
    private final Supplier<UserId> userIdSupplier;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public DefaultUserService(MongoUserRepository userRepository, Supplier<UserId> userIdSupplier) {
        this.userRepository = userRepository;
        this.userIdSupplier = userIdSupplier;
    }


    public FluereAppUser findUserByUsername(String username) {
        return userRepository.findByUserName(username).orElseThrow();
    }

    public BCryptPasswordEncoder getEncoder() {
        return encoder;
    }

    public List<UserDetailsResponse> findAll() {
        return userRepository.findAll().stream().map(UserDetailsResponse::fromDomain).collect(Collectors.toList());
    }

    public FluereAppUser registerNewUser(RegisterUserRequest registerUserRequest) {
        if (userRepository.existsAccountByEmail(registerUserRequest.email())) {
            throw new UnableToRegisterException("Użytkownik z podanym e-mailem już istnieje");
        }
        if (userRepository.existsAccountByUserName(registerUserRequest.name())) {
            throw new UnableToRegisterException("Użytkownik z podaną nazwą już istnieje");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return userRepository.save(new FluereAppUser(userIdSupplier.get(),
                registerUserRequest.name(),
                registerUserRequest.email(),
                encoder.encode(registerUserRequest.password()),
                new SimpleGrantedAuthority("USER"),
                true));
    }

    public UserDetails findByUserNameOrEmail(String usernameOrEmail) {
        return userRepository.findUserDetailsByUserNameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new WrongCredentialsException("Niepoprawne dane logowania"));
    }

    public List<UserDetailsResponse> getAllUsers() {
        return userRepository.findAll().stream().map(UserDetailsResponse::fromDomain).toList();
    }

    public Optional<UserDetailsResponse> findAppUserByName(String name) {
        return Optional.of(userRepository.findAppUserByUserName(name).map(UserDetailsResponse::fromDomain)).get();
    }

    public Optional<UserDetailsResponse> findAppUserByUserId(String userId) {
        return userRepository.findAppUserByUserId(UserId.newId(userId)).map(UserDetailsResponse::fromDomain);
    }
}
