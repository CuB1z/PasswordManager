package com.cub1z.pwmanager;

import com.cub1z.pwmanager.service.AESCryptoService;
import com.cub1z.pwmanager.service.MasterPasswordService;
import com.cub1z.pwmanager.service.PasswordEntryService;

public class Main {
    public static void main(String[] args) {
        PasswordManagerUI passwordManagerUI = new PasswordManagerUI(
            new PasswordManager(
                new AESCryptoService(),
                new PasswordEntryService(),
                new MasterPasswordService()
            )
        );

        passwordManagerUI.run();
    }
}