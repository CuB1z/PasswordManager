package com.cub1z.pwmanager.service;

import java.security.GeneralSecurityException;

public interface CryptoService {
    byte[] encrypt(char[] plainData, char[] password) throws GeneralSecurityException;
    char[] decrypt(byte[] encryptedData, char[] password) throws GeneralSecurityException;
}