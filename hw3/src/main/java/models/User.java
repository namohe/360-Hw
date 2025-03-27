package models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and roles.
 */
public class User {
    private String userName;
    private String password;
    private Set<UserRole> roles;
    private LocalDate lastActiveDate; // To see how long an account was active for (Adir)

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.roles = new HashSet<>();
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Set<UserRole> getRoles() {
        return new HashSet<>(roles); // Return copy to prevent external modification
    }
    
    public void addRole(UserRole role) {
        this.roles.add(role);
    }
    
    public void removeRole(UserRole role) {
        this.roles.remove(role);
    }

    public boolean hasRole(UserRole role) {
        return roles.contains(role);
    }

    public String getRolesAsString() {
        return roles.stream()
                .map(UserRole::getValue)
                .collect(Collectors.joining(","));
    }

    public static Set<UserRole> getRolesFromString(String rolesStr) {
        Set<UserRole> roles = new HashSet<>();
        String[] roleArray = rolesStr.split(",");
        for (String role : roleArray) {
            roles.add(UserRole.fromString(role.trim()));
        }
        return roles;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    // Updates whenever an account is most recently active (Adir)
    public void updateLastActive() {
        this.lastActiveDate = LocalDate.now();
    }

    // Sees how long a user has been inactive for month-wise (Adir)
    public boolean isInactive(int months) {
        return lastActiveDate != null && lastActiveDate.isBefore(LocalDate.now().minus(months, ChronoUnit.MONTHS));
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }
}