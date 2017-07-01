package com.github.philippheuer.chatbot4twitch.dbFeatures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class DB_Settings {

    private static String BASE_URL;
    private static String BASE_USERNAME;
    private static String BASE_PASS;

    static {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/home/appuser/DB_Settings"));
            BASE_URL = reader.readLine();
            BASE_USERNAME = reader.readLine();
            BASE_PASS = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static String getBaseUrl() {
        return BASE_URL;
    }

    static String getBaseUsername() {
        return BASE_USERNAME;
    }

    static String getBasePass() {
        return BASE_PASS;
    }
}
