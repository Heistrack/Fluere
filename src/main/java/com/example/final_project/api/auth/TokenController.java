package com.example.final_project.api.auth;

import com.example.final_project.domain.tokens.TokenService;
import com.example.final_project.domain.users.WrongCredentialsException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {

        this.tokenService = tokenService;
    }


    @PostMapping("/token")
    public ResponseEntity<Token> token(Authentication authentication) throws WrongCredentialsException {
        return ResponseEntity.ok().body(tokenService.getToken(authentication));
    }

}
