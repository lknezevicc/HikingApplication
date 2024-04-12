package hr.application.entities;

import hr.application.enums.UserRole;
import hr.application.utilities.PasswordHashing;

import java.io.Serializable;

public class User implements Serializable {

    private Long ID;
    private String username;
    private String hashedPassword;
    private UserRole role;

    public User(String username, String password) {
        this.username = username;
        this.hashedPassword = PasswordHashing.hash(password);
        this.role = UserRole.USER;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String password) {
        this.hashedPassword = PasswordHashing.hash(password);
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    private User(Builder builder) {
        this.ID = builder.ID;
        this.username = builder.username;
        this.hashedPassword = builder.hashedPassword;
        this.role = builder.role;
    }

    public static class Builder {
        private Long ID;
        private String username;
        private String hashedPassword;
        private UserRole role;

        public Builder withID(Long ID) {
            this.ID = ID;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.hashedPassword = PasswordHashing.hash(password);
            return this;
        }

        public Builder withHashedPassword(String hashedPassword) {
            this.hashedPassword = hashedPassword;
            return this;
        }

        public Builder withRole(UserRole role) {
            this.role = role;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Override
    public String toString() {
        return "ID: " + ID + '\n' + "Username: " + username + '\n' + "Role: " + role;
    }

}
