package hr.application.enums;

public enum RegexEnums {
    VALID_USERNAME("^[\\p{L}\\p{N}]{4,16}$"),
    VALID_PASSWORD("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[\\p{L}\\p{N}\\p{P}]+$");

    private final String regex;
    RegexEnums(String regex) {
        this.regex = regex;
    }
    public boolean matches(String input) {
        return input.matches(regex);
    }
}
