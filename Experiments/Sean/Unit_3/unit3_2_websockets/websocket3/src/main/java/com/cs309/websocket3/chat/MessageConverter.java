package com.cs309.websocket3.chat;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Converter
public class MessageConverter implements AttributeConverter<String, String> {

    private static final String SECRET_KEY = "VERYSECUREKEY123";
    private static final String ALGORITHM = "AES";

    @Override
    public String convertToDatabaseColumn(String s) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        byte[] encryptedBytes;
        try {
            encryptedBytes = cipher.doFinal(s.getBytes());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String convertToEntityAttribute(String s) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);

        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        byte[] decodedBytes = Base64.getDecoder().decode(s);
        try {
            return new String(cipher.doFinal(decodedBytes));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
