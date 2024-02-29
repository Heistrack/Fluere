package com.example.final_project.domain.securities.jwt;

import java.security.KeyPair;

public interface KeySupplier {

    KeyPair getKeys();
}
