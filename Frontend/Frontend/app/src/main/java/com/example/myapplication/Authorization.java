package com.example.myapplication;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Authorization {
    public static String globalUserEmail;
    public static String globalPassword;

    public static String generateAuthToken() {
        // Create Base64 encoder
        Base64.Encoder encoder = Base64.getEncoder();
        // Create auth token
        Log.d("Authorization", encoder.encodeToString((globalUserEmail + ":" + globalPassword).getBytes(StandardCharsets.UTF_8)));
        return encoder.encodeToString((globalUserEmail + ":" + globalPassword).getBytes());
    }
}
