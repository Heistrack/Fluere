package com.example.final_project.api.requests.users;

import jakarta.validation.constraints.Email;

public record EmailChangeRequest(AuthenticationRequest auth, @Email String newEmail) {
}
