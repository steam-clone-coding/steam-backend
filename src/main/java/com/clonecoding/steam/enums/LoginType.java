package com.clonecoding.steam.enums;

public enum LoginType {
    NORMAL("normal"),
    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google"),
    DUMMY("dummy");

    private final String text;

    LoginType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static LoginType fromString(String text) {
        for (LoginType loginType : LoginType.values()) {
            if (loginType.text.equalsIgnoreCase(text)) {
                return loginType;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}