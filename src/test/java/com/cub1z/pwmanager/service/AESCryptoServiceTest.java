package com.cub1z.pwmanager.service;

public class AESCryptoServiceTest extends AbstractCryptoServiceTest {

    @Override
    protected CryptoService getCryptoService() {
        return new AESCryptoService();
    }
}