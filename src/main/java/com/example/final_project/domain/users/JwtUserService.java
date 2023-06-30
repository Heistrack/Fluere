package com.example.final_project.domain.users;

import com.example.final_project.api.requests.users.RegisterUserRequest;
import com.example.final_project.api.responses.UserDetailsResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

public interface JwtUserService {

    // TODO sprwadzic wykorzystanie

    public FluereAppUser findUserByUsername(String username);

    public BCryptPasswordEncoder getEncoder();

    public List<UserDetailsResponse> findAll();

    public FluereAppUser registerNewUser(RegisterUserRequest userRequestDto);

    public UserDetails findByUserNameOrEmail(String usernameOrEmail);

    public List<UserDetailsResponse> getAllUsers();

    public Optional<UserDetailsResponse> findAppUserByName(String name);

    public Optional<UserDetailsResponse> findAppUserByUserId(String userId);

    public FluereAppUser mapJwtToUser(org.springframework.security.oauth2.jwt.Jwt jwt);

}
