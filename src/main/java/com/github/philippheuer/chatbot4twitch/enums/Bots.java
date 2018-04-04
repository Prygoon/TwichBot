package com.github.philippheuer.chatbot4twitch.enums;

public enum Bots {
    NIGHTBOT("nightbot"),
    MOOBOT("moobot"),
    LILERINE("lilerine"),
    MARYATTIBOTE("maryattibote");

    private String nickname;

    public String getNickname() {
        return nickname;
    }

    Bots(String key) {
        this.nickname = key;
    }
}
