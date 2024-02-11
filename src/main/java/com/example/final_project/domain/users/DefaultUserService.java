package com.example.final_project.domain.users;

import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import com.example.final_project.infrastructure.userRepo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultUserService {

    private final UserRepository userRepository;
    private final Supplier<UserId> userIdSupplier;
    private final PasswordEncoder passwordEncoder;

    public AppUser findUserByUsername(String username) {
        return userRepository.findAppUserByUsername(username)
                             .orElseThrow();
    }

    public List<UserDetailsResponse> findAll() {
        return userRepository.findAll()
                             .stream()
                             .map(UserDetailsResponse::fromDomain)
                             .collect(Collectors.toList());
    }

    public AppUser registerNewUser(RegisterUserRequest registerUserRequest) {
        if (userRepository.existsAppUserByEmail(registerUserRequest.email())) {
            throw new UnableToRegisterException("Użytkownik z podanym e-mailem już istnieje");
        }
        if (userRepository.existsAccountByUsername(registerUserRequest.name())) {
            throw new UnableToRegisterException("Użytkownik z podaną nazwą już istnieje");
        }
        return userRepository.save(new AppUser(
                userIdSupplier.get(),
                registerUserRequest.name(),
                registerUserRequest.email(),
                passwordEncoder.encode(registerUserRequest.password()),
                Role.USER,
                true
        ));
    }

    public UserDetails findByUserNameOrEmail(String usernameOrEmail) {
        return userRepository.findUserDetailsByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                             .orElseThrow(() -> new WrongCredentialsException("Niepoprawne dane logowania"));
    }

    public List<UserDetailsResponse> getAllUsers() {
        return userRepository.findAll()
                             .stream()
                             .map(UserDetailsResponse::fromDomain)
                             .toList();
    }

    public Optional<UserDetailsResponse> findAppUserByName(String name) {
        return Optional.of(userRepository.findAppUserByUsername(name)
                                         .map(UserDetailsResponse::fromDomain))
                       .get();
    }

    public Optional<UserDetailsResponse> findAppUserByUserId(String userId) {
        return userRepository.findById(UserId.newId(userIdSupplier.get().userId()))
                             .map(UserDetailsResponse::fromDomain);
    }
}
