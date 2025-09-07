package com.labaway.integration.enums;

public enum Language {
    EN, DE, BG;
    public static Language fromString(String lang) {
        try {
            return Language.valueOf(lang.replace("-", "_").toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return EN;
        }
    }
}
