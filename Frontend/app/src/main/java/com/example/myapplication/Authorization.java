package com.example.myapplication;

import java.util.Base64;

public class Authorization {
    public static String globalUserEmail;
    public static String globalPassword;

    public static String generateAuthToken() {
        // Create Base64 encoder
        Base64.Encoder encoder = Base64.getEncoder();
        // Create auth token
        return encoder.encodeToString((globalUserEmail + ":" + globalPassword).getBytes());
    }
}
