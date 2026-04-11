package com.example.bankcards.service;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CardEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    @Value("${app.card-encryption.key}")
    private String encryptionKey;

    private SecretKey secretKey;

    @PostConstruct
    public void init(){
        byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
        if(keyBytes.length!=16 && keyBytes.length !=24 && keyBytes.length !=32){
            throw new IllegalArgumentException("card encryption key must be 16, 24, or 32 bytes long");
        }
        this.secretKey = new SecretKeySpec(keyBytes,"AES");
    }

    public String encrypt(String value){
        try{
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH,iv);
            cipher.init(Cipher.ENCRYPT_MODE,secretKey,parameterSpec);

            byte[]encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            byte[]combined  = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv,0,combined,0,iv.length);
            System.arraycopy(encryptedBytes,0,combined,iv.length, encryptedBytes.length);
            return Base64.getEncoder().encodeToString(combined);
        }catch (Exception e){
            throw new IllegalArgumentException("failed to encrypt card number");
        }
    }
    public String decrypt(String encryptedValue) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedValue);

            byte[] iv = new byte[IV_LENGTH];
            byte[] encryptedBytes = new byte[combined.length - IV_LENGTH];

            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("failed to decrypt card number");
        }
    }

}
