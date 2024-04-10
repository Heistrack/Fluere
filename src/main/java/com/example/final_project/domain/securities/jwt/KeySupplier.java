package com.example.final_project.domain.securities.jwt;

import javax.crypto.SecretKey;

//TODO change keyInterface supplier to Key pair, remove get Keys
public interface KeySupplier {

    //    KeyPair getKeys();
    SecretKey getKey();
}
