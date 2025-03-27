package models;

/**
 * Allow user to choose a role 
 */
public enum UserRole {
    ADMIN("ADMIN"),
    STUDENT("STUDENT"),
    INSTRUCTOR("INSTRUCTOR"),
    STAFF("STAFF"),
    REVIEWER("REVIEWER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromString(String text) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No role with text " + text + " found");
    }
}
